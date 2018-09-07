package fluence.hackethberlin

import shapeless._
import types._

object MakeVyperApp extends App {

  val data = new DataDef(
    ("address" → address) ::
      ("owner" → `public`(address)) :: HNil
  )

  val struct = new StructType(
    "someStruct",
    ("address" → address) ::
      ("owner" → address) ::
      ("size" -> uint256) ::
      ("time" -> int128) :: HNil
  )

  println(data.toVyper)

  println(struct.toVyper)

}