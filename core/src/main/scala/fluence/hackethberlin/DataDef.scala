package fluence.hackethberlin

import fluence.hackethberlin.types.DataVyper
import shapeless.HList

class DataDef[D <: HList](dataDef: D)(implicit dv: DataVyper[D]) {
  def toVyper: String = dv.toVyperDefinitions(dataDef).mkString("\n")
}
