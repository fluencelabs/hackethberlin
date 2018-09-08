package fluence.hackethberlin

import shapeless.tag

import reflect.runtime.universe.{Liftable, Quasiquote}
import scala.reflect.runtime.universe

package object types extends PrimitiveType.Defs {
  sealed trait Public
  sealed trait Indexed

  val `public`: tag.Tagger[Public] = tag[Public]
  val indexed: tag.Tagger[Indexed] = tag[Indexed]

  implicit val lift: Liftable[(String, PrimitiveType)] = Liftable[(String, PrimitiveType)] { case (l, r) =>
    q"(l -> r)"
  }
}
