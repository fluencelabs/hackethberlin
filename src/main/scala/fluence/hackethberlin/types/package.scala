package fluence.hackethberlin

import shapeless.tag

package object types extends PrimitiveType.Defs {
  sealed trait Public
  sealed trait Indexed

  val `public`: tag.Tagger[Public] = tag[Public]
  val indexed: tag.Tagger[Indexed] = tag[Indexed]
}
