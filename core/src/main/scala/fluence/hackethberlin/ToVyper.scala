package fluence.hackethberlin

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import reflect.runtime.universe.ValDef
import reflect.runtime.universe.Tree
import fluence.hackethberlin.types.{PrimitiveType, _}

import reflect.runtime.universe.{Liftable, Quasiquote}


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

        implicit val lift: c.universe.Liftable[(String, PrimitiveType)] = c.universe.Liftable[(String, PrimitiveType)] {
          case (l, r) =>
            q"($l -> $r)"
        }

        val params = paramss.map(p => mapParam(p.asInstanceOf[ValDef])).foldLeft[CTree](q"HNil")(
          (acc, elem) => q"$elem :: $acc"
        )
        c.Expr[Any](
          q"""
          class $tpname(..$paramss) {
            def toVyper = {
              FuncDef("__init__", $params, uint256)
            }
          }
        """)

//      case Some(cls @ ClassDef(_, typeName, typeParams, impl)) =>
//        println("CLASSDEF MATCHED")
//
//        c.Expr[Any](
//          q"""
//          class $typeName(owner: String) {
//            def toVyper = {
//              FuncDef("__init__", ("a" -> address) :: HNil, uint256)
//            }
//          }
//        """)

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
