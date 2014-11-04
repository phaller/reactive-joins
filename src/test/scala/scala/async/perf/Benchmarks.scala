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

  val sumSizes = Gen.range("LockFreeJoins")(10, 10, 10)

  performance of "zipMap" config (
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 30,
    exec.independentSamples -> 1) in {

      using(sumSizes) curve ("Us") in { size =>

        var j = 0
        while (j < size) {
          val internalSize = 1024

          val s1 = Subject[Int]
          val s2 = Subject[Int]
          val s3 = Subject[Int]
          val s4 = Subject[Int]
          val s5 = Subject[Int]
          val s6 = Subject[Int]

          val gotAll = new CountDownLatch(internalSize)
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
              if (counter.get == (internalSize * 2)) { latch.countDown }
              Next(())
            }
            case o4(x) && o5(y) && o6(z) => {
              counter.incrementAndGet()
              if (counter.get == (internalSize * 2)) { latch.countDown }
              Next(())
            }
          }.serialize

          obs.subscribe((_: Unit) => gotAll.countDown,
            (_: Throwable) => (),
            () => ())

          val thread1 = sendIndexFromThread(s1, internalSize)
          val thread2 = sendIndexFromThread(s2, internalSize)
          val thread3 = sendIndexFromThread(s3, internalSize)
          val thread4 = sendIndexFromThread(s4, internalSize)
          val thread5 = sendIndexFromThread(s5, internalSize)
          val thread6 = sendIndexFromThread(s6, internalSize)

          thread1.start()
          thread2.start()
          thread3.start()
          thread4.start()
          thread5.start()
          thread6.start()

          latch.await
          gotAll.await

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
          val internalSize = 1024

          val s1 = Subject[Int]
          val s2 = Subject[Int]
          val s3 = Subject[Int]
          val s4 = Subject[Int]
          val s5 = Subject[Int]
          val s6 = Subject[Int]

          val gotAll = new CountDownLatch(internalSize)
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
            if (counter.get == (internalSize * 2)) { latch.countDown }
          })

          val p2 = RxJoinObservable.from(o4).and(o5).and(o6).then((x: Int, y: Int, z: Int) => {
            counter.incrementAndGet()
            if (counter.get == (internalSize * 2)) { latch.countDown }
          })

          val result = RxJoinObservable.when(p1, p2).toObservable

          result.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())

          val thread1 = sendIndexFromThread(s1, internalSize)
          val thread2 = sendIndexFromThread(s2, internalSize)
          val thread3 = sendIndexFromThread(s3, internalSize)
          val thread4 = sendIndexFromThread(s4, internalSize)
          val thread5 = sendIndexFromThread(s5, internalSize)
          val thread6 = sendIndexFromThread(s6, internalSize)

          thread1.start()
          thread2.start()
          thread3.start()
          thread4.start()
          thread5.start()
          thread6.start()

          latch.await
          gotAll.await

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