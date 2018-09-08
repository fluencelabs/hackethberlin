package fluence.hackethberlin

sealed trait Expr[T <: types.Type] {
  def toVyper(depth: Int): String

  protected def spaces(depth: Int): String = "  " * depth
}

sealed trait InlineExpr[T <: types.Type] extends Expr[T]{
  override def toVyper(depth: Int): String = spaces(depth) + toInlineVyper

  def toInlineVyper: String

  def toReturn: Expr.Return[T] = Expr.Return[T](this)
}

object Expr {
  case class Ref[T <: types.Type](name: String) extends InlineExpr[T] {
    override def toInlineVyper: String = name
  }

  case class Return[T <: types.Type](ret: InlineExpr[T]) extends Expr[T] {
    override def toVyper(depth: Int): String =
      spaces(depth) + "return "+ret.toInlineVyper

  }
}
