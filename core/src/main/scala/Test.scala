package scala.async

import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global
import scala.async.Join._

object Test extends App {

  val f = Future { 42 }
  val g = Future { 43 }
  val h = Future { 44 }
  val k = Future { 45 }

  /* Create pattern objects from futures using the "p" method */
  val (f1, f2, f3, f4) = (f.p, g.p, h.p, k.p)

  /* Coordinate futures with a join pattern! */
  val fut = join {
    case f1(x) && f2(y) && f3(z) => x + y + z
    case f3(x) || f4(_) => x
  }
}
