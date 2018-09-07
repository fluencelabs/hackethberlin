package fluence.hackethberlin.types

import shapeless.{::, HList, HNil}

sealed trait DataVyper[T] {
  def toVyperDefinitions(data: T): List[String]
}

object DataVyper {

  implicit object hnilDataVyper extends DataVyper[HNil] {
    override def toVyperDefinitions(data: HNil): List[String] = Nil
  }

  implicit def hlistDataVyper[H, T <: HList](implicit dh: DataVyper[H], dt: DataVyper[T]): DataVyper[H :: T] =
    new DataVyper[H :: T] {
      override def toVyperDefinitions(data: H :: T): List[String] =
        dh.toVyperDefinitions(data.head) ::: dt.toVyperDefinitions(data.tail)
    }

  implicit def pairDataVyper[T <: Type]: DataVyper[(String, T)] =
    new DataVyper[(String, T)] {
      override def toVyperDefinitions(pair: (String, T)): List[String] = {
        val (name, ttype) = pair
        s"$name: ${ttype.toVyper}" :: Nil
      }
    }
}
