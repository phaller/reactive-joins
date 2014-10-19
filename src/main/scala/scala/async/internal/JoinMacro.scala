package scala.async.internal

import language.experimental.macros
import scala.reflect.macros.blackbox.Context


//  self: JoinMacro with Parse with ReactiveSystem with ReactiveSystemHelper with Backpressure => 

trait JoinMacro extends LockFreeTransform with Parse with RxJavaSystem with ReactiveSystemHelper with Backpressure with Util {
  self: Transform =>
  val c: Context
}

// trait NonDeterministicChoice extends JoinMacro with LockFreeTransform with Parse with RxJavaSystem with ReactiveSystemHelper with Backpressure with Util

// trait DeterministicChoice extends JoinMacro with LockTransform with Parse with RxJavaSystem with ReactiveSystemHelper with Backpressure with Util

object JoinMacro {
  def apply(c0: Context) = {
    new JoinMacro {
      val c: c0.type = c0
    }
  }
}