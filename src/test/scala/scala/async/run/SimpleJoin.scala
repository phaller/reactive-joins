package scala.async

import scala.async.Join._
import org.junit.Test
import rx.lang.scala.Observable
import scala.concurrent.duration._

class AsyncSpec {

  @Test
  def `unary join`() = {
    val o1 = Observable.items(1).p
    
    val obs: Observable[Int] = join {
      case o1(x) => Some(x + 1)
      case o1.done => None
    }

    assert(obs.map(x => x == 2).toBlocking.first)
  }

  // @Test
  // def `unary join error`() = {
  //   val o1 = Observable.items(1).p
    
  //   val obs = join {
  //     case o1.error(e) => Some(e)
  //   }

  //   assert(obs.map(x => x == 2).toBlocking.first)
  // }

  // @Test
  // def `unary join done`() = {
  //   val o1 = Observable.items(1).p
    
  //   val obs = join {
  //     case o1.done => Some(2)
  //   }

  //   assert(obs.map(x => x == 2).toBlocking.first)
  // }

  // @Test
  // def `binary join`() = {
  //   val o1 = Observable.items(1).p
  //   val o2 = Observable.items(1).p
    
  //   val obs = join {
  //     case o1(x) && o2(y) => Some(x + y)
  //   }

  //   assert(obs.map(x => x == 2).toBlocking.first)
  // }

}