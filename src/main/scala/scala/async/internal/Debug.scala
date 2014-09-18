package scala.async.internal

// The compile-time printCT will print anything during compilation, if the "debug" system
// property was set, e.g. "sbt -Dscala.async.joins.debug=true".
object Debug {

  private[async] def debug = sys.props.getOrElse(s"scala.async.joins.debug", "false").equalsIgnoreCase("true")

  def printCT(s: => Any): Unit = if (debug) println(s"[join] $s")
}