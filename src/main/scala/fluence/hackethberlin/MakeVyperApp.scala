package fluence.hackethberlin

import shapeless._
import types._
import record._
import Decorator._
import shapeless.labelled.FieldType
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

  println(Console.RED + recordStruct.get('other_some) + Console.RESET)

  println(recordStruct.toVyper)

  println(data.toVyper)

  println(func.toVyper)

  println((`@public` @: func).toVyper)

}
