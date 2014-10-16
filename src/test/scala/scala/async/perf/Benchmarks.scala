package scala.async.tests

import scala.async.Join._
import rx.lang.scala._
import rx.observables.{JoinObservable => RxJoinObservable}
import scala.async.tests.Util._

import org.scalameter.api._

class RxReactBench extends PerformanceTest.OfflineReport {
  
  val javaNewThreadScheduler = rx.schedulers.Schedulers.newThread()

  val sumSizes = Gen.range("size")(100000, 1000000, 100000)

  performance of "zipMap" config(
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 30,
    exec.independentSamples -> 1
  ) in {

    using(sumSizes) curve("AsyncJoins") in { size =>
      val s1 = Subject[Int]
      val s2 = Subject[Int]

      val o1 = s1.p
      val o2 = s2.p
      
      // val o1 = s1.observeOn(newThreadScheduler).p
      // val o2 = s2.observeOn(newThreadScheduler).p

      val obs = join {
        case o1(x) && o2(y) => Next(x + y)
        case o1.done && o2.done => Done
      }
      var i = 0
      while (i < size) {
        s1.onNext(i)
        s2.onNext(i)
        i += 1
      }
    }

    using(sumSizes) curve("RxJoins") in { size =>
      import rx.subjects._
      import rx.lang.scala.ImplicitFunctionConversions._

      val s1 = PublishSubject.create[Int]()
      val s2 = PublishSubject.create[Int]()

      // RxJoinObservable.when(RxJoinObservable.from(s1.observeOn(javaNewThreadScheduler)).and(s2.observeOn(javaNewThreadScheduler)).then((x: Int, y: Int) => (x + y)))

      RxJoinObservable.when(RxJoinObservable.from(s1).and(s2).then((x: Int, y: Int) => (x + y)))

      var i = 0
      while (i < size) {
        s1.onNext(i)
        s2.onNext(i)
        i += 1
      }
    }
  }

  performance of "MergezipMap" config(
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 30,
    exec.independentSamples -> 1
  ) in {

    using(sumSizes) curve("AsyncJoins") in { size =>
      val s1 = Subject[Int]
      val s2 = Subject[Int]
      val s3 = Subject[Int]

      val o1 = s1.p
      val o2 = s2.p
      val o3 = s2.p

      // val o1 = s1.observeOn(newThreadScheduler).p
      // val o2 = s2.observeOn(newThreadScheduler).p
      // val o3 = s2.observeOn(newThreadScheduler).p

      val obs = join {
        case o1(x) && o2(y) => Next(x + y)
        case o1(x) && o3(y) => Next(x + y)
        case o1.done && o2.done => Done
      }
      var i = 0
      while (i < size) {
        s1.onNext(i)
        s1.onNext(i)
        s2.onNext(i)
        s3.onNext(i)
        i += 1
      }
    }

    using(sumSizes) curve("RxJoins") in { size =>
      import rx.subjects._
      import rx.lang.scala.ImplicitFunctionConversions._

      val s1 = PublishSubject.create[Int]()
      val s2 = PublishSubject.create[Int]()
      val s3 = PublishSubject.create[Int]()

      // val first = RxJoinObservable.from(s1.observeOn(javaNewThreadScheduler)).and(s2.observeOn(javaNewThreadScheduler)).then((x: Int, y: Int) => (x + y))
      // val second = RxJoinObservable.from(s1.observeOn(javaNewThreadScheduler)).and(s3.observeOn(javaNewThreadScheduler)).then((x: Int, y: Int) => (x + y))
      // RxJoinObservable.when(first, second)

      val first = RxJoinObservable.from(s1).and(s2).then((x: Int, y: Int) => (x + y))
      val second = RxJoinObservable.from(s1).and(s3).then((x: Int, y: Int) => (x + y))
      RxJoinObservable.when(first, second)


      var i = 0
      while (i < size) {
        s1.onNext(i)
        s1.onNext(i)
        s2.onNext(i)
        s3.onNext(i)
        i += 1
      }
    }
  }
}