package scala.async.tests

import scala.async.Join._
import rx.lang.scala._
import rx.observables.{ JoinObservable => RxJoinObservable }
import scala.async.tests.Util._
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import rx.lang.scala.Subject
import rx.lang.scala.subjects.ReplaySubject
import org.scalameter.api._

class RxReactBench extends PerformanceTest.OfflineReport {

  def sendIndexFromThread(s: Subject[Int], repeats: Int) = new Thread(new Runnable {
    def run() {
      var i = 0
      while (i < repeats) {
        s.onNext(i)
        i = i + 1
      }
      s.onCompleted()
    }
  })

  val sumSizes = Gen.range("LockFreeJoins")(100, 100, 10)

  performance of "zipMap" config (
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 30,
    exec.independentSamples -> 1) in {

      using(sumSizes) curve ("Us") in { size =>

        var j = 0
        while (j < size) {
          val size = 1024

          val s1 = Subject[Int]
          val s2 = Subject[Int]
          val s3 = Subject[Int]
          val s4 = Subject[Int]
          val s5 = Subject[Int]
          val s6 = Subject[Int]

          val latch = new CountDownLatch(1)
          val counter = new AtomicInteger(0)

          val o1 = s1.observeOn(newThreadScheduler).p
          val o2 = s2.observeOn(newThreadScheduler).p
          val o3 = s3.observeOn(newThreadScheduler).p
          val o4 = s4.observeOn(newThreadScheduler).p
          val o5 = s5.observeOn(newThreadScheduler).p
          val o6 = s6.observeOn(newThreadScheduler).p

          val obs = join {
            case o1(x) && o2(y) && o3(z) => {
              counter.incrementAndGet()
              if (counter.get == (size * 2)) { latch.countDown }
              Pass
            }
            case o4(x) && o5(y) && o6(z) => {
              counter.incrementAndGet()
              if (counter.get == (size * 2)) { latch.countDown }
              Pass
            }
          }

          obs.subscribe((_: Unit) => (),
            (_: Throwable) => (),
            () => ())

          val thread1 = sendIndexFromThread(s1, size)
          val thread2 = sendIndexFromThread(s2, size)
          val thread3 = sendIndexFromThread(s3, size)
          val thread4 = sendIndexFromThread(s4, size)
          val thread5 = sendIndexFromThread(s5, size)
          val thread6 = sendIndexFromThread(s6, size)

          thread1.start()
          thread2.start()
          thread3.start()
          thread4.start()
          thread5.start()
          thread6.start()

          latch.await

          thread1.join()
          thread2.join()
          thread3.join()
          thread4.join()
          thread5.join()
          thread6.join()
          j = j + 1
        }
      }

      using(sumSizes) curve ("ReactiveX") in { size =>
        import rx.lang.scala.ImplicitFunctionConversions._
        import rx.lang.scala.JavaConversions._

        var j = 0
        while (j < size) {
          val size = 1024

          val s1 = Subject[Int]
          val s2 = Subject[Int]
          val s3 = Subject[Int]
          val s4 = Subject[Int]
          val s5 = Subject[Int]
          val s6 = Subject[Int]

          val latch = new CountDownLatch(1)
          val counter = new AtomicInteger(0)

          val o1 = s1.observeOn(newThreadScheduler)
          val o2 = s2.observeOn(newThreadScheduler)
          val o3 = s3.observeOn(newThreadScheduler)
          val o4 = s4.observeOn(newThreadScheduler)
          val o5 = s5.observeOn(newThreadScheduler)
          val o6 = s6.observeOn(newThreadScheduler)

          val p1 = RxJoinObservable.from(o1).and(o2).and(o3).then((x: Int, y: Int, z: Int) => {
            counter.incrementAndGet()
            if (counter.get == (size * 2)) { latch.countDown }
          })

          val p2 = RxJoinObservable.from(o4).and(o5).and(o6).then((x: Int, y: Int, z: Int) => {
            counter.incrementAndGet()
            if (counter.get == (size * 2)) { latch.countDown }
          })

          val result = RxJoinObservable.when(p1, p2).toObservable

          result.subscribe((_: Unit) => (), (_: Throwable) => (), () => ())

          val thread1 = sendIndexFromThread(s1, size)
          val thread2 = sendIndexFromThread(s2, size)
          val thread3 = sendIndexFromThread(s3, size)
          val thread4 = sendIndexFromThread(s4, size)
          val thread5 = sendIndexFromThread(s5, size)
          val thread6 = sendIndexFromThread(s6, size)

          thread1.start()
          thread2.start()
          thread3.start()
          thread4.start()
          thread5.start()
          thread6.start()

          latch.await

          thread1.join()
          thread2.join()
          thread3.join()
          thread4.join()
          thread5.join()
          thread6.join()
          j = j + 1
        }
      }
    }
}