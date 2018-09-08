package fluence.hackethberlin

import shapeless._
import types._
import Decorator._
import cats.free.Free
import syntax.singleton._

object MakeVyperApp extends App {

  val struct = ProductType(
    ('address ->> address) ::
      ('owner ->> address) ::
      ('size ->> uint256) ::
      ('time ->> int128) :: HNil
  )

  val data = ProductType(
    ('address ->> address) ::
      ('owner ->> `public`(address)) ::
      ('holders ->> (address ~>> bool)) ::
      ('structMap ->> `public`(uint256 ~>> struct)) ::
      ('struct ->> `public`(struct)) ::
      ('struct2 ->> struct) :: HNil
  )

  val func = FuncDef(
    "myFunc",
    ('addr ->> address) :: HNil,
    address
  )(args ⇒ args.ref('addr).toReturn)

  val recordStruct = ProductType(
    ('record_address ->> address) :: ('other_some ->> uint256) :: HNil
  )

  println(Console.RED + recordStruct.ref('other_some) + Console.RESET)

  println(recordStruct.toVyper)

  println(data.toDataVyper)

  println(func.toVyper)

  val sumArgs = ProductType(('a ->> uint256) :: ('b ->> uint256) :: HNil)

  import Expr.Defs._

  println(
    (
      `@public` @:
        sumArgs.funcDef("sum", uint256) { args ⇒
        for {
          c ← 'c :=: `++`(args.ref('a), args.ref('b))
          d ← 'd :=: `++`(args.ref('b), c)
          sum ← `++`(args.ref('a), d).toReturn
        } yield sum
      }
    ).toVyper
  )

}
