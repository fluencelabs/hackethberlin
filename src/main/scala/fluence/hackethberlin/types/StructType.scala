package fluence.hackethberlin.types

import shapeless.HList

class StructType[D <: HList](dataDef: D)(implicit dv: DataVyper[D]) extends Type {
  override def toVyper: String = s"{${dv.toVyperDefinitions(dataDef).mkString(", ")}}"
}
