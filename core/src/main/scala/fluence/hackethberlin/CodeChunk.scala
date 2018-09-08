package fluence.hackethberlin

import cats.data.Writer
import cats.{~>, Monoid}

import scala.collection.immutable.Queue

sealed trait CodeChunk {
  def toVyper(indent: Int): String
}

case class CodeBlock(lines: Queue[String]) extends CodeChunk {
  override def toVyper(indent: Int): String =
    lines.mkString(CodeChunk.spaces(indent), "\n" + CodeChunk.spaces(indent), "\n")
}

case class CodeLine(line: String) extends CodeChunk {
  override def toVyper(indent: Int): String =
    CodeChunk.spaces(indent) + line
}

object CodeChunk {
  val Space = "  "

  def spaces(indent: Int): String = Space * indent

  implicit object codeChunkMonoid extends Monoid[CodeChunk] {
    override def empty: CodeChunk = CodeBlock(Queue.empty)

    override def combine(x: CodeChunk, y: CodeChunk): CodeChunk =
      (x, y) match {
        case (CodeBlock(lines), CodeLine(line)) ⇒ CodeBlock(lines enqueue line)
        case (CodeLine(l1), CodeLine(l2)) ⇒ CodeBlock(Queue(l1, l2))
        case (CodeBlock(ls1), CodeBlock(ls2)) ⇒ CodeBlock(ls1 enqueue ls2)
        case (CodeLine(line), CodeBlock(lines)) ⇒ CodeBlock(line +: lines)
      }
  }

  type W[A] = Writer[CodeChunk, A]

  object fromExpr extends (Expr ~> W) {
    override def apply[A](fa: Expr[A]): W[A] =
      Writer(CodeLine(fa.toVyper), fa.boxedValue)

  }
}
