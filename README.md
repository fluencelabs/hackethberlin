# Crotalinae

Metaprogramming for Ethereum Smart Contracts expressed in Scala's Type System.

- Unlock Smart Contracts **adoption with a JVM**-based language: code them _from_ Scala or _in_ Scala
- Smart Contracts code generator is itself **a strictly typed program**
- Write a Contract using structs and definitions of **Crotalinae DSL**, and if it compiles, you're **safe**
- **Export** Smart Contract in [Vyper](https://github.com/ethereum/vyper) as a single plaintext and check it visually if needed
- _(WIP)_ Code directly in Scala: **Scala source code** is translated to Crotalinae DSL using macros

## Tech dive

[Scala language](https://www.scala-lang.org/) is a functional programming language for JVM.
 
We've noticed that we may generate Smart Contracts in a functional way: function definition is a profunctor (_Intuitively it is a bifunctor where the first argument is contravariant and the second argument is covariant._), arguments and structs are _products_, and so on.

Scala's strictly typed product exists in form of a [shapeless heterogenous list](https://github.com/milessabin/shapeless). As arguments or contract data may be referenced by name, we also use Record pattern and reflect these names in the type system.

To go beyond the data and function definitions, we use [Free Monad](https://typelevel.org/cats/datatypes/freemonad.html) from a category theory library for Scala named [Cats](https://github.com/typelevel/cats). It lets us _represent stateful computations as data, and run them_.

Running, in our case, means code generation. It is done with a _natural transformation_ from _Crotalinae DSL_ into a _Writer Monad_, which is then converted to a text.

Contracts are exported in [Vyper](https://github.com/ethereum/vyper) language. 
It's simple, very readable and comprehensive, and we really like it! 
However, it lacks tools for code reuse: you often need to copy-paste where, say in Solidity, you may call a function from a library. 
With _Crotalinae_ you still have _Vyper_ sources at the end, but may take advantages of the functional composition to re-use code in different contracts instead of copy-pasting it.

## Example

This _crazy_ Scala code:

```scala

  val f = `@public` @:
    sumArgs.funcDef("sumSome", uint256) { args ⇒
    for {
      c ← 'c :=: `++`(args.ref('a), args.ref('b))
      d ← 'd :=: `++`(args.ref('b), c)
      _ ← d :=: c
      sum ← `++`(args.ref('a), d).toReturn
    } yield sum
  }
  
  println(f.toVyper)

```

Compiles into this:

```python

@public
def sumSome(a: uint256, b: uint256) -> uint256:
  c = a + b
  d = b + c
  d = c
  return a + d

```

The more sophisticated [Auction example](https://github.com/fluencelabs/hackethberlin/blob/master/src/main/scala/fluence/Auction.scala) is deployed on [Rinkeby](https://rinkeby.etherscan.io/address/0xf24A7726eaF1337A2E8826579EA381705fe64164). Hooray!

## Future plans

Of course, building EVM code from Vyper, from Scala DSL, using Free Monads and Shapeless Records, is not enough.

So we're working on Macro Programming support as well. 
It will add one more layer of Scala code (parsed to Scala AST, used to generate Crotalinae DSL, and down on a chain).

So stay tuned!