package scala.async.internal

trait BackPressure {
  self: JoinMacro =>
  import c.universe._
  import scala.async.Join.BufferSize
  
  private def implicitBufferSizeValue = c.inferImplicitValue(typeOf[BufferSize])
  // For backpressure control we need to know the buffer size to use.
  def bufferSizeTree: c.Tree = q"${implicitBufferSizeValue}.size"
  // Required to determine whether we need to call request more than just once at initialization (onStart override in Subscriber)
  def unboundBuffer: Boolean = implicitBufferSizeValue match {
    case Select(_, TermName("defaultBufferSize")) => true
    case _ => false
  }
}