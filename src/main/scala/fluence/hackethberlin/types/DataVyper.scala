package fluence.hackethberlin.types

import shapeless._
import shapeless.tag._

sealed trait DataVyper[T] {
  def toVyperDefinitions(data: T): List[String]
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

  implicit def pairDataVyper[T <: Type]: DataVyper[(String, T)] =
    new DataVyper[(String, T)] {
      override def toVyperDefinitions(pair: (String, T)): List[String] = {
        val (name, ttype) = pair
        s"$name: ${ttype.toVyper}" :: Nil
      }
    }
}

object DataVyper extends LowPriorityDataVyperImplicits {

  implicit def pairDataPublicVyper[T <: Type]: DataVyper[(String, T @@ Public)] =
    new DataVyper[(String, T @@ Public)] {
      override def toVyperDefinitions(pair: (String, T @@ Public)): List[String] = {
        val (name, ttype) = pair
        s"$name: public(${ttype.toVyper})" :: Nil
      }
    }

  implicit def pairDataIndexedVyper[T <: Type]: DataVyper[(String, T @@ Indexed)] =
    new DataVyper[(String, T @@ Indexed)] {
      override def toVyperDefinitions(pair: (String, T @@ Indexed)): List[String] = {
        val (name, ttype) = pair
        s"$name: indexed(${ttype.toVyper})" :: Nil
      }
    }

}
