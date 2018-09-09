package fluence.hackethberlin

import scala.language.implicitConversions
import shapeless._
import shapeless.Everywhere
import ops.hlist.{Align, Mapper, Prepend}
import poly._
import record._
import syntax.singleton._
import shapeless.tag
import tag.{@@, Tagged}

object untag extends Poly1 {
  implicit def ut[A, B] = at[A @@ B]{ x =>
    println("untagging " + x)
    x.asInstanceOf[A]
  }

  trait Converter[G <: HList, A] {
    def convert(g: G): A
  }

  implicit def convert[A, F <: HList, G <: HList](implicit
                                                  gen: LabelledGeneric.Aux[A, F],
                                                  align: Align[G, F]) =
    new Converter[G, A] {
      def convert(g: G) = gen.from(align(g))
    }

  implicit class ToConverter[G <: HList, R <: HList, F <: HList](ag: (R, F)) {
    def convertTo[B](implicit p: Prepend.Aux[R, F, G], c: Converter[G, B]) =
      c.convert(ag._1 ++ ag._2)
  }

  implicit class Untag[A, R <: HList](a: A)(implicit
                                            gen: LabelledGeneric.Aux[A, R],
                                            val e: Everywhere[untag.type, R]) {
    def untagged: e.Result = e(gen.to(a))
  }
}

object Example extends App {

  import untag._

  trait TagInt {}

  case class Foo(a: String, b: Int @@ TagInt)
  case class Bar(b: Int, a: String, c: String, d: Int)
  case class Baz(a: String, b: Int)



  println(Foo("foo", tag[TagInt](1)).untagged)
  // Next line is broken, type info is lost coming from call to untagged
  // poly is being applied to the tagged field as output shows

  // println(Foo("foo", tag[TagInt](1)).untagged, (('d ->> 3) :: ('c ->> "bar") :: HNil)).convertTo[Bar])

  val gbz = LabelledGeneric[Baz]
  println((gbz.to(Baz("foo", 1)), (('d ->> 3) :: ('c ->> "bar") :: HNil)).convertTo[Bar])
}
