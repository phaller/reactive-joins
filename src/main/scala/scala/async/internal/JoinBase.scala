package scala.async.internal

import language.experimental.macros
import scala.reflect.macros.blackbox.Context

abstract class JoinBase {
  type RS <: ReactiveSystem
  val reactiveSystem: RS

  def joinImpl[A: c.WeakTypeTag](c: Context)(pf: c.Tree): c.Tree = {
    val joinMacro = JoinMacro(c)
    val code = joinMacro.joinTransform[A](pf)
    Debug.printCT(code)
    code
  }
}