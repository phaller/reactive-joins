package scala.async

import scala.async.Join._
import org.junit.Test
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.TestScheduler
import scala.concurrent.duration._

class AsyncSpec {

  @Test
  def `unary join`() = {
    val input = (1 to 10).toList
    val fn = (x: Int) => x + 1
    val expected = input.map(fn)

    val o1 = Observable.items(input:_*).p
    
    val obs = join {
      case o1(x) => Some(fn(x))
      case o1.done => None
    }

    val result = obs.toBlocking.toList
    
    assert(result == expected)
  }

  @Test
  def `unary join error`() = {
    val input = (1 to 10).toList
    val o1 = Observable.items(input: _*).map(_ => throw new Exception("")).p
    
    val obs = join {
      case o1.error(e) => Some(e)
    }

    val result = obs.map({ case e: Exception => true }).toBlocking

    assert(result.first)
  }

  @Test
  def `unary join done`() = {
    val input = (1 to 10).toList
    val output = 1
    val o1 = Observable.items(input: _*).p
    
    val obs = join {
      case o1.done => Some(output)
    }
    
    val result = obs.map(x => x == 1).toBlocking

    assert(result.first)
  }

  @Test
  def `binary or join`() = {
    val scheduler1 = TestScheduler()
    val scheduler2 = TestScheduler()

    val o1 = Observable.items(1).subscribeOn(scheduler1).p
    val o2 = Observable.items(2).subscribeOn(scheduler2).p
    
    val obs = join {
      case o1(x) => Some(x)
      case o2(y) => Some(y)
      case o1.done && o2.done => None
    }

    scheduler2.triggerActions()
    scheduler1.triggerActions()

    val result = obs.toBlocking.toList
    assert(result == List(2, 1))
  }

   @Test
  def `binary and join`() = {
    val scheduler1 = TestScheduler()
    val scheduler2 = TestScheduler()

    val input = (1 to 10).toList
    val fn = (x: Int, y: Int) => x + y
    val expected = input.zip(input).map({ case (x, y) => fn(x, y) })

    val o1 = Observable.items(input: _*).subscribeOn(scheduler1).p
    val o2 = Observable.items(input: _*).subscribeOn(scheduler2).p
    
    val obs = join {
      case o1(x) && o2(y) => Some(fn(x, y))
      case o1.done && o2.done => None
    }

    scheduler2.triggerActions()
    scheduler1.triggerActions()

    val result = obs.toBlocking.toList
    assert(result == expected)
  }

}