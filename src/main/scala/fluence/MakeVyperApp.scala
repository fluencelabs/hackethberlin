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
      _ ← Free.pure(`++`(args.ref('a), args.ref('b)))
      d ← 'd :=: `++`(args.ref('b), c)
      _ ← d :=: c
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

object Auction extends App {
  import Expr.Defs._

  val predef = ProductType(
    (Symbol("block.timestamp") ->> timestamp) ::
      (Symbol("msg.value") ->> wei_value) ::
      (Symbol("msg.sender") ->> address) ::
      (Symbol("True") ->> bool) ::
      HNil
  )

  val `block.timestamp` = predef.ref(Symbol("block.timestamp"))
  val `msg.value` = predef.ref(Symbol("msg.value"))
  val `msg.sender` = predef.ref(Symbol("msg.sender"))
  val `True` = predef.ref('True)

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
  val highest_bidder = data.ref('highest_bidder)
  val ended = data.ref('ended)

  val initArgs = ProductType(('_beneficiary ->> address) :: ('_bidding_time ->> timedelta) :: HNil)

  val _beneficiary = initArgs.ref('_beneficiary)
  val _bidding_time = initArgs.ref('_bidding_time)

  val init = `@public` @: initArgs.funcDef(
    "__init__",
    Void
  ) { args ⇒
    for {
      _ <- Free.pure(Void)
      _ <- beneficiary :=: _beneficiary
      _ <- auction_start :=: `block.timestamp`
      _ <- auction_end :=: `+:+`(auction_start, _bidding_time)
    } yield Void
  }

  val bidIf: () ⇒ Free[Expr, Void] = { () =>
    for {
      _ <- FuncDef.send(highest_bidder :: highest_bid :: HNil).liftF
    } yield Void
  }

  val bid = `@public` @: `@payable` @: ProductType.hNil.funcDef(
    "bid",
    Void
  ) { args ⇒
    for {
      _ <- `assertt`(`<<`(`block.timestamp`, auction_end)).liftF
      _ <- `assertt`(`>>`(`msg.value`, highest_bid)).liftF
      _ <- `if`(`not`(`:===:`(highest_bid, `msg.value`)), bidIf).liftF
      _ <- highest_bidder :=: `msg.sender`
      _ <- highest_bid :=: `msg.value`
    } yield Void
  }

  val end_auction = `@public` @: ProductType.hNil.funcDef(
    "end_auction",
    Void
  ) { args ⇒
    for {
      _ <- `assertt`(`>=`(`block.timestamp`, auction_end)).liftF
      _ <- `assertt`(`not`(ended)).liftF
      _ <- ended :=: `True`
      _ <- FuncDef.send(beneficiary :: highest_bid :: HNil).liftF
    } yield Void
  }

  val contract = new Contract(data :: init :: bid :: end_auction :: HNil)

  println(contract.toVyper)
}
