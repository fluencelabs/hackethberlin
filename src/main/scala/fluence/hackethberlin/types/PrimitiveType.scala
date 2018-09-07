package fluence.hackethberlin.types

abstract sealed class PrimitiveType(name: String) extends PlainType {
  override def toVyper: String = name
}

object PrimitiveType {
  trait Defs {
    object address extends PrimitiveType("address")
    object bool extends PrimitiveType("bool")
    object int128 extends PrimitiveType("int128")
    object uint256 extends PrimitiveType("uint256")
    object decimal extends PrimitiveType("decimal")
  }
}