package fluence.hackethberlin

import fluence.hackethberlin.types.{DataVyper, StructType}
import shapeless._

class FuncDef[Args <: HList: DataVyper, Ret <: types.Type](
  name: String,
  argsDef: Args,
  ret: Option[Ret],
  body: StructType[Args] ⇒ Expr[Ret],
  decorators: Set[Decorator] = Set.empty
) {

  def toVyper: String =
    s"${decorators.map(_.toVyper).mkString("\n")}\ndef $name(${DataVyper[Args]
      .mkString(argsDef, ", ")})${ret.fold("")(" -> " + _.toVyper)}:\n${body(new StructType[Args](argsDef)).toVyper(1)};\n"

  def @:(decorator: Decorator): FuncDef[Args, Ret] =
    new FuncDef[Args, Ret](name, argsDef, ret, body, decorators + decorator)
}

object FuncDef {

  def apply[Args <: HList: DataVyper, Ret <: types.Type](
    name: String,
    argsDef: Args,
    ret: Ret
  )(body: StructType[Args] ⇒ Expr.Return[Ret]): FuncDef[Args, Ret] =
    new FuncDef(name, argsDef, Some(ret), body)

  def apply[Args <: HList: DataVyper](
    name: String,
    argsDef: Args
  )(body: StructType[Args] ⇒ Expr[types.Void]): FuncDef[Args, types.Void] =
    new FuncDef(name, argsDef, None, body)

}
