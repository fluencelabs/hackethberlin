package fluence.hackethberlin.types

import fluence.hackethberlin.{types, FuncDef}
import shapeless._
import shapeless.labelled.FieldType
import shapeless.tag._

sealed trait DataVyper[T] {
  def toVyperDefinitions(data: T): List[String]

  def mkString(data: T, sep: String): String =
    toVyperDefinitions(data).mkString(sep)
}

sealed trait LowPriorityDataVyperImplicits {

  implicit object hnilDataVyper extends DataVyper[HNil] {
    override def toVyperDefinitions(data: HNil): List[String] = Nil
  }

  implicit def hlistDataVyper[H, T <: HList](implicit dh: DataVyper[H], dt: DataVyper[T]): DataVyper[H :: T] =
    new DataVyper[H :: T] {
      override def toVyperDefinitions(data: H :: T): List[String] =
        dh.toVyperDefinitions(data.head) ::: dt.toVyperDefinitions(data.tail)
    }

  implicit def recDataVyper[K <: Symbol, V <: Type](implicit wk: Witness.Aux[K]): DataVyper[FieldType[K, V]] =
    new DataVyper[FieldType[K, V]] {
      override def toVyperDefinitions(data: FieldType[K, V]): List[String] =
        s"${wk.value.name}: ${data.toVyper}" :: Nil
    }

  implicit def productTypeDataVyper[D <: HList]: DataVyper[ProductType[D]] =
    new DataVyper[ProductType[D]] {
      override def toVyperDefinitions(data: ProductType[D]): List[String] =
        data.toDataVyper :: Nil
    }

  implicit def funcDefDataVyper[Args <: HList, Ret <: types.Type, Params <: HList]
    : DataVyper[FuncDef[Args, Ret, Params]] =
    new DataVyper[FuncDef[Args, Ret, Params]] {
      override def toVyperDefinitions(func: FuncDef[Args, Ret, Params]): List[String] =
        func.toVyper :: Nil
    }

}

object DataVyper extends LowPriorityDataVyperImplicits {

  def apply[T](implicit dataVyper: DataVyper[T]): DataVyper[T] = dataVyper

  implicit def pairDataIndexedVyper[K <: Symbol, T <: Type](
    implicit wk: Witness.Aux[K]
  ): DataVyper[FieldType[K, T @@ Indexed]] =
    new DataVyper[FieldType[K, T @@ Indexed]] {
      override def toVyperDefinitions(data: FieldType[K, T @@ Indexed]): List[String] =
        s"${wk.value.name}: indexed(${data.toVyper})" :: Nil
    }

  implicit def pairDataPublicVyper[K <: Symbol, T <: Type](
    implicit wk: Witness.Aux[K]
  ): DataVyper[FieldType[K, T @@ Public]] =
    new DataVyper[FieldType[K, T @@ Public]] {
      override def toVyperDefinitions(data: FieldType[K, T @@ Public]): List[String] =
        s"${wk.value.name}: public(${data.toVyper})" :: Nil
    }

}
