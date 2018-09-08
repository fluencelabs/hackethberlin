package fluence.hackethberlin.types

sealed trait Void extends Type

// TODO: should it exist as an instance?
case object Void extends Void {
  override def toVyper: String = ""
}
