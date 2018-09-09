package fluence
import fluence.hackethberlin.ToVyper
import shapeless.::
import fluence.hackethberlin.Decorator._
import fluence.hackethberlin._
import fluence.hackethberlin.types._
import shapeless._
import shapeless.syntax.singleton._

object MyContract {
  implicit val p: shapeless.Witness.Aux[Symbol] = shapeless.Witness.mkWitness('aaa)

  val func = FuncDef.apply(
    "myFunc",
    ('addr ->> address) :: HNil,
    MyVoid()
  )(
    fluence.hackethberlin.types.EmptyBody.get(('addr ->> address) :: HNil)
  )

  val func2 = FuncDef(
    "myFunc",
    ('addr ->> address) :: HNil,
    address
  )(args ⇒ args.ref('addr).toReturn)

  @ToVyper
  class Contract(owner: String, friend: Int) {}
}
