package fluence.hackethberlin.types

abstract sealed class PrimitiveType(name: String) extends Type {
  self ⇒

  override def toVyper: String = name

  def ~>>[V <: Type](vtype: V): Mapping[self.type, V] = Mapping(self, vtype)
}

object PrimitiveType {

  trait Defs {
    case object address extends PrimitiveType("address")
    case object bool extends PrimitiveType("bool")
    case object int128 extends PrimitiveType("int128")
    case object uint256 extends PrimitiveType("uint256")
    case object decimal extends PrimitiveType("decimal")
  }
}
