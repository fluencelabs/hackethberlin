package fluence.hackethberlin

import fluence.hackethberlin.types.{PrimitiveType, _}
import shapeless.labelled.FieldType
import shapeless.syntax
import shapeless.syntax.SingletonOps
import syntax.singleton._

import scala.annotation.{compileTimeOnly, StaticAnnotation}
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import scala.reflect.runtime.universe.{Tree, ValDef}

@compileTimeOnly("ToVyper is compileTimeOnly")
class ToVyper extends StaticAnnotation {

  def macroTransform(annottees: Any*) = macro ToVyper.impl
}

object ToVyper {

  def mapParam(p: ValDef): (String, PrimitiveType) = {
    p.name.toString -> mapType(p.tpt)
  }

  def mapType(t: Tree) = {
    t.toString match {
      case "String" => fluence.hackethberlin.types.address
      case "Int" => fluence.hackethberlin.types.int128
    }
  }

  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe.{Quasiquote, Tree => CTree}

    annottees.headOption.map(_.tree) match {
      case Some(q"$mods class $tpname $ctorMods(..$paramss) { ..$stats }") =>
        println(s"QUASI MATCHED \nmods $mods \ntpname $tpname \nctorMods $ctorMods \nparamss $paramss \nstats$stats")

        implicit val liftPrimitive: c.universe.Liftable[PrimitiveType] = c.universe.Liftable[PrimitiveType] {
          case `address` => q"_root_.fluence.hackethberlin.types.address"
          case `bool` => q"_root_.fluence.hackethberlin.types.bool"
          case `int128` => q"_root_.fluence.hackethberlin.types.int128"
          case `uint256` => q"_root_.fluence.hackethberlin.types.uint256"
          case `decimal` => q"_root_.fluence.hackethberlin.types.decimal"
          case `string` => q"_root_.fluence.hackethberlin.types.string"
        }

        // fluence.hackethberlin.types.PrimitiveType with shapeless.labelled.KeyTag[l.type,fluence.hackethberlin.types.PrimitiveType]

        implicit val lift: c.universe.Liftable[(Symbol, PrimitiveType)] = c.universe.Liftable[(Symbol, PrimitiveType)] {
          case (l, r) =>
            type wtf = fluence.hackethberlin.types.PrimitiveType with shapeless.labelled.KeyTag[
              l.type,
              fluence.hackethberlin.types.PrimitiveType
            ]
            implicit val liftWtf: c.universe.Liftable[wtf] = c.universe.Liftable[wtf] {
              case _ => q"shapeless.labelled.field.apply(_root_.fluence.hackethberlin.types.address)"
            }

//              q"$l -> $r"
//            q"('abc ->> $r)"
            q"(${shapeless.syntax.singleton.mkSingletonOps(l).->>(r)})"
        }

        val params = paramss
          .map(p => mapParam(p.asInstanceOf[ValDef]))
          .foldRight[CTree](q"shapeless.HNil")(
            (elem, acc) => q"$elem :: $acc"
          )
        c.Expr[Any](q"""
          class $tpname(..$paramss) {
            def toAST = {
             fluence.hackethberlin.FuncDef.apply(
                "__init__", $params
             )(fluence.hackethberlin.types.EmptyBody.get($params))
            }
          }
        """)

      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }
  }
}

// .${params.map(mapParam)} ::
//  @inline def apply(defn: Any): Any = meta {
//    defn match {
//      case cls @ Defn.Class(_, name, _, ctor, template) =>
//        val params = ctor.paramss.head
//        val tree: Tree =
//          q"""
//          FuncDef("__init__", HNil, uint256)
//          """
//        tree
//
//      case _ => throw new Exception()
//    }
//  }
//

//
//  def mapParam(p: Term.Param) = {
//    s"${p.name}" -> s"${mapType(p.decltpe.get)}"
//  }

/*

// Scala

class MyContract(owner: String) {
  val _owner: String = owner

  def isOwner(addr: String): Boolean = {
    _owner == owner
  }
}


// Vyper

_owner: public(string)

@public
__init__(owner: string):
  self._owner = owner

@public
def isOwner(addr: string):
  return self._owner == addr

 */
