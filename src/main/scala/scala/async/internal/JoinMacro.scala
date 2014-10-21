package scala.async.internal

import language.experimental.macros
import scala.reflect.macros.blackbox.Context

trait JoinMacro { 
  self: Transform =>
  val c: Context
}

trait DeterministicChoice extends JoinMacro with LockTransform with Parse with RxJavaSystem with ReactiveSystemHelper with Backpressure with Util

trait NonDeterministicChoice extends JoinMacro with LockFreeTransform with Parse with RxJavaSystem with ReactiveSystemHelper with Backpressure with Util

object JoinMacro {


  def apply(c0: Context) = {
    import c0.universe._
    import scala.async.Join.{CheckOrder, InOrder, NoOrder}

    val checkOrder = c0.inferImplicitValue(typeOf[CheckOrder])
    
    if (checkOrder.tpe == typeOf[InOrder.type]) {
      Debug.printCT("The transform used implements deterministic choice.") 
      new DeterministicChoice {
        val c: c0.type = c0
      }
    }
    else
    {
      Debug.printCT("The transform used implements non-deterministic choice.") 
      new NonDeterministicChoice {
        val c: c0.type = c0
      }
    }
  }
}