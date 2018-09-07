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
      ("struct" → `public`(struct)) :: HNil
  )


  println(data.toVyper)

  println(struct.toVyper)

}