package scala.async.tests

import scala.async.Join._
import rx.functions._
import rx.lang.scala._
import rx.observables.{ JoinObservable => RxJoinObservable }
import scala.async.tests.Util._
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import rx.lang.scala.Subject
import rx.lang.scala.subjects.ReplaySubject
import org.scalameter.api._

class RxReactBench extends PerformanceTest.OfflineReport {

 implicit def scalaFunction8ToRxFunc8[A, B, C, D, E, F, G, H, I](fn: (A, B, C, D, E, F, G, H) => I): Func8[A, B, C, D, E, F, G, H, I] =
  new Func8[A, B, C, D, E, F, G, H, I] {
    def call(a: A, b: B, c: C, d: D, e :E, f: F, g: G, h: H) = fn(a, b, c, d, e, f, g, h)
  }

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

  val independentObservables = Gen.enumeration("Observables")(2, 4, 8, 16, 32)
  val iterations = 1
  val internalSize = 1024

  
  performance of "fullyIndependentTwoCase" config (
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 1000,
    exec.independentSamples -> 1,
    exec.jvmflags -> "-Xms64m -Xmx512m") in 
  {
      //////////////////////////////////////////////////////////////////////////////////////
      using(independentObservables) curve ("Us") in { independentObservablesNumber =>

        if (independentObservablesNumber == 2) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse).p
val o2 = s2.observeOn(schedulerToUse).p

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val obs = join {
      case o1(x) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
case o2(x) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
    }.serialize
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    val thread1 = sendIndexFromThread(s1, internalSize)
val thread2 = sendIndexFromThread(s2, internalSize)

    thread1.start()
thread2.start()

    latch.await
gotAll.await
    thread1.join()
thread2.join()

  
    
    j = j + 1
  }
        }
        if (independentObservablesNumber == 4) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse).p
val o2 = s2.observeOn(schedulerToUse).p
val o3 = s3.observeOn(schedulerToUse).p
val o4 = s4.observeOn(schedulerToUse).p

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val obs = join {
      case o1(x) && o2(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
case o3(x) && o4(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
    }.serialize
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    val thread1 = sendIndexFromThread(s1, internalSize)
val thread2 = sendIndexFromThread(s2, internalSize)
val thread3 = sendIndexFromThread(s3, internalSize)
val thread4 = sendIndexFromThread(s4, internalSize)

    thread1.start()
thread2.start()
thread3.start()
thread4.start()

    latch.await
gotAll.await
    thread1.join()
thread2.join()
thread3.join()
thread4.join()

  
    
    j = j + 1
  }
        }
        if (independentObservablesNumber == 8) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]
val s5 = Subject[Int]
val s6 = Subject[Int]
val s7 = Subject[Int]
val s8 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse).p
val o2 = s2.observeOn(schedulerToUse).p
val o3 = s3.observeOn(schedulerToUse).p
val o4 = s4.observeOn(schedulerToUse).p
val o5 = s5.observeOn(schedulerToUse).p
val o6 = s6.observeOn(schedulerToUse).p
val o7 = s7.observeOn(schedulerToUse).p
val o8 = s8.observeOn(schedulerToUse).p

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val obs = join {
      case o1(x) && o2(xx) && o3(xxx) && o4(xxxx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
case o5(x) && o6(xx) && o7(xxx) && o8(xxxx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
    }.serialize
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    val thread1 = sendIndexFromThread(s1, internalSize)
val thread2 = sendIndexFromThread(s2, internalSize)
val thread3 = sendIndexFromThread(s3, internalSize)
val thread4 = sendIndexFromThread(s4, internalSize)
val thread5 = sendIndexFromThread(s5, internalSize)
val thread6 = sendIndexFromThread(s6, internalSize)
val thread7 = sendIndexFromThread(s7, internalSize)
val thread8 = sendIndexFromThread(s8, internalSize)

    thread1.start()
thread2.start()
thread3.start()
thread4.start()
thread5.start()
thread6.start()
thread7.start()
thread8.start()

    latch.await
gotAll.await
    thread1.join()
thread2.join()
thread3.join()
thread4.join()
thread5.join()
thread6.join()
thread7.join()
thread8.join()

  
    
    j = j + 1
  }
        }
        if (independentObservablesNumber == 16) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]
val s5 = Subject[Int]
val s6 = Subject[Int]
val s7 = Subject[Int]
val s8 = Subject[Int]
val s9 = Subject[Int]
val s10 = Subject[Int]
val s11 = Subject[Int]
val s12 = Subject[Int]
val s13 = Subject[Int]
val s14 = Subject[Int]
val s15 = Subject[Int]
val s16 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse).p
val o2 = s2.observeOn(schedulerToUse).p
val o3 = s3.observeOn(schedulerToUse).p
val o4 = s4.observeOn(schedulerToUse).p
val o5 = s5.observeOn(schedulerToUse).p
val o6 = s6.observeOn(schedulerToUse).p
val o7 = s7.observeOn(schedulerToUse).p
val o8 = s8.observeOn(schedulerToUse).p
val o9 = s9.observeOn(schedulerToUse).p
val o10 = s10.observeOn(schedulerToUse).p
val o11 = s11.observeOn(schedulerToUse).p
val o12 = s12.observeOn(schedulerToUse).p
val o13 = s13.observeOn(schedulerToUse).p
val o14 = s14.observeOn(schedulerToUse).p
val o15 = s15.observeOn(schedulerToUse).p
val o16 = s16.observeOn(schedulerToUse).p

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val obs = join {
      case o1(x) && o2(xx) && o3(xxx) && o4(xxxx) && o5(xxxxx) && o6(xxxxxx) && o7(xxxxxxx) && o8(xxxxxxxx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
case o9(x) && o10(xx) && o11(xxx) && o12(xxxx) && o13(xxxxx) && o14(xxxxxx) && o15(xxxxxxx) && o16(xxxxxxxx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
    }.serialize
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    val thread1 = sendIndexFromThread(s1, internalSize)
val thread2 = sendIndexFromThread(s2, internalSize)
val thread3 = sendIndexFromThread(s3, internalSize)
val thread4 = sendIndexFromThread(s4, internalSize)
val thread5 = sendIndexFromThread(s5, internalSize)
val thread6 = sendIndexFromThread(s6, internalSize)
val thread7 = sendIndexFromThread(s7, internalSize)
val thread8 = sendIndexFromThread(s8, internalSize)
val thread9 = sendIndexFromThread(s9, internalSize)
val thread10 = sendIndexFromThread(s10, internalSize)
val thread11 = sendIndexFromThread(s11, internalSize)
val thread12 = sendIndexFromThread(s12, internalSize)
val thread13 = sendIndexFromThread(s13, internalSize)
val thread14 = sendIndexFromThread(s14, internalSize)
val thread15 = sendIndexFromThread(s15, internalSize)
val thread16 = sendIndexFromThread(s16, internalSize)

    thread1.start()
thread2.start()
thread3.start()
thread4.start()
thread5.start()
thread6.start()
thread7.start()
thread8.start()
thread9.start()
thread10.start()
thread11.start()
thread12.start()
thread13.start()
thread14.start()
thread15.start()
thread16.start()

    latch.await
gotAll.await
    thread1.join()
thread2.join()
thread3.join()
thread4.join()
thread5.join()
thread6.join()
thread7.join()
thread8.join()
thread9.join()
thread10.join()
thread11.join()
thread12.join()
thread13.join()
thread14.join()
thread15.join()
thread16.join()

  
    
    j = j + 1
  }
        }
        if (independentObservablesNumber == 32) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]
val s5 = Subject[Int]
val s6 = Subject[Int]
val s7 = Subject[Int]
val s8 = Subject[Int]
val s9 = Subject[Int]
val s10 = Subject[Int]
val s11 = Subject[Int]
val s12 = Subject[Int]
val s13 = Subject[Int]
val s14 = Subject[Int]
val s15 = Subject[Int]
val s16 = Subject[Int]
val s17 = Subject[Int]
val s18 = Subject[Int]
val s19 = Subject[Int]
val s20 = Subject[Int]
val s21 = Subject[Int]
val s22 = Subject[Int]
val s23 = Subject[Int]
val s24 = Subject[Int]
val s25 = Subject[Int]
val s26 = Subject[Int]
val s27 = Subject[Int]
val s28 = Subject[Int]
val s29 = Subject[Int]
val s30 = Subject[Int]
val s31 = Subject[Int]
val s32 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse).p
val o2 = s2.observeOn(schedulerToUse).p
val o3 = s3.observeOn(schedulerToUse).p
val o4 = s4.observeOn(schedulerToUse).p
val o5 = s5.observeOn(schedulerToUse).p
val o6 = s6.observeOn(schedulerToUse).p
val o7 = s7.observeOn(schedulerToUse).p
val o8 = s8.observeOn(schedulerToUse).p
val o9 = s9.observeOn(schedulerToUse).p
val o10 = s10.observeOn(schedulerToUse).p
val o11 = s11.observeOn(schedulerToUse).p
val o12 = s12.observeOn(schedulerToUse).p
val o13 = s13.observeOn(schedulerToUse).p
val o14 = s14.observeOn(schedulerToUse).p
val o15 = s15.observeOn(schedulerToUse).p
val o16 = s16.observeOn(schedulerToUse).p
val o17 = s17.observeOn(schedulerToUse).p
val o18 = s18.observeOn(schedulerToUse).p
val o19 = s19.observeOn(schedulerToUse).p
val o20 = s20.observeOn(schedulerToUse).p
val o21 = s21.observeOn(schedulerToUse).p
val o22 = s22.observeOn(schedulerToUse).p
val o23 = s23.observeOn(schedulerToUse).p
val o24 = s24.observeOn(schedulerToUse).p
val o25 = s25.observeOn(schedulerToUse).p
val o26 = s26.observeOn(schedulerToUse).p
val o27 = s27.observeOn(schedulerToUse).p
val o28 = s28.observeOn(schedulerToUse).p
val o29 = s29.observeOn(schedulerToUse).p
val o30 = s30.observeOn(schedulerToUse).p
val o31 = s31.observeOn(schedulerToUse).p
val o32 = s32.observeOn(schedulerToUse).p

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val obs = join {
      case o1(x) && o2(xx) && o3(xxx) && o4(xxxx) && o5(xxxxx) && o6(xxxxxx) && o7(xxxxxxx) && o8(xxxxxxxx) && o9(xxxxxxxxx) && o10(xxxxxxxxxx) && o11(xxxxxxxxxxx) && o12(xxxxxxxxxxxx) && o13(xxxxxxxxxxxxx) && o14(xxxxxxxxxxxxxx) && o15(xxxxxxxxxxxxxxx) && o16(xxxxxxxxxxxxxxxx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
case o17(x) && o18(xx) && o19(xxx) && o20(xxxx) && o21(xxxxx) && o22(xxxxxx) && o23(xxxxxxx) && o24(xxxxxxxx) && o25(xxxxxxxxx) && o26(xxxxxxxxxx) && o27(xxxxxxxxxxx) && o28(xxxxxxxxxxxx) && o29(xxxxxxxxxxxxx) && o30(xxxxxxxxxxxxxx) && o31(xxxxxxxxxxxxxxx) && o32(xxxxxxxxxxxxxxxx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
    }.serialize
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    val thread1 = sendIndexFromThread(s1, internalSize)
val thread2 = sendIndexFromThread(s2, internalSize)
val thread3 = sendIndexFromThread(s3, internalSize)
val thread4 = sendIndexFromThread(s4, internalSize)
val thread5 = sendIndexFromThread(s5, internalSize)
val thread6 = sendIndexFromThread(s6, internalSize)
val thread7 = sendIndexFromThread(s7, internalSize)
val thread8 = sendIndexFromThread(s8, internalSize)
val thread9 = sendIndexFromThread(s9, internalSize)
val thread10 = sendIndexFromThread(s10, internalSize)
val thread11 = sendIndexFromThread(s11, internalSize)
val thread12 = sendIndexFromThread(s12, internalSize)
val thread13 = sendIndexFromThread(s13, internalSize)
val thread14 = sendIndexFromThread(s14, internalSize)
val thread15 = sendIndexFromThread(s15, internalSize)
val thread16 = sendIndexFromThread(s16, internalSize)
val thread17 = sendIndexFromThread(s17, internalSize)
val thread18 = sendIndexFromThread(s18, internalSize)
val thread19 = sendIndexFromThread(s19, internalSize)
val thread20 = sendIndexFromThread(s20, internalSize)
val thread21 = sendIndexFromThread(s21, internalSize)
val thread22 = sendIndexFromThread(s22, internalSize)
val thread23 = sendIndexFromThread(s23, internalSize)
val thread24 = sendIndexFromThread(s24, internalSize)
val thread25 = sendIndexFromThread(s25, internalSize)
val thread26 = sendIndexFromThread(s26, internalSize)
val thread27 = sendIndexFromThread(s27, internalSize)
val thread28 = sendIndexFromThread(s28, internalSize)
val thread29 = sendIndexFromThread(s29, internalSize)
val thread30 = sendIndexFromThread(s30, internalSize)
val thread31 = sendIndexFromThread(s31, internalSize)
val thread32 = sendIndexFromThread(s32, internalSize)

    thread1.start()
thread2.start()
thread3.start()
thread4.start()
thread5.start()
thread6.start()
thread7.start()
thread8.start()
thread9.start()
thread10.start()
thread11.start()
thread12.start()
thread13.start()
thread14.start()
thread15.start()
thread16.start()
thread17.start()
thread18.start()
thread19.start()
thread20.start()
thread21.start()
thread22.start()
thread23.start()
thread24.start()
thread25.start()
thread26.start()
thread27.start()
thread28.start()
thread29.start()
thread30.start()
thread31.start()
thread32.start()

    latch.await
gotAll.await
    thread1.join()
thread2.join()
thread3.join()
thread4.join()
thread5.join()
thread6.join()
thread7.join()
thread8.join()
thread9.join()
thread10.join()
thread11.join()
thread12.join()
thread13.join()
thread14.join()
thread15.join()
thread16.join()
thread17.join()
thread18.join()
thread19.join()
thread20.join()
thread21.join()
thread22.join()
thread23.join()
thread24.join()
thread25.join()
thread26.join()
thread27.join()
thread28.join()
thread29.join()
thread30.join()
thread31.join()
thread32.join()

  
    
    j = j + 1
  }
        }
      }
      //////////////////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////////////////
      using(independentObservables) curve ("ReactiveX") in { independentObservablesNumber =>
        import rx.lang.scala.ImplicitFunctionConversions._
        import rx.lang.scala.JavaConversions._

        if (independentObservablesNumber == 2) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse)
val o2 = s2.observeOn(schedulerToUse)

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val p1 = RxJoinObservable.from(o1).then((x: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val p2 = RxJoinObservable.from(o2).then((x: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2).toObservable
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    val thread1 = sendIndexFromThread(s1, internalSize)
val thread2 = sendIndexFromThread(s2, internalSize)

    thread1.start()
thread2.start()

    latch.await
gotAll.await
    thread1.join()
thread2.join()

  
    
    j = j + 1
  }
        }

        if (independentObservablesNumber == 4) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse)
val o2 = s2.observeOn(schedulerToUse)
val o3 = s3.observeOn(schedulerToUse)
val o4 = s4.observeOn(schedulerToUse)

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val p1 = RxJoinObservable.from(o1).and(o2).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val p2 = RxJoinObservable.from(o3).and(o4).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2).toObservable
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    val thread1 = sendIndexFromThread(s1, internalSize)
val thread2 = sendIndexFromThread(s2, internalSize)
val thread3 = sendIndexFromThread(s3, internalSize)
val thread4 = sendIndexFromThread(s4, internalSize)

    thread1.start()
thread2.start()
thread3.start()
thread4.start()

    latch.await
gotAll.await
    thread1.join()
thread2.join()
thread3.join()
thread4.join()

  
    
    j = j + 1
  }
        }

        if (independentObservablesNumber == 8) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]
val s5 = Subject[Int]
val s6 = Subject[Int]
val s7 = Subject[Int]
val s8 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse)
val o2 = s2.observeOn(schedulerToUse)
val o3 = s3.observeOn(schedulerToUse)
val o4 = s4.observeOn(schedulerToUse)
val o5 = s5.observeOn(schedulerToUse)
val o6 = s6.observeOn(schedulerToUse)
val o7 = s7.observeOn(schedulerToUse)
val o8 = s8.observeOn(schedulerToUse)

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val p1 = RxJoinObservable.from(o1).and(o2).and(o3).and(o4).then((x: Int, xx: Int, xxx: Int, xxxx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val p2 = RxJoinObservable.from(o5).and(o6).and(o7).and(o8).then((x: Int, xx: Int, xxx: Int, xxxx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2).toObservable
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    val thread1 = sendIndexFromThread(s1, internalSize)
val thread2 = sendIndexFromThread(s2, internalSize)
val thread3 = sendIndexFromThread(s3, internalSize)
val thread4 = sendIndexFromThread(s4, internalSize)
val thread5 = sendIndexFromThread(s5, internalSize)
val thread6 = sendIndexFromThread(s6, internalSize)
val thread7 = sendIndexFromThread(s7, internalSize)
val thread8 = sendIndexFromThread(s8, internalSize)

    thread1.start()
thread2.start()
thread3.start()
thread4.start()
thread5.start()
thread6.start()
thread7.start()
thread8.start()

    latch.await
gotAll.await
    thread1.join()
thread2.join()
thread3.join()
thread4.join()
thread5.join()
thread6.join()
thread7.join()
thread8.join()

  
    
    j = j + 1
  }
        }

        if (independentObservablesNumber == 16) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]
val s5 = Subject[Int]
val s6 = Subject[Int]
val s7 = Subject[Int]
val s8 = Subject[Int]
val s9 = Subject[Int]
val s10 = Subject[Int]
val s11 = Subject[Int]
val s12 = Subject[Int]
val s13 = Subject[Int]
val s14 = Subject[Int]
val s15 = Subject[Int]
val s16 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse)
val o2 = s2.observeOn(schedulerToUse)
val o3 = s3.observeOn(schedulerToUse)
val o4 = s4.observeOn(schedulerToUse)
val o5 = s5.observeOn(schedulerToUse)
val o6 = s6.observeOn(schedulerToUse)
val o7 = s7.observeOn(schedulerToUse)
val o8 = s8.observeOn(schedulerToUse)
val o9 = s9.observeOn(schedulerToUse)
val o10 = s10.observeOn(schedulerToUse)
val o11 = s11.observeOn(schedulerToUse)
val o12 = s12.observeOn(schedulerToUse)
val o13 = s13.observeOn(schedulerToUse)
val o14 = s14.observeOn(schedulerToUse)
val o15 = s15.observeOn(schedulerToUse)
val o16 = s16.observeOn(schedulerToUse)

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val p1 = RxJoinObservable.from(o1).and(o2).and(o3).and(o4).and(o5).and(o6).and(o7).and(o8).then((x: Int, xx: Int, xxx: Int, xxxx: Int, xxxxx: Int, xxxxxx: Int, xxxxxxx: Int, xxxxxxxx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val p2 = RxJoinObservable.from(o9).and(o10).and(o11).and(o12).and(o13).and(o14).and(o15).and(o16).then((x: Int, xx: Int, xxx: Int, xxxx: Int, xxxxx: Int, xxxxxx: Int, xxxxxxx: Int, xxxxxxxx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2).toObservable
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    val thread1 = sendIndexFromThread(s1, internalSize)
val thread2 = sendIndexFromThread(s2, internalSize)
val thread3 = sendIndexFromThread(s3, internalSize)
val thread4 = sendIndexFromThread(s4, internalSize)
val thread5 = sendIndexFromThread(s5, internalSize)
val thread6 = sendIndexFromThread(s6, internalSize)
val thread7 = sendIndexFromThread(s7, internalSize)
val thread8 = sendIndexFromThread(s8, internalSize)
val thread9 = sendIndexFromThread(s9, internalSize)
val thread10 = sendIndexFromThread(s10, internalSize)
val thread11 = sendIndexFromThread(s11, internalSize)
val thread12 = sendIndexFromThread(s12, internalSize)
val thread13 = sendIndexFromThread(s13, internalSize)
val thread14 = sendIndexFromThread(s14, internalSize)
val thread15 = sendIndexFromThread(s15, internalSize)
val thread16 = sendIndexFromThread(s16, internalSize)

    thread1.start()
thread2.start()
thread3.start()
thread4.start()
thread5.start()
thread6.start()
thread7.start()
thread8.start()
thread9.start()
thread10.start()
thread11.start()
thread12.start()
thread13.start()
thread14.start()
thread15.start()
thread16.start()

    latch.await
gotAll.await
    thread1.join()
thread2.join()
thread3.join()
thread4.join()
thread5.join()
thread6.join()
thread7.join()
thread8.join()
thread9.join()
thread10.join()
thread11.join()
thread12.join()
thread13.join()
thread14.join()
thread15.join()
thread16.join()

  
    
    j = j + 1
  }
        }
      }
      //////////////////////////////////////////////////////////////////////////////////////
  }

  
  performance of "fullyIndependentTwoCaseBase" config (
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 1000,
    exec.independentSamples -> 1,
    exec.jvmflags -> "-Xms64m -Xmx512m") in 
  {

      //////////////////////////////////////////////////////////////////////////////////////
      using(independentObservables) curve ("Us") in { independentObservablesNumber =>

        if (independentObservablesNumber == 2) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse).p
val o2 = s2.observeOn(schedulerToUse).p

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val obs = join {
      case o1(x) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
case o2(x) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
    }.serialize
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    
    
    j = j + 1
  }
        }
        if (independentObservablesNumber == 4) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse).p
val o2 = s2.observeOn(schedulerToUse).p
val o3 = s3.observeOn(schedulerToUse).p
val o4 = s4.observeOn(schedulerToUse).p

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val obs = join {
      case o1(x) && o2(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
case o3(x) && o4(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
    }.serialize
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    
    
    j = j + 1
  }
        }
        if (independentObservablesNumber == 8) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]
val s5 = Subject[Int]
val s6 = Subject[Int]
val s7 = Subject[Int]
val s8 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse).p
val o2 = s2.observeOn(schedulerToUse).p
val o3 = s3.observeOn(schedulerToUse).p
val o4 = s4.observeOn(schedulerToUse).p
val o5 = s5.observeOn(schedulerToUse).p
val o6 = s6.observeOn(schedulerToUse).p
val o7 = s7.observeOn(schedulerToUse).p
val o8 = s8.observeOn(schedulerToUse).p

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val obs = join {
      case o1(x) && o2(xx) && o3(xxx) && o4(xxxx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
case o5(x) && o6(xx) && o7(xxx) && o8(xxxx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
    }.serialize
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    
    
    j = j + 1
  }
        }
        if (independentObservablesNumber == 16) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]
val s5 = Subject[Int]
val s6 = Subject[Int]
val s7 = Subject[Int]
val s8 = Subject[Int]
val s9 = Subject[Int]
val s10 = Subject[Int]
val s11 = Subject[Int]
val s12 = Subject[Int]
val s13 = Subject[Int]
val s14 = Subject[Int]
val s15 = Subject[Int]
val s16 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse).p
val o2 = s2.observeOn(schedulerToUse).p
val o3 = s3.observeOn(schedulerToUse).p
val o4 = s4.observeOn(schedulerToUse).p
val o5 = s5.observeOn(schedulerToUse).p
val o6 = s6.observeOn(schedulerToUse).p
val o7 = s7.observeOn(schedulerToUse).p
val o8 = s8.observeOn(schedulerToUse).p
val o9 = s9.observeOn(schedulerToUse).p
val o10 = s10.observeOn(schedulerToUse).p
val o11 = s11.observeOn(schedulerToUse).p
val o12 = s12.observeOn(schedulerToUse).p
val o13 = s13.observeOn(schedulerToUse).p
val o14 = s14.observeOn(schedulerToUse).p
val o15 = s15.observeOn(schedulerToUse).p
val o16 = s16.observeOn(schedulerToUse).p

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val obs = join {
      case o1(x) && o2(xx) && o3(xxx) && o4(xxxx) && o5(xxxxx) && o6(xxxxxx) && o7(xxxxxxx) && o8(xxxxxxxx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
case o9(x) && o10(xx) && o11(xxx) && o12(xxxx) && o13(xxxxx) && o14(xxxxxx) && o15(xxxxxxx) && o16(xxxxxxxx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
    }.serialize
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    
    
    j = j + 1
  }
        }
        if (independentObservablesNumber == 32) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]
val s5 = Subject[Int]
val s6 = Subject[Int]
val s7 = Subject[Int]
val s8 = Subject[Int]
val s9 = Subject[Int]
val s10 = Subject[Int]
val s11 = Subject[Int]
val s12 = Subject[Int]
val s13 = Subject[Int]
val s14 = Subject[Int]
val s15 = Subject[Int]
val s16 = Subject[Int]
val s17 = Subject[Int]
val s18 = Subject[Int]
val s19 = Subject[Int]
val s20 = Subject[Int]
val s21 = Subject[Int]
val s22 = Subject[Int]
val s23 = Subject[Int]
val s24 = Subject[Int]
val s25 = Subject[Int]
val s26 = Subject[Int]
val s27 = Subject[Int]
val s28 = Subject[Int]
val s29 = Subject[Int]
val s30 = Subject[Int]
val s31 = Subject[Int]
val s32 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse).p
val o2 = s2.observeOn(schedulerToUse).p
val o3 = s3.observeOn(schedulerToUse).p
val o4 = s4.observeOn(schedulerToUse).p
val o5 = s5.observeOn(schedulerToUse).p
val o6 = s6.observeOn(schedulerToUse).p
val o7 = s7.observeOn(schedulerToUse).p
val o8 = s8.observeOn(schedulerToUse).p
val o9 = s9.observeOn(schedulerToUse).p
val o10 = s10.observeOn(schedulerToUse).p
val o11 = s11.observeOn(schedulerToUse).p
val o12 = s12.observeOn(schedulerToUse).p
val o13 = s13.observeOn(schedulerToUse).p
val o14 = s14.observeOn(schedulerToUse).p
val o15 = s15.observeOn(schedulerToUse).p
val o16 = s16.observeOn(schedulerToUse).p
val o17 = s17.observeOn(schedulerToUse).p
val o18 = s18.observeOn(schedulerToUse).p
val o19 = s19.observeOn(schedulerToUse).p
val o20 = s20.observeOn(schedulerToUse).p
val o21 = s21.observeOn(schedulerToUse).p
val o22 = s22.observeOn(schedulerToUse).p
val o23 = s23.observeOn(schedulerToUse).p
val o24 = s24.observeOn(schedulerToUse).p
val o25 = s25.observeOn(schedulerToUse).p
val o26 = s26.observeOn(schedulerToUse).p
val o27 = s27.observeOn(schedulerToUse).p
val o28 = s28.observeOn(schedulerToUse).p
val o29 = s29.observeOn(schedulerToUse).p
val o30 = s30.observeOn(schedulerToUse).p
val o31 = s31.observeOn(schedulerToUse).p
val o32 = s32.observeOn(schedulerToUse).p

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val obs = join {
      case o1(x) && o2(xx) && o3(xxx) && o4(xxxx) && o5(xxxxx) && o6(xxxxxx) && o7(xxxxxxx) && o8(xxxxxxxx) && o9(xxxxxxxxx) && o10(xxxxxxxxxx) && o11(xxxxxxxxxxx) && o12(xxxxxxxxxxxx) && o13(xxxxxxxxxxxxx) && o14(xxxxxxxxxxxxxx) && o15(xxxxxxxxxxxxxxx) && o16(xxxxxxxxxxxxxxxx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
case o17(x) && o18(xx) && o19(xxx) && o20(xxxx) && o21(xxxxx) && o22(xxxxxx) && o23(xxxxxxx) && o24(xxxxxxxx) && o25(xxxxxxxxx) && o26(xxxxxxxxxx) && o27(xxxxxxxxxxx) && o28(xxxxxxxxxxxx) && o29(xxxxxxxxxxxxx) && o30(xxxxxxxxxxxxxx) && o31(xxxxxxxxxxxxxxx) && o32(xxxxxxxxxxxxxxxx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(()) }
    }.serialize
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    
    
    j = j + 1
  }
        }
      }
      //////////////////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////////////////
      using(independentObservables) curve ("ReactiveX") in { independentObservablesNumber =>
        import rx.lang.scala.ImplicitFunctionConversions._
        import rx.lang.scala.JavaConversions._

        if (independentObservablesNumber == 2) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse)
val o2 = s2.observeOn(schedulerToUse)

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val p1 = RxJoinObservable.from(o1).then((x: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val p2 = RxJoinObservable.from(o2).then((x: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2).toObservable
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    
    
    j = j + 1
  }
        }

        if (independentObservablesNumber == 4) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse)
val o2 = s2.observeOn(schedulerToUse)
val o3 = s3.observeOn(schedulerToUse)
val o4 = s4.observeOn(schedulerToUse)

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val p1 = RxJoinObservable.from(o1).and(o2).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val p2 = RxJoinObservable.from(o3).and(o4).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2).toObservable
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    
    
    j = j + 1
  }
        }

        if (independentObservablesNumber == 8) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]
val s5 = Subject[Int]
val s6 = Subject[Int]
val s7 = Subject[Int]
val s8 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse)
val o2 = s2.observeOn(schedulerToUse)
val o3 = s3.observeOn(schedulerToUse)
val o4 = s4.observeOn(schedulerToUse)
val o5 = s5.observeOn(schedulerToUse)
val o6 = s6.observeOn(schedulerToUse)
val o7 = s7.observeOn(schedulerToUse)
val o8 = s8.observeOn(schedulerToUse)

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val p1 = RxJoinObservable.from(o1).and(o2).and(o3).and(o4).then((x: Int, xx: Int, xxx: Int, xxxx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val p2 = RxJoinObservable.from(o5).and(o6).and(o7).and(o8).then((x: Int, xx: Int, xxx: Int, xxxx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2).toObservable
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    
    
    j = j + 1
  }
        }

        if (independentObservablesNumber == 16) {
          var j = 0
  while (j < iterations) {
    
    val s1 = Subject[Int]
val s2 = Subject[Int]
val s3 = Subject[Int]
val s4 = Subject[Int]
val s5 = Subject[Int]
val s6 = Subject[Int]
val s7 = Subject[Int]
val s8 = Subject[Int]
val s9 = Subject[Int]
val s10 = Subject[Int]
val s11 = Subject[Int]
val s12 = Subject[Int]
val s13 = Subject[Int]
val s14 = Subject[Int]
val s15 = Subject[Int]
val s16 = Subject[Int]

    val o1 = s1.observeOn(schedulerToUse)
val o2 = s2.observeOn(schedulerToUse)
val o3 = s3.observeOn(schedulerToUse)
val o4 = s4.observeOn(schedulerToUse)
val o5 = s5.observeOn(schedulerToUse)
val o6 = s6.observeOn(schedulerToUse)
val o7 = s7.observeOn(schedulerToUse)
val o8 = s8.observeOn(schedulerToUse)
val o9 = s9.observeOn(schedulerToUse)
val o10 = s10.observeOn(schedulerToUse)
val o11 = s11.observeOn(schedulerToUse)
val o12 = s12.observeOn(schedulerToUse)
val o13 = s13.observeOn(schedulerToUse)
val o14 = s14.observeOn(schedulerToUse)
val o15 = s15.observeOn(schedulerToUse)
val o16 = s16.observeOn(schedulerToUse)

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val p1 = RxJoinObservable.from(o1).and(o2).and(o3).and(o4).and(o5).and(o6).and(o7).and(o8).then((x: Int, xx: Int, xxx: Int, xxxx: Int, xxxxx: Int, xxxxxx: Int, xxxxxxx: Int, xxxxxxxx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val p2 = RxJoinObservable.from(o9).and(o10).and(o11).and(o12).and(o13).and(o14).and(o15).and(o16).then((x: Int, xx: Int, xxx: Int, xxxx: Int, xxxxx: Int, xxxxxx: Int, xxxxxxx: Int, xxxxxxxx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2).toObservable
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    
    
    j = j + 1
  }
        }
      }
      //////////////////////////////////////////////////////////////////////////////////////
  }


}
