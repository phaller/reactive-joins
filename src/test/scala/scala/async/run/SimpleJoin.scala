package scala.async

import scala.async.Join._
import org.junit.Test
import rx.lang.scala.Observable
import scala.concurrent.duration._

class AsyncSpec {
  @Test
  def `binary join`() = {
    val o1 = Observable.items(1).p
    val o2 = Observable.items(1).p
    
    val obs = join {
      case o1(x) && o2(y) => Some(x + y)
      case o1.done && o2.done => None
    }

    assert(obs.map(x => x == 2).toBlocking.first)
  }

  // @Test
  // def  `single joinOnce next` = {
  //   val message = "1"
  //   val o1 = Observable.items(message).p

  //   val obs = joinOnce {
  //     case o1(x) => x
  //   }

  //   assert(obs.map(x => x == message).toBlocking.first)
  // }
}