package fluence.hackethberlin

import cats.free.Free
import types._

sealed trait Expr[T] {
  def boxedValue: T

  def toVyper: String
}

sealed trait InlineExpr[T <: types.Type] extends Expr[T] {
  def toReturn: Free[Expr, T] = Free liftF Expr.Return[T](this)

  def :=:(name: Symbol): Free[Expr, Expr.Ref[T]] =
    Free.liftF[Expr, Expr.Ref[T]](Expr.Assign[T](Expr.Ref[T](name.name, boxedValue), this))
}

object Expr {
  case class Ref[T <: types.Type](name: String, boxedValue: T) extends InlineExpr[T] {
    override def toVyper: String = name
  }

  case class Infix[L <: types.Type, R <: types.Type, T <: types.Type](
    op: String,
    left: InlineExpr[L],
    right: InlineExpr[R],
    boxedValue: T
  ) extends InlineExpr[T] {
    override def toVyper: String = left.toVyper + s" $op " + right.toVyper
  }

  case class Assign[T <: Type](ref: Ref[T], value: InlineExpr[T]) extends Expr[Ref[T]] {
    override def boxedValue: Ref[T] = ref

    override def toVyper: String =
      s"${ref.toVyper} = ${value.toVyper}"
  }

  case class Return[T <: types.Type](ret: InlineExpr[T]) extends Expr[T] {
    override def boxedValue: T = ret.boxedValue

    override def toVyper: String =
      "return " + ret.toVyper

  }

  trait Defs {

    def `++`(a: InlineExpr[uint256.type], b: InlineExpr[uint256.type]): InlineExpr[uint256.type] =
      Infix("+", a, b, uint256)
  }

  object Defs extends Defs
}
