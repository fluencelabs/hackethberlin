package fluence.hackethberlin

import cats.free.Free
import types._

sealed trait Expr[T] {
  def boxedValue: T

  def toVyper: String
}

sealed trait InlineExpr[T] extends Expr[T] with Expr.ToInlineVyper {
  def toReturn: Free[Expr, T] = Free liftF Expr.Return[T](this)

  def liftF: Free[Expr, T] = Free liftF this

  def :=:(name: Symbol): Free[Expr, Expr.Ref[T]] =
    Free.liftF[Expr, Expr.Ref[T]](Expr.Assign[T](Expr.Ref[T](name.name, boxedValue), this))

  def :=:[J <: T](ref: Expr.Ref[J]): Free[Expr, Expr.Ref[T]] =
    Free.liftF[Expr, Expr.Ref[T]](Expr.Assign[T](Expr.Ref[T](ref.name, ref.boxedValue), this))
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

  case class RightBody[R, T](
    op: String,
    right: InlineExpr[R],
    boxedValue: T,
    body: () ⇒ Free[Expr, Void]
  ) extends InlineExpr[T] {
    def bodyVyper: String =
      body().foldMap(CodeChunk.fromExpr).run._1.toVyper(2)
    override def toVyper: String = s"$op " + right.toVyper + s":\n$bodyVyper"
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

    def `:===:`[A <: Type, B <: Type](a: InlineExpr[A], b: InlineExpr[B]): InlineExpr[bool.type] =
      Infix("==", a, b, bool)

    def `+:+`[A <: timestamp.type, B <: timedelta.type](a: InlineExpr[A], b: InlineExpr[B]): InlineExpr[timestamp.type] =
      Infix("+", a, b, timestamp)

    def `if`(expr: InlineExpr[bool.type], body: () ⇒ Free[Expr, Void]): InlineExpr[Void] =
      RightBody("if", expr, Void, body)

    def `not`[A <: bool.type](expr: InlineExpr[A]): InlineExpr[bool.type] =
      Right("not", expr, bool)

    def `assertt`(expr: InlineExpr[bool.type]): InlineExpr[bool.type] =
      Right("assert", expr, bool)

    def `<<`[A <: Type, B <: Type](a: InlineExpr[A], b: InlineExpr[B]): InlineExpr[bool.type] =
      Infix("<", a, b, bool)

    def `>=`[A <: Type, B <: Type](a: InlineExpr[A], b: InlineExpr[B]): InlineExpr[bool.type] =
      Infix(">=", a, b, bool)

    def `>>`[A <: Type, B <: Type](a: InlineExpr[A], b: InlineExpr[B]): InlineExpr[bool.type] =
      Infix(">", a, b, bool)
  }

  object Defs extends Defs
}
