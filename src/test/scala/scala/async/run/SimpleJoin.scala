package scala.async

import scala.async.Join._
import org.junit.Test
import rx.Observable
import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class AsyncSpec {
  @Test
  def `simple join`() = {
    val f = Observable.just(1)
    val o1 = f.p
    val o2 = Observable.just(1).p

    val obs: Future[Int] = join {
        case o1(x) && o2(y) => x + y
    }

     assert(Await.result(obs, 0 nanos) == 2)
  }
}