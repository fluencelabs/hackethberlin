package fluence.hackethberlin.types

import shapeless.{HList, LUBConstraint}

class StructType[D <: HList](name: String, dataDef: D)(implicit dv: DataVyper[D], c: LUBConstraint[D, (String, PlainType)]) extends PlainType {
  override def toVyper: String = s"$name: {${dv.toVyperDefinitions(dataDef).mkString(", ")}}"
}
