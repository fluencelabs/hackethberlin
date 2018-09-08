package fluence.hackethberlin

import fluence.hackethberlin.types.DataVyper
import shapeless.HList

class Contract[D <: HList](instructions: D)(implicit dv: DataVyper[D]) {
  def toVyper: String =
    dv.toVyperDefinitions(instructions).mkString("\n")
}
