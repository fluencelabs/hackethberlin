package fluence.hackethberlin

import fluence.hackethberlin.types.{DataVyper, ProductType}
import shapeless._

class FuncDef[Args <: HList, Ret <: types.Type](
                                                            name: String,
                                                            argsDef: ProductType[Args],
                                                            ret: Option[Ret],
                                                            body: ProductType[Args] ⇒ Expr[Ret],
                                                            decorators: Set[Decorator] = Set.empty
) {

  def toVyper: String =
    s"${decorators.map(_.toVyper).mkString("\n")}\ndef $name(${argsDef.toArgsVyper})${ret.fold("")(" -> " + _.toVyper)}:\n${body(argsDef).toVyper(1)};\n"

  def @:(decorator: Decorator): FuncDef[Args, Ret] =
    new FuncDef[Args, Ret](name, argsDef, ret, body, decorators + decorator)
}

object FuncDef {

  def apply[Args <: HList: DataVyper, Ret <: types.Type](
    name: String,
    argsDef: Args,
    ret: Ret
  )(body: ProductType[Args] ⇒ Expr.Return[Ret]): FuncDef[Args, Ret] =
    new FuncDef(name, ProductType(argsDef), Some(ret), body)

  def apply[Args <: HList: DataVyper](
    name: String,
    argsDef: Args
  )(body: ProductType[Args] ⇒ Expr[types.Void]): FuncDef[Args, types.Void] =
    new FuncDef(name, ProductType(argsDef), None, body)

}
