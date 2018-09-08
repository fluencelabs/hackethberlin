package fluence.hackethberlin.types

import cats.free.Free
import fluence.hackethberlin.{Expr, FuncDef}
import shapeless.{HList, Witness}
import shapeless.ops.record.Selector

class ProductType[D <: HList](dataDef: D, dv: DataVyper[D]) extends Type {

  def ref[T <: Symbol, V <: Type](k: Witness.Aux[T])(implicit selector: Selector.Aux[D, T, V]): Expr.Ref[V] =
    Expr.Ref[V](k.value.name, selector(dataDef))

  // type in type definition somewhere
  override def toVyper: String =
    s"{${dv.toVyperDefinitions(dataDef).mkString(", ")}}"

  // contract data definition
  def toDataVyper: String =
    dv.toVyperDefinitions(dataDef).mkString("\n")

  // function arguments
  def toArgsVyper: String =
    dv.toVyperDefinitions(dataDef).mkString(", ")

  def funcDef[Ret <: Type](name: String, ret: Ret)(body: ProductType[D] ⇒ Free[Expr, Ret]): FuncDef[D, Ret] =
    new FuncDef[D, Ret](name, this, Some(ret), body)

}

object ProductType {

  def apply[D <: HList](dataDef: D)(implicit dv: DataVyper[D]): ProductType[D] =
    new ProductType[D](dataDef, dv)
}
