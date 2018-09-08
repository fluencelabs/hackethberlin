package fluence.hackethberlin.macrogen

import fluence.hackethberlin.{types, Decorator, FuncDef}
import shapeless._

import scala.annotation.StaticAnnotation
import scala.meta._

class ToVyper extends StaticAnnotation {

  // .${params.map(mapParam)} ::
  inline def apply(defn: Any): Any = meta {
    defn match {
      case cls @ Defn.Class(_, name, _, ctor, template) =>
        val params = ctor.paramss.head
        val tree: Tree =
          q"""
          FuncDef("__init__", HNil, uint256)
          """
        tree

      case _ => throw new Exception()
    }
  }

  def mapType(t: Type) = {
    t match {
      case Name("String") => "string"
    }
  }

  def mapParam(p: Term.Param) = {
    s"${p.name}" -> s"${mapType(p.decltpe.get)}"
  }
}

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
