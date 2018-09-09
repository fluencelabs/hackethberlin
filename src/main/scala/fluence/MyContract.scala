package fluence
import fluence.hackethberlin.ToVyper
import shapeless.::

object MyContract {
  implicit val p: shapeless.Witness.Aux[Symbol] = shapeless.Witness.mkWitness('aaa)

  /*import fluence.hackethberlin.Decorator._
  import fluence.hackethberlin._
  import fluence.hackethberlin.types._
  import shapeless._
  import shapeless.syntax.singleton._*/

  @ToVyper
  class Contract(owner: String, friend: Int) {}
}
