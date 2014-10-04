package scala.async.tests

import Util._
import org.junit.Test

import scala.async.Join._
import rx.lang.scala.Observable

// Test the join keyword with multiple source Observables
class MultaryTests {

  @Test
  def binaryOrJoin() = {
    val size = randomNonZeroEvenInteger(maxListSize)
    val input = List.fill(size)(())

    val o1 = Observable.just(input: _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
    val o2 = Observable.just(input: _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
    
    val obs = join {
      case o1(x) => Next(x)
      case o2(y) => Next(y)
      case o1.done && o2.done => Done
    }

    val result = obs.toBlocking.toList
    assert(result.size == (size * 2))
  }

  @Test
  def binaryAndJoin() = {
    val input = (1 to randomNonZeroEvenInteger(maxListSize)).toList
    val fn = (x: Int, y: Int) => x + y
    val expected = input.zip(input).map({ case (x, y) => fn(x, y) })

    val o1 = Observable.just(input: _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
    val o2 = Observable.just(input: _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
    
    val obs = join {
      case o1(x) && o2(y) => Next(fn(x, y))
      case o1.done && o2.done => Done
    }

    val result = obs.toBlocking.toList
    assert(result == expected)
  }

    @Test
  def binaryAndOrJoin() = {
    val full = randomNonZeroEvenInteger(maxListSize)
    val half = full / 2

    val o1 = Observable.just(List.fill(full)(1): _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
    val o2 = Observable.just(List.fill(half)(2): _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
    val o3 = Observable.just(List.fill(half)(3): _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p

    val obs = join {
      case o1(x) && o2(y) => Next(true)
      case o1(x) && o3(y) => Next(false)
      case o1.done && o2.done && o3.done => Done
    }

    val result = obs.toBlocking.toList
    assert(result.filter(identity).size == half)
    assert(result.filter(x => !x).size == half)
  }

  // @Test
  // def joinRespectsPatternOrder() = {
  //   import rx.lang.scala.JavaConversions._
  //   import scala.collection.JavaConversions._    
  //   import java.util.concurrent.TimeUnit
  //   // We use some RxJava (*not* RxScala) classes
  //   import rx.subjects.TestSubject
  //   import rx.schedulers.Schedulers

  //   val testScheduler = Schedulers.test() // RxJava TestScheduler

  //   val s1 = TestSubject.create[Int](testScheduler) // RxJava TestSubject
  //   val s2 = TestSubject.create[Int](testScheduler)
  //   val s3 = TestSubject.create[Int](testScheduler)

  //   val o1 = toScalaObservable[Int](s1).observeOn(testScheduler).p
  //   val o2 = toScalaObservable[Int](s2).observeOn(testScheduler).p
  //   val o3 = toScalaObservable[Int](s3).observeOn(testScheduler).p

  //   val obs = join {
  //     case o1(x) && o2(y) => Next(true)
  //     case o1(x) && o3(y) => Done
  //   }

  //   s2.onNext(2, 1)
  //   s3.onNext(3, 1)
  //   s1.onNext(1, 2)
  //   s1.onNext(1, 2)

  //   testScheduler.advanceTimeTo(1, TimeUnit.MILLISECONDS)
  //   testScheduler.advanceTimeTo(2, TimeUnit.MILLISECONDS)

  //   assert(obs.toBlocking.toList.head)
  // }
}