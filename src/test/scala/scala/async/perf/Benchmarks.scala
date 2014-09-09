package scala.async.tests

import scala.async.Join._
import rx.lang.scala.Observable
import org.scalameter.api._

object FirstBenchmark extends PerformanceTest.Quickbenchmark {
  
  val sizes = Gen.range("size")(300000, 1500000, 300000)

  val ranges = for {
    size <- sizes
  } yield 0 until size

  performance of "Range" in {
    measure method "map" in {
      using(ranges) in {
        r => r.map(_ + 1)
      }
    }
  }
}
// class Benchmarks {

//   @Test
//   def binaryOrJoin() = {
//     val size = randomNonZeroEvenInteger(maxListSize)
//     val input = List.fill(size)(())

//     val o1 = Observable.just(input: _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
//     val o2 = Observable.just(input: _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
    
//     val obs = join {
//       case o1(x) => Next(x)
//       case o2(y) => Next(y)
//       case o1.done && o2.done => Done
//     }

//     val result = obs.toBlocking.toList
//     assert(result.size == (size * 2))
//   }

// }