package scala.async.internal

// There are two ways to get output from the join transform: at compile-time, or run-time.
// The compile-time printCT will print anything during compilation, if the "debug" system
// property was set, e.g. "sbt -Dscala.async.debug=true". The run-time "insertIfTracing" will include
// a tree at the position if the trace system property was set, e.g. "sbt -Dscala.async.trace=true".
// The "insertIfTracing" function lives in Util, because it has a dependency on the context
// type c.Tree, but it accesses the trace value in this module.
object Debug {
  private def enabled(level: String) = sys.props.getOrElse(s"scala.async.$level", "false").equalsIgnoreCase("true")

  private[async] def debug = enabled("debug")
  private[async] def trace = enabled("trace")

  def printCT(s: => Any): Unit = if (debug) println(s"[join] $s")
}