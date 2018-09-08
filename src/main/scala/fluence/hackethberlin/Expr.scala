package fluence.hackethberlin

sealed trait Expr[T <: types.Type] {
  def toVyper(depth: Int): String
}

object Expr {
  case class Ref[T <: types.Type](name: String) extends Expr[T] {
    override def toVyper(depth: Int): String =
      ("  " * depth) + name
  }
}
