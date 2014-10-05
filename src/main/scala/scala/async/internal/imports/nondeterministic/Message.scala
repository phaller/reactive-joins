package scala.async.internal.imports.nondeterministic

object Status extends Enumeration {
  val Pending = Value("Pending")
  val Claimed = Value("Claimed")
}

case class Message[A](content: A) extends AbstractMessage {
  updateState(null, Status.Pending)
  def tryClaim(): Boolean = updateState(Status.Pending, Status.Claimed)
  // Only call unclaim on messages with the thread which has claimed them
  def unclaim() = { _ref = Status.Pending }
  def status: Status.type = getState().asInstanceOf[Status.type]
}
