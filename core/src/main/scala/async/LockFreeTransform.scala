import rx.lang.scala.Observable
import scala.collection.mutable
import scala.concurrent.duration._

object LockFreeTransform {

  object MessageStatus extends Enumeration {
    type Status = Value
    val Pending, Claimed, Consumed = Value
  }

  class Message[T](value: T) {
    var status = MessageStatus.Pending
  }

  val obs1 = Observable.interval(200 millis)
  val obs2 = Observable.interval(400 millis)
  val obs3 = Observable.interval(100 millis)

  // Lock-free queue sample transform

  // The patterns are traversed in order for checking.

  // TODO: Lazy
  val obs1_buffer = mutable.Queue[Message[_]]()
  val obs2_buffer = mutable.Queue[Message[_]]()
  val obs3_buffer = mutable.Queue[Message[_]]()

  // We get the messages from the observables
  // obs1.subscribe(msg => ...)
  obs1.subscribe(msg => {
    // for every pattern that we are in
    obs1_buffer.enqueue(new Message(msg))
  })

  obs2.subscribe(msg => {
    obs2_buffer.enqueue(new Message(msg))
  })

  obs3.subscribe(msg => {
    obs3_buffer.enqueue(new Message(msg))
  })

  // If we want to synchronize on the messages like
  // the guys from scalable-join-patterns, then we
  // need some status field, or similar, on every message.
  // That means additional allocation costs...

  // Maybe we could avoid allocating these objects
  // if we put some flags on the buffers?

  // Could we assume bounded buffers as we can
  // control back-pressure?

  // The problem we additionally have is the one of
  // ordering; a property which observables guarantee.
}
