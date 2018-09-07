package fluence.hackethberlin.types

trait Type {
  def toVyper: String
}

trait PlainType extends Type