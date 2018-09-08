package fluence.hackethberlin.types

import fluence.hackethberlin.Expr
import shapeless.{HList, Witness}
import shapeless.ops.record.Selector

class StructType[D <: HList](dataDef: D)(implicit dv: DataVyper[D]) extends Type {
  def get(k: Witness)(implicit selector : Selector[D, k.T], ev: k.T <:< Type): Expr.Ref[k.T] =
    Expr.Ref[k.T]("field should be there")

  override def toVyper: String = s"{${dv.toVyperDefinitions(dataDef).mkString(", ")}}"
}
