package scala.async.internal

import language.experimental.macros
import scala.reflect.macros.blackbox.Context

trait JoinMacro extends LockTransform with Parse with RxJavaSystem with ReactiveSystemHelper with Backpressure with Util {
  self: Transform =>
  val c: Context
}

object JoinMacro {
  def apply(c0: Context) = {
    new JoinMacro {
      val c: c0.type = c0
    }
  }
}