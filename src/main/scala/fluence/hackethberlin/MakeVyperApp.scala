package fluence.hackethberlin

import shapeless._
import types._
import Decorator._
import syntax.singleton._

object MakeVyperApp extends App {

  val struct = new StructType(
    ('address ->> address) ::
      ('owner ->> address) ::
      ('size ->> uint256) ::
      ('time ->> int128) :: HNil
  )

  val data = new DataDef(
    ('address ->> address) ::
      ('owner ->> `public`(address)) ::
      ('holders ->> (address ~>> bool)) ::
      ('structMap ->> `public`(uint256 ~>> struct)) ::
      ('struct ->> `public`(struct)) ::
      ('struct2 ->> struct) :: HNil
  )

  val func = FuncDef(
    "myFunc",
    ('address ->> address) :: HNil,
    uint256
  )


  val strDataDef = ('address ->> address) :: HNil

  val recordStruct = new StructType(
    ('record_address ->> address) :: ('other_some ->> uint256) :: HNil
  )

  println(recordStruct.toVyper)

  println(data.toVyper)

  println(func.toVyper)

  println((`@public` @: func).toVyper)

}
