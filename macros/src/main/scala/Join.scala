package scala.async

import language.experimental.macros
import scala.reflect.macros.blackbox
import scala.language.implicitConversions
import rx.Observable

object Join {
  /* Code related for enabling our syntax in partial functions */
  class Pattern[A](val observable: Observable[A]) {
    def unapply(x: Any): Option[A] = ???
  }
  object Pattern {
    def apply[A](o: Observable[A]) = new Pattern(o)
  }
  object && {
    def unapply(obj: Any): Option[(Pattern[_] , Pattern[_])] = ???
  }
  object ||  {
    def unapply(obj: Any): Option[(Pattern[_], Pattern[_])] = ???
  }
  implicit class ObservableJoinOps[A](o: Observable[A]) {
    def p: Pattern[A] = Pattern(o)
  }
  /* The macros implementing the transform */

  def async[A](body: =>Any): Observable[A] = macro asyncImpl[A]

  def asyncImpl[A: c.WeakTypeTag](c: blackbox.Context)(body: c.Tree): c.Tree = {
    import c.universe._
    q""
  }

  def join[A](pf: PartialFunction[Pattern[_], A]): A = macro joinImpl[A]

  def joinImpl[A: c.WeakTypeTag](c: blackbox.Context)(pf: c.Tree): c.Tree = {
    import c.universe._

    /* Functionality of join:
        Analysis:
        1. Dead-code elimination (-> How to throw errors?)
        2. Determinism check
        Functionality:
        3. Transform
    */
    q""
  }
}

