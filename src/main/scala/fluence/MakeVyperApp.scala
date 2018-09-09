package fluence

import hackethberlin._
import hackethberlin.types._
import shapeless._
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

  val f = `@public` @:
    sumArgs.funcDef("sum", uint256) { args ⇒
    for {
      c ← 'c :=: `++`(args.ref('a), args.ref('b))
      d ← 'd :=: `++`(args.ref('b), c)
      _ ← d :==: c
      sum ← `++`(args.ref('a), d).toReturn
    } yield sum
  }

  val all = recordStruct :: data :: func :: f :: HNil

  val c = new Contract(recordStruct :: struct :: data :: func :: f :: HNil)

  println(c.toVyper)

  println(
    func(recordStruct.ref('record_address) :: HNil).toVyper
  )

//  println(s"MMMMMACRO\n\n ${new MyContract("abc", 123).toAST.toVyper}")
}

object Auction {
  val data = ProductType(
    ('beneficiary ->> public(address)) ::
      ('auction_start ->> public(timestamp)) ::
      ('auction_end ->> public(timestamp)) ::
      ('highest_bidder ->> `public`(address)) ::
      ('highest_bid ->> `public`(wei_value)) ::
      ('ended ->> public(bool)) :: HNil
  )

  val beneficiary = data.ref('beneficiary)
  val auction_start = data.ref('auction_start)
  val auction_end = data.ref('auction_end)
  val highest_bid = data.ref('highest_bid)

  val initArgs = ProductType(('_beneficiary ->> address) :: ('_bidding_time ->> timedelta) :: HNil)

  import untag._

  val _beneficiary = initArgs.ref('_beneficiary)
  val _bidding_time = initArgs.ref('_bidding_time)

  println(_beneficiary.getClass)

  val init = `@public` @: initArgs.funcDef(
    "__init__",
    Void
  ) { args ⇒
    for {
        _ <- Free.pure(Void)
//      _ <- beneficiary :==: _beneficiary
//      _ <- auction_start :==: block.timestamp
//      _ <- auction_end :==: auction_start `+:+` _bidding_time
    } yield Void
  }


  val bid = `@public` @: `@payable` @: ProductType(HNil).funcDef(
    "bid",
    Void
  ) { args ⇒
    for {
      _ <- Free.pure(Void)
      _ <- `assertt` (block.timestamp `<` auction_end)
      _ <- `assertt` (msg.value `>` highest_bid)
    } yield Void
  }
}