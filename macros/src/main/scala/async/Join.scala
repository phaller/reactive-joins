package scala.async

import language.experimental.macros
import scala.language.implicitConversions
import rx.Observable

/* Enables join-syntax for Observables in partial-functions */

object Join {

  class JoinObservable[A](val observable: Observable[A]) {
    def unapply(obj: Any): Option[A] = ???
    object error {
      def unapply(obj: Any): Option[A] = ???
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

  def join[A](pf: PartialFunction[JoinObservable[_], A]): A = macro internal.JoinBase.joinImpl[A]

}
