package fluence.hackethberlin

import fluence.hackethberlin.types.{Call, DataVyper, ProductType}
import shapeless._
import BasisConstraint._
import cats.free.Free
import shapeless.ops.hlist.{Mapped, ToList}
import shapeless.ops.record.{Keys, Selector, Values}
import shapeless.tag.@@

class FuncDef[Args <: HList, Ret <: types.Type, Params <: HList](
  name: String,
  val argsDef: ProductType[Args],
  ret: Ret,
  body: ProductType[Args] ⇒ Free[Expr, Ret],
  decorators: Set[Decorator] = Set.empty
) {

  def bodyVyper: String =
    body(argsDef).foldMap(CodeChunk.fromExpr).run._1.toVyper(1)

  def toVyper: String =
    s"${decorators.map(_.toVyper).mkString("\n")}\ndef $name(${argsDef.toArgsVyper})${Option(ret)
      .filter(_ != types.Void)
      .fold("")(" -> " + _.toVyper)}:\n$bodyVyper\n"

  def @:(decorator: Decorator): FuncDef[Args, Ret, Params] =
    new FuncDef[Args, Ret, Params](name, argsDef, ret, body, decorators + decorator)

  def apply(params: Params)(implicit toList: ops.hlist.ToList[Params, Expr.ToInlineVyper]): InlineExpr[Ret] =
    Expr.Ref(toList.apply(params).map(_.toInlineVyper).mkString(s"$name(", ", ", ")"), ret)
}

object FuncDef {

  def apply[Args <: HList: DataVyper, Ret <: types.Type, _Values <: HList](
    name: String,
    argsDef: Args,
    ret: Ret
  )(body: ProductType[Args] ⇒ Free[Expr, Ret])(
    implicit values: ops.record.Values.Aux[Args, _Values],
    mapped: ops.hlist.Mapped[_Values, InlineExpr]
  ): FuncDef[Args, Ret, mapped.Out] =
    new FuncDef(name, ProductType(argsDef), ret, body)

  def apply[Args <: HList: DataVyper, _Values <: HList](
    name: String,
    argsDef: Args
  )(
    body: ProductType[Args] ⇒ Free[Expr, Unit],
    mapped: ops.hlist.Mapped[_Values, InlineExpr]
  )(implicit values: ops.record.Values.Aux[Args, _Values]): FuncDef[Args, types.Void, mapped.Out] =
    new FuncDef(name, ProductType(argsDef), types.Void, args ⇒ body(args).map(_ ⇒ types.Void))

}
