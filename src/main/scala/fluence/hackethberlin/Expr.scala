package fluence.hackethberlin

sealed trait Expr[T <: types.Type] {
  def toVyper(depth: Int): String
}

object Expr {
  case class Ref[T]()
}
