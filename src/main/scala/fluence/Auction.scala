package fluence

import hackethberlin._
import hackethberlin.types._
import shapeless._
import Decorator._
import syntax.singleton._
import fluence.hackethberlin.{Contract, Expr}
import fluence.hackethberlin.types.{`public`, ProductType, Void}
import shapeless.HNil

object Auction extends App {
  import Expr.Defs._

  val data = ProductType.self(
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
      _ <- beneficiary :=: _beneficiary
      _ <- auction_start :=: `block.timestamp`
      _ <- auction_end :=: `+:+`(auction_start, _bidding_time)
    } yield Void
  }

  val bid = `@public` @: `@payable` @: ProductType.hNil.funcDef(
    "bid",
    Void
  ) { args ⇒
    for {
      _ <- `assert`(`<<`(`block.timestamp`, auction_end))
      _ <- `assert`(`>>`(`msg.value`, highest_bid))
      _ <- `if`(`not`(`:===:`(highest_bid, `msg.value`)), {
        () =>
          send(highest_bidder :: highest_bid :: HNil).liftF.map(_ => Void)
      })
      _ <- highest_bidder :=: `msg.sender`
      _ <- highest_bid :=: `msg.value`
    } yield Void
  }

  val end_auction = `@public` @: ProductType.hNil.funcDef(
    "end_auction",
    Void
  ) { args ⇒
    for {
      _ <- `assert`(`>=`(`block.timestamp`, auction_end))
      _ <- `assert`(`not`(ended))
      _ <- ended :=: `True`
      _ <- send(beneficiary :: highest_bid :: HNil).liftF.map(_ => Void)
    } yield Void
  }

  val contract = new Contract(data :: init :: bid :: end_auction :: HNil)

  println(contract.toVyper)
}
