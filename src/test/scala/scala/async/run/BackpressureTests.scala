package scala.async.tests

import Util._
import org.junit.Test

import scala.async.Join._
import rx.lang.scala.Observable

class BackpressureTests {

  @Test
  def setBufferSizeTest() = {
    val size = randomNonZeroEvenInteger(maxListSize)
    val input = List.fill(size)(randomNonZeroEvenInteger(maxListSize))

    val o1 = Observable.just(input: _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
    val o2 = Observable.just(input: _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p

    implicit val bufferSize = BufferSize(1)
    val obs = join {
      case o1(x) && o2(y) => Next(x + y)
      case o1.done && o2.done => Done
    }

    val result = obs.toBlocking.toList
    assert(result != input)
  }

}