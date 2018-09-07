package fluence.hackethberlin.types

case class ModifiedType[T <: Type](modifier: String, ttype: T) extends Type {
  override def toVyper: String = s"$modifier(${ttype.toVyper})"
}

object ModifiedType {
  trait Defs {
    def `public`[T <: Type](ttype: T): ModifiedType[T] = ModifiedType("public", ttype)
  }
}
