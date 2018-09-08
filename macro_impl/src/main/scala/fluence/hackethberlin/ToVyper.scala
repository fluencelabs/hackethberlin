package fluence.hackethberlin

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

class ToVyper extends StaticAnnotation {

  def macroTransform(annottees: Any*) = macro ToVyper.impl
}

object ToVyper {

  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    println("ARE YOU PEEEDOR")

    annottees.headOption.map(_.tree) match {
      case q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends ..$parents { $self => ..$stats }" =>
        val className = tpname.toString()
        c.Expr[Any](q"""$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends ..$parents {
            override def toString: String = {
              println("Hello I'm " + $className + " and my name is " + name)
              "pyos"
            }
          }""")

//      case Some(cls @ ClassDef(_, typeName, params, impl)) =>
//        println(s"params are $params")
//        c.Expr[Any](
//          q"""
//          class $typeName
//          FuncDef("__init__", HNil, uint256)
//        """
//        )
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
//  def mapType(t: Type) = {
//    t match {
//      case Name("String") => "string"
//    }
//  }
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
