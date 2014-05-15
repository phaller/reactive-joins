package scala.async

import scala.concurrent.{Future}
import language.experimental.macros
import scala.reflect.macros.blackbox
import scala.language.implicitConversions

object Join {
  class Pattern[A](f: Future[A]) {
    def unapply(x: Any): Option[A] = ???
  }
  object Pattern {
    def apply[A](f: Future[A]) = new Pattern(f)
  }
  object && {
    def unapply(obj: Any): Option[(Pattern[_] , Pattern[_])] = ???
  }
  object ||  {
    def unapply(obj: Any): Option[(Pattern[_], Pattern[_])] = ???
  }
  implicit class FutureJoinOps[A](f: Future[A]) {
    def p: Pattern[A] = Pattern(f)
  }

  /*
  def join[A](pf: PartialFunction[Pattern[_], A]): A = macro joinImpl[A]

  def joinImpl[A: c.WeakTypeTag](c: blackbox.Context)(pf: c.Expr[PartialFunction[Pattern[_], A]]): c.Expr[A] = {
    c.universe.reify(pf.value.apply(Pattern(Future{42})))
  }
  */

  /* TEST ZONE */
  def join[A](pf: PartialFunction[Pattern[_], A]): A = macro joinImpl[A]

  def joinImpl[A: c.WeakTypeTag](c: blackbox.Context)(pf: c.Tree): c.Tree = {
    import c.universe._

    /* Functionality of join:
        1. Dead-code elimination (-> How to throw errors?)
        2. Determinism check
        3. Transform
    */

    /* How to get the type: */
    val tpe = weakTypeOf[A]
    q""
  }
  /* END ZONE */
}

