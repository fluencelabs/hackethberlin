package fluence.hackethberlin.types

import shapeless.{HList, LUBConstraint}

class StructType[D <: HList](dataDef: D)(implicit dv: DataVyper[D], c: LUBConstraint[D, (String, Type)]) extends Type {
  override def toVyper: String = s"{${dv.toVyperDefinitions(dataDef).mkString(", ")}}"
}
