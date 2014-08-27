package scala.async

import language.experimental.macros
import scala.language.implicitConversions

import scala.concurrent.{Future, Promise}
import rx.lang.scala.Observable

// Enables join-syntax for Observables in partial-functions

// Be very careful when changing any names in this object: you'll need 
// to refactor many pattern-matches where the distinct types are recognized by
// matching against hard-coded TermNames. For example: Done is could
// be matched against by with "Select(_, TermName("Done"))".
object Join {

  class JoinObservable[A](val observable: Observable[A]) {
    def unapply(obj: Any): Option[A] = ???
    object error {
      def unapply(obj: Any): Option[Throwable] = ???
    }
    case object done extends JoinObservable(observable)
  }

  object JoinObservable {
    def apply[A](obs: Observable[A]) = new JoinObservable(obs)
  }

  object && {
    def unapply(obj: Any): Option[(JoinObservable[_], JoinObservable[_])] = ???
  }

  object || {
    def unapply(obj: Any): Option[(JoinObservable[_], JoinObservable[_])] = ???
  }

  implicit class ObservableJoinOps[A](obs: Observable[A]) {
    def p = JoinObservable(obs)
  }

  sealed trait JoinReturn[+A]
  case class Next[A](a: A) extends JoinReturn[A]
  case object Done extends JoinReturn[Nothing]
  case object Pass extends JoinReturn[Nothing]

  implicit def unitToPass(a: Unit): JoinReturn[Nothing] = Pass

  def join[A](pf: PartialFunction[JoinObservable[_], JoinReturn[A]]): Observable[A] = macro internal.JoinBase.joinImpl[A]
  
}
