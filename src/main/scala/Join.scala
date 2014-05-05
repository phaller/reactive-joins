import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

object Join {
  class Pattern[A](f: Future[A]) {
    def unapply(x: Pattern[A]): A = ???
    def get: A = ???
    def isEmpty: Boolean = ???
  }
  object && {
    def unapply[A, B](a: Pattern[A], b: Pattern[B]): Pattern[(A, B)] = ???
  }
  def join[A](pf: PartialFunction[Pattern[A], A]): Future[A] = ???
}

object test extends App {

  import Join._
  val f = Future { 42 }
  val g = Future { 43 }
  val h = Future { 44 }

  object f1 extends Pattern(f)
  object f2 extends Pattern(g)
  object f3 extends Pattern(h)

  join {
    case f1(x) && f2(y) => x + y
    case f3(x) => x
  }

  /*
    Problems with the Join-Syntax:

    We need to trick to compiler into accepting the types, then we
    can do whatever we want with macros. This means, the efficiency
    be achieved by other means.

    We can convert futures implicitly into pattern objects. Syntactically,
    there seems to be a problem with using variable references at "extractor"
    positions. In the docs it says patterns in form of "Something(x)" mean
    that "Something" (in that case) has to be be a "Stable Identifier",
    which is a "Path" with "Id" at the end.

    We could force the user to create objects that extend Pattern, which type-checks,
    but is not particularly nice.

   */
}
