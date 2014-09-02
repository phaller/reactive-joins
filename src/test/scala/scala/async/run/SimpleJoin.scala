package scala.async

import scala.async.Join._
import org.junit.Test
import rx.lang.scala.Observable
import rx.lang.scala.Observer
import rx.lang.scala.schedulers._
import rx.lang.scala.subjects._
import scala.concurrent.duration._
import scala.language.postfixOps

class AsyncSpec {

  val random = new scala.util.Random

  def randomNonZeroEvenInteger(max: Int) = 2 * (random.nextInt(max / 2) + 1)

  val newThreadScheduler = NewThreadScheduler()

  val maxListSize = 50 

  @Test
  def unaryJoin() = {
    val input = (1 to randomNonZeroEvenInteger(maxListSize)).toList
    val fn = (x: Int) => x + 1
    val expected = input.map(fn)

    val o1 = Observable.just(input:_*).observeOn(newThreadScheduler).p
    
    val obs = join {
      case o1(x) => Next(fn(x))
      case o1.done => Done
    }

    val result = obs.toBlocking.toList
    
    assert(result == expected)
  }

  @Test
  def unaryJoinError() = {
    import scala.collection.JavaConversions._
   
    val size = randomNonZeroEvenInteger(2)
    val o1 = Observable.just(1 to size: _*).map(x => 
        if (x % size == 0) throw new Exception("") else x
    ).observeOn(newThreadScheduler).p

    val obs = join {
      case o1.error(e) => Next(e)
    }

    assert(obs.toBlocking.first.isInstanceOf[Throwable])
  }

  @Test
  def unaryJoinDone() = {
    val input = (1 to randomNonZeroEvenInteger(maxListSize)).toList
    val o1 = Observable.just(input: _*).observeOn(newThreadScheduler).p
    
    val obs = join {
      case o1.done => Next(true)
    }
    
    assert(obs.toBlocking.first)
  }

   @Test
  def unaryJoinGuard() = {
    val o1 = Observable.just(1, 2).p

    var received = false
    val obs = join {
      case o1(x) if !received => 
        received = true
        Next(true)
      case o1(x) if received => Done
    }
    
    assert(obs.toBlocking.first)
  }

  @Test
  def joinResultImplementedCorrectly() = {
    val input = (1 to randomNonZeroEvenInteger(maxListSize)).toList
    val o1 = Observable.just(input: _*).p

    var received = false
    val obs = join {
      case o1(x) if !received => 
        received = true
        Pass
      case o1(x) if received => Next(x)
      case o1.done => Done
    }
    
    assert(obs.toBlocking.toList == input.tail)
  }

  @Test
  def joinResultImplicitPassWorks() = {
    val input = (1 to randomNonZeroEvenInteger(maxListSize)).toList
    val o1 = Observable.just(input: _*).p

    var received = false
    val obs = join {
      case o1(x) if !received => received = true
      case o1(x) if received => Next(x)
      case o1.done => Done
    }
    
    assert(obs.toBlocking.toList == input.tail)
  }


 // TODO: Find a way to test this. Try Mockito again?
 // @Test
 //  def `unary join throw`() = {
 //    val input = (1 to randomNonZeroEvenInteger(maxListSize)).toList
 //    val o1 = Observable.items(input: _*).observeOn(newThreadScheduler).p
    
 //    val obs = join {
 //      case o1.done => throw new Exception("")
 //    }
    
 //  }

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

  @Test
  def joinRespectsPatternOrder() = {
    import rx.lang.scala.JavaConversions._
    import scala.collection.JavaConversions._    
    import java.util.concurrent.TimeUnit
    // We use some RxJava (*not* RxScala) classes
    import rx.subjects.TestSubject
    import rx.schedulers.Schedulers

    val testScheduler = Schedulers.test() // RxJava TestScheduler

    val s1 = TestSubject.create[Int](testScheduler) // RxJava TestSubject
    val s2 = TestSubject.create[Int](testScheduler)
    val s3 = TestSubject.create[Int](testScheduler)

    val o1 = toScalaObservable[Int](s1).observeOn(testScheduler).p
    val o2 = toScalaObservable[Int](s2).observeOn(testScheduler).p
    val o3 = toScalaObservable[Int](s3).observeOn(testScheduler).p

    val obs = join {
      case o1(x) && o2(y) => Next(true)
      case o1(x) && o3(y) => Done
    }

    s2.onNext(2, 1)
    s3.onNext(3, 1)
    s1.onNext(1, 2)
    s1.onNext(1, 2)

    testScheduler.advanceTimeTo(1, TimeUnit.MILLISECONDS)
    testScheduler.advanceTimeTo(2, TimeUnit.MILLISECONDS)

    assert(obs.toBlocking.toList.head)
  }

  @Test
  def coVariantJoinReturn() = {
    sealed trait Animal
    case class Dog(name: String) extends Animal
    case class Chicken(name: String) extends Animal

    val numberOfEvents = randomNonZeroEvenInteger(maxListSize)

    val o1 = Observable.just(List.fill(numberOfEvents)(1): _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
    val o2 = Observable.just(List.fill(numberOfEvents)(2): _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p

    val obs = join {
      case o1(x) => Next(Dog("Lassie"))
      case o2(x) => Next(Chicken("Pgack"))
      case o1.done && o2.done => Done
    }

    val result = obs.toBlocking.toList
    assert(result.count(_.isInstanceOf[Dog]) == numberOfEvents)
    assert(result.count(_.isInstanceOf[Chicken]) == numberOfEvents)
    assert(result.count(_.isInstanceOf[Animal]) == 2 * numberOfEvents)
  }

}