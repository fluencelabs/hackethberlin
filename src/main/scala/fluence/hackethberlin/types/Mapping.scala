package fluence.hackethberlin.types

case class Mapping[K <: PrimitiveType, V <: Type](ktype: K, vtype: V) extends Type {
  override def toVyper: String = s"${vtype.toVyper}[${ktype.toVyper}]"
}
