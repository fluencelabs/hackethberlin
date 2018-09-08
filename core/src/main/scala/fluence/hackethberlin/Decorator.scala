package fluence.hackethberlin

sealed abstract class Decorator(name: String) {
  def toVyper: String = s"@$name"
}

object Decorator {
  case object `@public` extends Decorator("public")
  case object `@payable` extends Decorator("payable")
  case object `@constant` extends Decorator("constant")
}
