package fluence.hackethberlin.types

sealed trait Void extends Type

// TODO: should it exist as an instance?
case object Void extends Type {
  override def toVyper: String = ""
}