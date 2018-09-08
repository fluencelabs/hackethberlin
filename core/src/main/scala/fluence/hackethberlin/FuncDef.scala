package fluence.hackethberlin

import fluence.hackethberlin.types.{DataVyper, ProductType}
import shapeless._
import cats.free.Free

class FuncDef[Args <: HList, Ret <: types.Type](
  name: String,
  argsDef: ProductType[Args],
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

  def @:(decorator: Decorator): FuncDef[Args, Ret] =
    new FuncDef[Args, Ret](name, argsDef, ret, body, decorators + decorator)

  //def apply[Params <: HList](params: Params)(implicit a: LUBConstraint s: ops.hlist.Mapped[Args, InlineExpr]): InlineExpr[Ret] = ???
}

object FuncDef {

  def apply[Args <: HList: DataVyper, Ret <: types.Type](
    name: String,
    argsDef: Args,
    ret: Ret
  )(body: ProductType[Args] ⇒ Free[Expr, Ret]): FuncDef[Args, Ret] =
    new FuncDef(name, ProductType(argsDef), ret, body)

  def apply[Args <: HList: DataVyper](
    name: String,
    argsDef: Args
  )(body: ProductType[Args] ⇒ Free[Expr, Unit]): FuncDef[Args, types.Void] =
    new FuncDef(name, ProductType(argsDef), types.Void, args ⇒ body(args).map(_ ⇒ types.Void))

}
