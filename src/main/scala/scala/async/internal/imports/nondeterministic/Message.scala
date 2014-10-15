package scala.async.internal.imports.nondeterministic

sealed trait Status
case object Pending extends Status
case object Claimed extends Status

case class Message[+A](content: A, source: Long) extends AbstractMessage {
  updateState(null, Pending)
  def tryClaim(): Boolean = updateState(Pending, Claimed)
  // Only call unclaim on messages with the thread which has claimed them
  def unclaim(): Unit = { _ref = Pending }
  def status(): Status = getState().asInstanceOf[Status]
}

// We use a message to implement the claiming of a single queue, it therefore does not
// use the source-field.
class QueueLock extends Message[Unit]((), -1)
object QueueLock {
  def apply() = new QueueLock()
}
