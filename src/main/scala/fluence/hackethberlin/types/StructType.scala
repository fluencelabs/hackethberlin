package fluence.hackethberlin.types

import shapeless.{HList, LUBConstraint}

class StructType[D <: HList](dataDef: D)(implicit dv: DataVyper[D], c: LUBConstraint[D, (String, PlainType)]) extends PlainType {
  override def toVyper: String = s"{${dv.toVyperDefinitions(dataDef).mkString(", ")}}"
}
