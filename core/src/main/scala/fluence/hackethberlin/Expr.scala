package fluence.hackethberlin

import cats.free.Free
import types._

sealed trait Expr[T] {
  def boxedValue: T

  def toVyper: String
}

sealed trait InlineExpr[T] extends Expr[T] with Expr.ToInlineVyper {
  def toReturn: Free[Expr, T] = Free liftF Expr.Return[T](this)

  def :=:(name: Symbol): Free[Expr, Expr.Ref[T]] =
    Free.liftF[Expr, Expr.Ref[T]](Expr.Assign[T](Expr.Ref[T](name.name, boxedValue), this))

  def :==:(ref: Expr.Ref[T]): Free[Expr, Expr.Ref[T]] =
    Free.liftF[Expr, Expr.Ref[T]](Expr.Assign[T](ref, this))
}

object Expr {
  sealed trait ToInlineVyper {
    def toInlineVyper: String
  }

  case class Ref[T](name: String, boxedValue: T) extends InlineExpr[T] {
    override def toVyper: String = name
    override def toInlineVyper: String = toVyper
  }

  case class Infix[L, R, T](
    op: String,
    left: InlineExpr[L],
    right: InlineExpr[R],
    boxedValue: T
  ) extends InlineExpr[T] {
    override def toVyper: String = left.toVyper + s" $op " + right.toVyper
    override def toInlineVyper: String = toVyper
  }

  case class Right[R, T](
    op: String,
    right: InlineExpr[R],
    boxedValue: T
  ) extends InlineExpr[T] {
    override def toVyper: String = s"$op " + right.toVyper
    override def toInlineVyper: String = toVyper
  }

  case class Assign[T](ref: Ref[T], value: InlineExpr[T]) extends Expr[Ref[T]] {
    override def boxedValue: Ref[T] = ref

    override def toVyper: String =
      s"${ref.toVyper} = ${value.toVyper}"
  }

  case class Return[T](ret: InlineExpr[T]) extends Expr[T] {
    override def boxedValue: T = ret.boxedValue

    override def toVyper: String =
      "return " + ret.toVyper

  }

  trait Defs {

    def `++`(a: InlineExpr[uint256.type], b: InlineExpr[uint256.type]): InlineExpr[uint256.type] =
      Infix("+", a, b, uint256)

    def `==`[A <: Type, B <: Type](a: InlineExpr[A], b: InlineExpr[B]): InlineExpr[bool.type] =
      Infix("==", a, b, bool)

    def `+:+`(a: InlineExpr[timestamp.type], b: InlineExpr[timedelta.type]): InlineExpr[timestamp.type] =
      Infix("+", a, b, timestamp)

    def `if`(expr: InlineExpr[bool.type]): InlineExpr[bool.type] =
      Right("if", expr, bool)

    def `not`(expr: InlineExpr[bool.type]): InlineExpr[bool.type] =
      Right("not", expr, bool)

    def `assertt`(expr: InlineExpr[bool.type]): InlineExpr[bool.type] =
      Right("assert", expr, bool)

    def `<`[A <: Type, B <: Type](a: InlineExpr[A], b: InlineExpr[B]): InlineExpr[bool.type] =
      Infix("<", a, b, bool)

    def `>`[A <: Type, B <: Type](a: InlineExpr[A], b: InlineExpr[B]): InlineExpr[bool.type] =
      Infix(">", a, b, bool)
  }

  object Defs extends Defs
}
