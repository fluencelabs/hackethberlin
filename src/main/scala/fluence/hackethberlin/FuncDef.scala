package fluence.hackethberlin

import cats.Monad
import fluence.hackethberlin.types.{DataVyper, ProductType}
import shapeless._
import cats.free.Free
import cats.syntax.functor._

class FuncDef[Args <: HList, Ret <: types.Type](
  name: String,
  argsDef: ProductType[Args],
  ret: Option[Ret],
  body: ProductType[Args] ⇒ Free[Expr, Ret],
  decorators: Set[Decorator] = Set.empty
) {

  def bodyVyper: String =
    body(argsDef).foldMap(CodeChunk.fromExpr).run._1.toVyper(1)

  def toVyper: String =
    s"${decorators.map(_.toVyper).mkString("\n")}\ndef $name(${argsDef.toArgsVyper})${ret.fold("")(" -> " + _.toVyper)}:\n$bodyVyper\n"

  def @:(decorator: Decorator): FuncDef[Args, Ret] =
    new FuncDef[Args, Ret](name, argsDef, ret, body, decorators + decorator)
}

object FuncDef {

  def apply[Args <: HList: DataVyper, Ret <: types.Type](
    name: String,
    argsDef: Args,
    ret: Ret
  )(body: ProductType[Args] ⇒ Free[Expr, Ret]): FuncDef[Args, Ret] =
    new FuncDef(name, ProductType(argsDef), Some(ret), body)

  def apply[Args <: HList: DataVyper](
    name: String,
    argsDef: Args
  )(body: ProductType[Args] ⇒ Free[Expr, Unit]): FuncDef[Args, types.Void] =
    new FuncDef(name, ProductType(argsDef), None, args ⇒ body(args).map(_ ⇒ types.Void))

}
