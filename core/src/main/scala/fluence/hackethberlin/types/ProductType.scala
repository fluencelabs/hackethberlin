package fluence.hackethberlin.types

import cats.free.Free
import fluence.hackethberlin.{Expr, FuncDef, InlineExpr}
import shapeless._
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

  def funcDef[Ret <: Type, _Values <: HList](name: String, ret: Ret)(
    body: ProductType[D] ⇒ Free[Expr, Ret]
  )(
    implicit values: ops.record.Values.Aux[D, _Values],
    mapped: ops.hlist.Mapped[_Values, InlineExpr]
  ): FuncDef[D, Ret, mapped.Out] =
    new FuncDef[D, Ret, mapped.Out](name, this, ret, body)

}

object ProductType {

  def apply[D <: HList](dataDef: D)(implicit dv: DataVyper[D]): ProductType[D] =
    new ProductType[D](dataDef, dv)
}
