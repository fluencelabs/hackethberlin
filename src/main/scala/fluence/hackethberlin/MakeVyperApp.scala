package fluence.hackethberlin

import shapeless._
import types._
import Decorator._
import fluence.hackethberlin.macrogen.ToVyper

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
      ("structMap" → `public`(uint256 ~>> struct)) ::
      ("struct" → `public`(struct)) ::
      ("struct2" → struct) :: HNil
  )

  val func = FuncDef(
    "myFunc",
    ("address" → address) :: HNil,
    uint256
  )

  println(data.toVyper)

  println(func.toVyper)

  println((`@public` @: func).toVyper)

  @ToVyper
  class MyContract(owner: String, smowner: Int) {
    val _owner: String = owner

    def isOwner(addr: String): Boolean = {
      _owner == owner
    }
  }
}
