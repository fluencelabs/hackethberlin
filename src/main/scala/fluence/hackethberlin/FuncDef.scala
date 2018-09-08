package fluence.hackethberlin

import fluence.hackethberlin.types.DataVyper
import shapeless._

class FuncDef[Args <: HList: DataVyper](
  name: String,
  argsDef: Args,
  decorators: Set[Decorator] = Set.empty
) {

  def toVyper: String =
    s"${decorators.map(_.toVyper).mkString("\n")}\ndef $name(${DataVyper[Args].mkString(argsDef, ", ")}):\n   body;\n"

  def @:(decorator: Decorator): FuncDef[Args] =
    new FuncDef[Args](name, argsDef, decorators + decorator)
}
