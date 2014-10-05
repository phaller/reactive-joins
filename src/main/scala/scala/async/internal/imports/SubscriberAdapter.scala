package scala.async.internal.imports

import rx.lang.scala.Subscriber

trait SubscriberAdapter[T] extends Subscriber[T] {
  // Add a method to expose the protected `request` method
  def requestMore(n: Long): Unit
}