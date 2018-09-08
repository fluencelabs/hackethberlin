package fluence.hackethberlin.types

import fluence.hackethberlin.Expr
import shapeless.{HList, Witness}
import shapeless.ops.record.Selector

class StructType[D <: HList](dataDef: D)(implicit dv: DataVyper[D]) extends Type {

  def ref[T <: Symbol, V <: Type](k: Witness.Aux[T])(implicit selector : Selector.Aux[D, T, V]): Expr.Ref[V] =
    Expr.Ref[V](k.value.name)

  override def toVyper: String =
    s"{${dv.toVyperDefinitions(dataDef).mkString(", ")}}"
}
