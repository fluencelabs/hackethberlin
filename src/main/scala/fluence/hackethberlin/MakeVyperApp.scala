package fluence.hackethberlin

import shapeless._
import types._

object MakeVyperApp extends App {

  val struct = new StructType(
    ("address" → address) ::
      ("owner" → address) ::
      ("size" -> uint256) ::
      ("time" -> int128) :: HNil
  )

  val data = new DataDef(
    ("address" → address) ::
      ("owner" → `public`(address)) ::
      ("holders" → (address ~>> bool)) ::
      ("structMap" → `public`(Mapping(uint256, struct))) ::
      ("struct" → `public`(struct)) ::
      ("struct2" → struct) :: HNil
  )

  println(data.toVyper)

}