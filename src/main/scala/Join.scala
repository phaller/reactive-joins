import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

object Join {
  class Pattern[A](f: Future[A])
  object Pattern {
    def unapply[A](x: Pattern[A]): Option[A] = ???
  }
  object && {
    def unapply[A, B](a: Pattern[A], b: Pattern[B]): Option[(A, B)] = ???
  }
  def join[A](pf: PartialFunction[Any, A]): Future[A] = ???
}

object test extends App {

  def await[A](f: Future[A]): A = ???

  import Join._
  val f = Future { 42 }
  val g = Future { 10 }

  join {
    case `f`(x) => x // does not work
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

    We could force the user to create objects that extend Pattern.

   */
}
