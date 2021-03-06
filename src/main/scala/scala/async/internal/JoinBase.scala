package scala.async.internal

import language.experimental.macros
import scala.reflect.macros.blackbox.Context

object JoinBase {
  def joinImpl[A: c.WeakTypeTag](c: Context)(pf: c.Tree): c.Tree = {
    val joinMacro = JoinMacro(c)
    val code = joinMacro.joinTransform[A](pf)
    Debug.printCT(code)
    code
  }
}