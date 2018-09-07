package fluence.hackethberlin.types

abstract sealed class PrimitiveType(name: String) extends Type {
  override def toVyper: String = name
}

object PrimitiveType {
  trait Defs {
    object address extends PrimitiveType("address")
  }
}