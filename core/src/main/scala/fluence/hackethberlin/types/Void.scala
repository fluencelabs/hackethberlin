package fluence.hackethberlin.types
import cats.free.Free
import fluence.hackethberlin.Expr
import shapeless.HList

sealed trait Void extends Type

// TODO: should it exist as an instance?
case object Void extends Void {
  override def toVyper: String = ""
}

case object EmptyBody {

  def get[Args <: HList: DataVyper](args: Args): ProductType[Args] ⇒ Free[Expr, Unit] = { _ =>
    Free.pure(Void)
  }
}
