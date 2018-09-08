package fluence.hackethberlin

import fluence.hackethberlin.types.DataVyper
import shapeless._

class FuncDef[Args <: HList: DataVyper, Ret <: types.Type](
  name: String,
  argsDef: Args,
  ret: Option[Ret],
  decorators: Set[Decorator] = Set.empty
) {

  def toVyper: String =
    s"${decorators.map(_.toVyper).mkString("\n")}\ndef $name(${DataVyper[Args].mkString(argsDef, ", ")})${ret.fold("")(" -> "+_.toVyper)}:\n   body;\n"

  def @:(decorator: Decorator): FuncDef[Args, Ret] =
    new FuncDef[Args, Ret](name, argsDef, ret, decorators + decorator)
}

object FuncDef {
  def apply[Args <: HList : DataVyper, Ret <: types.Type](
                                                           name: String,
                                                           argsDef: Args,
                                                           ret: Ret
                                                         ): FuncDef[Args, Ret] =
    new FuncDef(name, argsDef, Some(ret))


  def apply[Args <: HList : DataVyper](
                                                           name: String,
                                                           argsDef: Args
                                                         ): FuncDef[Args, types.Void] =
    new FuncDef(name, argsDef, None)

}