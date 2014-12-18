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

  
   performance of "NCaseTwoIndependent" config (
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 1000,
    exec.independentSamples -> 1) in {
      //////////////////////////////////////////////////////////////////////////////////////
      using(independentObservables) curve ("Us") in { independentObservablesNumber =>

        if (independentObservablesNumber == 2) {
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
        if (independentObservablesNumber == 4) {
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
      case o1(x) && o2(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown }
    Next(()) }
case o3(x) && o4(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown }
    Next(()) }
case o5(x) && o6(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown }
    Next(()) }
case o7(x) && o8(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown }
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
      case o1(x) && o2(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o3(x) && o4(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o5(x) && o6(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o7(x) && o8(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o9(x) && o10(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o11(x) && o12(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o13(x) && o14(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o15(x) && o16(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
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
      case o1(x) && o2(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o3(x) && o4(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o5(x) && o6(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o7(x) && o8(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o9(x) && o10(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o11(x) && o12(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o13(x) && o14(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o15(x) && o16(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o17(x) && o18(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o19(x) && o20(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o21(x) && o22(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o23(x) && o24(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o25(x) && o26(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o27(x) && o28(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o29(x) && o30(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o31(x) && o32(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
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
val s33 = Subject[Int]
val s34 = Subject[Int]
val s35 = Subject[Int]
val s36 = Subject[Int]
val s37 = Subject[Int]
val s38 = Subject[Int]
val s39 = Subject[Int]
val s40 = Subject[Int]
val s41 = Subject[Int]
val s42 = Subject[Int]
val s43 = Subject[Int]
val s44 = Subject[Int]
val s45 = Subject[Int]
val s46 = Subject[Int]
val s47 = Subject[Int]
val s48 = Subject[Int]
val s49 = Subject[Int]
val s50 = Subject[Int]
val s51 = Subject[Int]
val s52 = Subject[Int]
val s53 = Subject[Int]
val s54 = Subject[Int]
val s55 = Subject[Int]
val s56 = Subject[Int]
val s57 = Subject[Int]
val s58 = Subject[Int]
val s59 = Subject[Int]
val s60 = Subject[Int]
val s61 = Subject[Int]
val s62 = Subject[Int]
val s63 = Subject[Int]
val s64 = Subject[Int]

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
val o33 = s33.observeOn(schedulerToUse).p
val o34 = s34.observeOn(schedulerToUse).p
val o35 = s35.observeOn(schedulerToUse).p
val o36 = s36.observeOn(schedulerToUse).p
val o37 = s37.observeOn(schedulerToUse).p
val o38 = s38.observeOn(schedulerToUse).p
val o39 = s39.observeOn(schedulerToUse).p
val o40 = s40.observeOn(schedulerToUse).p
val o41 = s41.observeOn(schedulerToUse).p
val o42 = s42.observeOn(schedulerToUse).p
val o43 = s43.observeOn(schedulerToUse).p
val o44 = s44.observeOn(schedulerToUse).p
val o45 = s45.observeOn(schedulerToUse).p
val o46 = s46.observeOn(schedulerToUse).p
val o47 = s47.observeOn(schedulerToUse).p
val o48 = s48.observeOn(schedulerToUse).p
val o49 = s49.observeOn(schedulerToUse).p
val o50 = s50.observeOn(schedulerToUse).p
val o51 = s51.observeOn(schedulerToUse).p
val o52 = s52.observeOn(schedulerToUse).p
val o53 = s53.observeOn(schedulerToUse).p
val o54 = s54.observeOn(schedulerToUse).p
val o55 = s55.observeOn(schedulerToUse).p
val o56 = s56.observeOn(schedulerToUse).p
val o57 = s57.observeOn(schedulerToUse).p
val o58 = s58.observeOn(schedulerToUse).p
val o59 = s59.observeOn(schedulerToUse).p
val o60 = s60.observeOn(schedulerToUse).p
val o61 = s61.observeOn(schedulerToUse).p
val o62 = s62.observeOn(schedulerToUse).p
val o63 = s63.observeOn(schedulerToUse).p
val o64 = s64.observeOn(schedulerToUse).p

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val obs = join {
      case o1(x) && o2(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o3(x) && o4(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o5(x) && o6(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o7(x) && o8(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o9(x) && o10(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o11(x) && o12(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o13(x) && o14(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o15(x) && o16(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o17(x) && o18(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o19(x) && o20(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o21(x) && o22(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o23(x) && o24(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o25(x) && o26(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o27(x) && o28(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o29(x) && o30(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o31(x) && o32(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o33(x) && o34(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o35(x) && o36(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o37(x) && o38(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o39(x) && o40(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o41(x) && o42(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o43(x) && o44(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o45(x) && o46(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o47(x) && o48(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o49(x) && o50(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o51(x) && o52(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o53(x) && o54(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o55(x) && o56(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o57(x) && o58(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o59(x) && o60(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o61(x) && o62(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o63(x) && o64(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
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
val thread33 = sendIndexFromThread(s33, internalSize)
val thread34 = sendIndexFromThread(s34, internalSize)
val thread35 = sendIndexFromThread(s35, internalSize)
val thread36 = sendIndexFromThread(s36, internalSize)
val thread37 = sendIndexFromThread(s37, internalSize)
val thread38 = sendIndexFromThread(s38, internalSize)
val thread39 = sendIndexFromThread(s39, internalSize)
val thread40 = sendIndexFromThread(s40, internalSize)
val thread41 = sendIndexFromThread(s41, internalSize)
val thread42 = sendIndexFromThread(s42, internalSize)
val thread43 = sendIndexFromThread(s43, internalSize)
val thread44 = sendIndexFromThread(s44, internalSize)
val thread45 = sendIndexFromThread(s45, internalSize)
val thread46 = sendIndexFromThread(s46, internalSize)
val thread47 = sendIndexFromThread(s47, internalSize)
val thread48 = sendIndexFromThread(s48, internalSize)
val thread49 = sendIndexFromThread(s49, internalSize)
val thread50 = sendIndexFromThread(s50, internalSize)
val thread51 = sendIndexFromThread(s51, internalSize)
val thread52 = sendIndexFromThread(s52, internalSize)
val thread53 = sendIndexFromThread(s53, internalSize)
val thread54 = sendIndexFromThread(s54, internalSize)
val thread55 = sendIndexFromThread(s55, internalSize)
val thread56 = sendIndexFromThread(s56, internalSize)
val thread57 = sendIndexFromThread(s57, internalSize)
val thread58 = sendIndexFromThread(s58, internalSize)
val thread59 = sendIndexFromThread(s59, internalSize)
val thread60 = sendIndexFromThread(s60, internalSize)
val thread61 = sendIndexFromThread(s61, internalSize)
val thread62 = sendIndexFromThread(s62, internalSize)
val thread63 = sendIndexFromThread(s63, internalSize)
val thread64 = sendIndexFromThread(s64, internalSize)

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
thread33.start()
thread34.start()
thread35.start()
thread36.start()
thread37.start()
thread38.start()
thread39.start()
thread40.start()
thread41.start()
thread42.start()
thread43.start()
thread44.start()
thread45.start()
thread46.start()
thread47.start()
thread48.start()
thread49.start()
thread50.start()
thread51.start()
thread52.start()
thread53.start()
thread54.start()
thread55.start()
thread56.start()
thread57.start()
thread58.start()
thread59.start()
thread60.start()
thread61.start()
thread62.start()
thread63.start()
thread64.start()

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
thread33.join()
thread34.join()
thread35.join()
thread36.join()
thread37.join()
thread38.join()
thread39.join()
thread40.join()
thread41.join()
thread42.join()
thread43.join()
thread44.join()
thread45.join()
thread46.join()
thread47.join()
thread48.join()
thread49.join()
thread50.join()
thread51.join()
thread52.join()
thread53.join()
thread54.join()
thread55.join()
thread56.join()
thread57.join()
thread58.join()
thread59.join()
thread60.join()
thread61.join()
thread62.join()
thread63.join()
thread64.join()

  
    
    j = j + 1
  }
        }

      }
      //////////////////////////////////////////////////////////////////////////////////////
      using(independentObservables) curve ("ReactiveX") in { independentObservablesNumber =>
        import rx.lang.scala.ImplicitFunctionConversions._
        import rx.lang.scala.JavaConversions._

        if (independentObservablesNumber == 2) {
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

        if (independentObservablesNumber == 4) {
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
    val p1 = RxJoinObservable.from(o1).and(o2).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown } })
val p2 = RxJoinObservable.from(o3).and(o4).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown } })
val p3 = RxJoinObservable.from(o5).and(o6).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown } })
val p4 = RxJoinObservable.from(o7).and(o8).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2,p3,p4).toObservable
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
    val p1 = RxJoinObservable.from(o1).and(o2).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p2 = RxJoinObservable.from(o3).and(o4).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p3 = RxJoinObservable.from(o5).and(o6).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p4 = RxJoinObservable.from(o7).and(o8).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p5 = RxJoinObservable.from(o9).and(o10).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p6 = RxJoinObservable.from(o11).and(o12).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p7 = RxJoinObservable.from(o13).and(o14).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p8 = RxJoinObservable.from(o15).and(o16).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2,p3,p4,p5,p6,p7,p8).toObservable
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
val o17 = s17.observeOn(schedulerToUse)
val o18 = s18.observeOn(schedulerToUse)
val o19 = s19.observeOn(schedulerToUse)
val o20 = s20.observeOn(schedulerToUse)
val o21 = s21.observeOn(schedulerToUse)
val o22 = s22.observeOn(schedulerToUse)
val o23 = s23.observeOn(schedulerToUse)
val o24 = s24.observeOn(schedulerToUse)
val o25 = s25.observeOn(schedulerToUse)
val o26 = s26.observeOn(schedulerToUse)
val o27 = s27.observeOn(schedulerToUse)
val o28 = s28.observeOn(schedulerToUse)
val o29 = s29.observeOn(schedulerToUse)
val o30 = s30.observeOn(schedulerToUse)
val o31 = s31.observeOn(schedulerToUse)
val o32 = s32.observeOn(schedulerToUse)

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val p1 = RxJoinObservable.from(o1).and(o2).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p2 = RxJoinObservable.from(o3).and(o4).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p3 = RxJoinObservable.from(o5).and(o6).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p4 = RxJoinObservable.from(o7).and(o8).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p5 = RxJoinObservable.from(o9).and(o10).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p6 = RxJoinObservable.from(o11).and(o12).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p7 = RxJoinObservable.from(o13).and(o14).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p8 = RxJoinObservable.from(o15).and(o16).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p9 = RxJoinObservable.from(o17).and(o18).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p10 = RxJoinObservable.from(o19).and(o20).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p11 = RxJoinObservable.from(o21).and(o22).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p12 = RxJoinObservable.from(o23).and(o24).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p13 = RxJoinObservable.from(o25).and(o26).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p14 = RxJoinObservable.from(o27).and(o28).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p15 = RxJoinObservable.from(o29).and(o30).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p16 = RxJoinObservable.from(o31).and(o32).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14,p15,p16).toObservable
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
val s33 = Subject[Int]
val s34 = Subject[Int]
val s35 = Subject[Int]
val s36 = Subject[Int]
val s37 = Subject[Int]
val s38 = Subject[Int]
val s39 = Subject[Int]
val s40 = Subject[Int]
val s41 = Subject[Int]
val s42 = Subject[Int]
val s43 = Subject[Int]
val s44 = Subject[Int]
val s45 = Subject[Int]
val s46 = Subject[Int]
val s47 = Subject[Int]
val s48 = Subject[Int]
val s49 = Subject[Int]
val s50 = Subject[Int]
val s51 = Subject[Int]
val s52 = Subject[Int]
val s53 = Subject[Int]
val s54 = Subject[Int]
val s55 = Subject[Int]
val s56 = Subject[Int]
val s57 = Subject[Int]
val s58 = Subject[Int]
val s59 = Subject[Int]
val s60 = Subject[Int]
val s61 = Subject[Int]
val s62 = Subject[Int]
val s63 = Subject[Int]
val s64 = Subject[Int]

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
val o17 = s17.observeOn(schedulerToUse)
val o18 = s18.observeOn(schedulerToUse)
val o19 = s19.observeOn(schedulerToUse)
val o20 = s20.observeOn(schedulerToUse)
val o21 = s21.observeOn(schedulerToUse)
val o22 = s22.observeOn(schedulerToUse)
val o23 = s23.observeOn(schedulerToUse)
val o24 = s24.observeOn(schedulerToUse)
val o25 = s25.observeOn(schedulerToUse)
val o26 = s26.observeOn(schedulerToUse)
val o27 = s27.observeOn(schedulerToUse)
val o28 = s28.observeOn(schedulerToUse)
val o29 = s29.observeOn(schedulerToUse)
val o30 = s30.observeOn(schedulerToUse)
val o31 = s31.observeOn(schedulerToUse)
val o32 = s32.observeOn(schedulerToUse)
val o33 = s33.observeOn(schedulerToUse)
val o34 = s34.observeOn(schedulerToUse)
val o35 = s35.observeOn(schedulerToUse)
val o36 = s36.observeOn(schedulerToUse)
val o37 = s37.observeOn(schedulerToUse)
val o38 = s38.observeOn(schedulerToUse)
val o39 = s39.observeOn(schedulerToUse)
val o40 = s40.observeOn(schedulerToUse)
val o41 = s41.observeOn(schedulerToUse)
val o42 = s42.observeOn(schedulerToUse)
val o43 = s43.observeOn(schedulerToUse)
val o44 = s44.observeOn(schedulerToUse)
val o45 = s45.observeOn(schedulerToUse)
val o46 = s46.observeOn(schedulerToUse)
val o47 = s47.observeOn(schedulerToUse)
val o48 = s48.observeOn(schedulerToUse)
val o49 = s49.observeOn(schedulerToUse)
val o50 = s50.observeOn(schedulerToUse)
val o51 = s51.observeOn(schedulerToUse)
val o52 = s52.observeOn(schedulerToUse)
val o53 = s53.observeOn(schedulerToUse)
val o54 = s54.observeOn(schedulerToUse)
val o55 = s55.observeOn(schedulerToUse)
val o56 = s56.observeOn(schedulerToUse)
val o57 = s57.observeOn(schedulerToUse)
val o58 = s58.observeOn(schedulerToUse)
val o59 = s59.observeOn(schedulerToUse)
val o60 = s60.observeOn(schedulerToUse)
val o61 = s61.observeOn(schedulerToUse)
val o62 = s62.observeOn(schedulerToUse)
val o63 = s63.observeOn(schedulerToUse)
val o64 = s64.observeOn(schedulerToUse)

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val p1 = RxJoinObservable.from(o1).and(o2).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p2 = RxJoinObservable.from(o3).and(o4).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p3 = RxJoinObservable.from(o5).and(o6).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p4 = RxJoinObservable.from(o7).and(o8).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p5 = RxJoinObservable.from(o9).and(o10).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p6 = RxJoinObservable.from(o11).and(o12).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p7 = RxJoinObservable.from(o13).and(o14).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p8 = RxJoinObservable.from(o15).and(o16).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p9 = RxJoinObservable.from(o17).and(o18).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p10 = RxJoinObservable.from(o19).and(o20).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p11 = RxJoinObservable.from(o21).and(o22).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p12 = RxJoinObservable.from(o23).and(o24).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p13 = RxJoinObservable.from(o25).and(o26).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p14 = RxJoinObservable.from(o27).and(o28).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p15 = RxJoinObservable.from(o29).and(o30).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p16 = RxJoinObservable.from(o31).and(o32).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p17 = RxJoinObservable.from(o33).and(o34).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p18 = RxJoinObservable.from(o35).and(o36).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p19 = RxJoinObservable.from(o37).and(o38).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p20 = RxJoinObservable.from(o39).and(o40).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p21 = RxJoinObservable.from(o41).and(o42).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p22 = RxJoinObservable.from(o43).and(o44).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p23 = RxJoinObservable.from(o45).and(o46).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p24 = RxJoinObservable.from(o47).and(o48).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p25 = RxJoinObservable.from(o49).and(o50).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p26 = RxJoinObservable.from(o51).and(o52).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p27 = RxJoinObservable.from(o53).and(o54).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p28 = RxJoinObservable.from(o55).and(o56).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p29 = RxJoinObservable.from(o57).and(o58).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p30 = RxJoinObservable.from(o59).and(o60).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p31 = RxJoinObservable.from(o61).and(o62).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p32 = RxJoinObservable.from(o63).and(o64).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14,p15,p16,p17,p18,p19,p20,p21,p22,p23,p24,p25,p26,p27,p28,p29,p30,p31,p32).toObservable
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
val thread33 = sendIndexFromThread(s33, internalSize)
val thread34 = sendIndexFromThread(s34, internalSize)
val thread35 = sendIndexFromThread(s35, internalSize)
val thread36 = sendIndexFromThread(s36, internalSize)
val thread37 = sendIndexFromThread(s37, internalSize)
val thread38 = sendIndexFromThread(s38, internalSize)
val thread39 = sendIndexFromThread(s39, internalSize)
val thread40 = sendIndexFromThread(s40, internalSize)
val thread41 = sendIndexFromThread(s41, internalSize)
val thread42 = sendIndexFromThread(s42, internalSize)
val thread43 = sendIndexFromThread(s43, internalSize)
val thread44 = sendIndexFromThread(s44, internalSize)
val thread45 = sendIndexFromThread(s45, internalSize)
val thread46 = sendIndexFromThread(s46, internalSize)
val thread47 = sendIndexFromThread(s47, internalSize)
val thread48 = sendIndexFromThread(s48, internalSize)
val thread49 = sendIndexFromThread(s49, internalSize)
val thread50 = sendIndexFromThread(s50, internalSize)
val thread51 = sendIndexFromThread(s51, internalSize)
val thread52 = sendIndexFromThread(s52, internalSize)
val thread53 = sendIndexFromThread(s53, internalSize)
val thread54 = sendIndexFromThread(s54, internalSize)
val thread55 = sendIndexFromThread(s55, internalSize)
val thread56 = sendIndexFromThread(s56, internalSize)
val thread57 = sendIndexFromThread(s57, internalSize)
val thread58 = sendIndexFromThread(s58, internalSize)
val thread59 = sendIndexFromThread(s59, internalSize)
val thread60 = sendIndexFromThread(s60, internalSize)
val thread61 = sendIndexFromThread(s61, internalSize)
val thread62 = sendIndexFromThread(s62, internalSize)
val thread63 = sendIndexFromThread(s63, internalSize)
val thread64 = sendIndexFromThread(s64, internalSize)

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
thread33.start()
thread34.start()
thread35.start()
thread36.start()
thread37.start()
thread38.start()
thread39.start()
thread40.start()
thread41.start()
thread42.start()
thread43.start()
thread44.start()
thread45.start()
thread46.start()
thread47.start()
thread48.start()
thread49.start()
thread50.start()
thread51.start()
thread52.start()
thread53.start()
thread54.start()
thread55.start()
thread56.start()
thread57.start()
thread58.start()
thread59.start()
thread60.start()
thread61.start()
thread62.start()
thread63.start()
thread64.start()

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
thread33.join()
thread34.join()
thread35.join()
thread36.join()
thread37.join()
thread38.join()
thread39.join()
thread40.join()
thread41.join()
thread42.join()
thread43.join()
thread44.join()
thread45.join()
thread46.join()
thread47.join()
thread48.join()
thread49.join()
thread50.join()
thread51.join()
thread52.join()
thread53.join()
thread54.join()
thread55.join()
thread56.join()
thread57.join()
thread58.join()
thread59.join()
thread60.join()
thread61.join()
thread62.join()
thread63.join()
thread64.join()

  
    
    j = j + 1
  }
        }
      }
      //////////////////////////////////////////////////////////////////////////////////////
  }

  
   performance of "NCaseTwoIndependent" config (
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 1000,
    exec.independentSamples -> 1) in {
      //////////////////////////////////////////////////////////////////////////////////////
      using(independentObservables) curve ("Us") in { independentObservablesNumber =>

        if (independentObservablesNumber == 2) {
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
        if (independentObservablesNumber == 4) {
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
      case o1(x) && o2(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown }
    Next(()) }
case o3(x) && o4(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown }
    Next(()) }
case o5(x) && o6(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown }
    Next(()) }
case o7(x) && o8(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown }
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
      case o1(x) && o2(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o3(x) && o4(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o5(x) && o6(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o7(x) && o8(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o9(x) && o10(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o11(x) && o12(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o13(x) && o14(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
    Next(()) }
case o15(x) && o16(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown }
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
      case o1(x) && o2(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o3(x) && o4(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o5(x) && o6(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o7(x) && o8(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o9(x) && o10(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o11(x) && o12(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o13(x) && o14(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o15(x) && o16(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o17(x) && o18(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o19(x) && o20(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o21(x) && o22(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o23(x) && o24(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o25(x) && o26(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o27(x) && o28(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o29(x) && o30(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
    Next(()) }
case o31(x) && o32(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown }
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
val s33 = Subject[Int]
val s34 = Subject[Int]
val s35 = Subject[Int]
val s36 = Subject[Int]
val s37 = Subject[Int]
val s38 = Subject[Int]
val s39 = Subject[Int]
val s40 = Subject[Int]
val s41 = Subject[Int]
val s42 = Subject[Int]
val s43 = Subject[Int]
val s44 = Subject[Int]
val s45 = Subject[Int]
val s46 = Subject[Int]
val s47 = Subject[Int]
val s48 = Subject[Int]
val s49 = Subject[Int]
val s50 = Subject[Int]
val s51 = Subject[Int]
val s52 = Subject[Int]
val s53 = Subject[Int]
val s54 = Subject[Int]
val s55 = Subject[Int]
val s56 = Subject[Int]
val s57 = Subject[Int]
val s58 = Subject[Int]
val s59 = Subject[Int]
val s60 = Subject[Int]
val s61 = Subject[Int]
val s62 = Subject[Int]
val s63 = Subject[Int]
val s64 = Subject[Int]

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
val o33 = s33.observeOn(schedulerToUse).p
val o34 = s34.observeOn(schedulerToUse).p
val o35 = s35.observeOn(schedulerToUse).p
val o36 = s36.observeOn(schedulerToUse).p
val o37 = s37.observeOn(schedulerToUse).p
val o38 = s38.observeOn(schedulerToUse).p
val o39 = s39.observeOn(schedulerToUse).p
val o40 = s40.observeOn(schedulerToUse).p
val o41 = s41.observeOn(schedulerToUse).p
val o42 = s42.observeOn(schedulerToUse).p
val o43 = s43.observeOn(schedulerToUse).p
val o44 = s44.observeOn(schedulerToUse).p
val o45 = s45.observeOn(schedulerToUse).p
val o46 = s46.observeOn(schedulerToUse).p
val o47 = s47.observeOn(schedulerToUse).p
val o48 = s48.observeOn(schedulerToUse).p
val o49 = s49.observeOn(schedulerToUse).p
val o50 = s50.observeOn(schedulerToUse).p
val o51 = s51.observeOn(schedulerToUse).p
val o52 = s52.observeOn(schedulerToUse).p
val o53 = s53.observeOn(schedulerToUse).p
val o54 = s54.observeOn(schedulerToUse).p
val o55 = s55.observeOn(schedulerToUse).p
val o56 = s56.observeOn(schedulerToUse).p
val o57 = s57.observeOn(schedulerToUse).p
val o58 = s58.observeOn(schedulerToUse).p
val o59 = s59.observeOn(schedulerToUse).p
val o60 = s60.observeOn(schedulerToUse).p
val o61 = s61.observeOn(schedulerToUse).p
val o62 = s62.observeOn(schedulerToUse).p
val o63 = s63.observeOn(schedulerToUse).p
val o64 = s64.observeOn(schedulerToUse).p

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val obs = join {
      case o1(x) && o2(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o3(x) && o4(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o5(x) && o6(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o7(x) && o8(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o9(x) && o10(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o11(x) && o12(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o13(x) && o14(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o15(x) && o16(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o17(x) && o18(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o19(x) && o20(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o21(x) && o22(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o23(x) && o24(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o25(x) && o26(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o27(x) && o28(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o29(x) && o30(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o31(x) && o32(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o33(x) && o34(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o35(x) && o36(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o37(x) && o38(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o39(x) && o40(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o41(x) && o42(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o43(x) && o44(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o45(x) && o46(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o47(x) && o48(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o49(x) && o50(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o51(x) && o52(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o53(x) && o54(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o55(x) && o56(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o57(x) && o58(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o59(x) && o60(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o61(x) && o62(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
case o63(x) && o64(xx) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown }
    Next(()) }
    }.serialize
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    
    
    j = j + 1
  }
        }

      }
      //////////////////////////////////////////////////////////////////////////////////////
      using(independentObservables) curve ("ReactiveX") in { independentObservablesNumber =>
        import rx.lang.scala.ImplicitFunctionConversions._
        import rx.lang.scala.JavaConversions._

        if (independentObservablesNumber == 2) {
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

        if (independentObservablesNumber == 4) {
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
    val p1 = RxJoinObservable.from(o1).and(o2).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown } })
val p2 = RxJoinObservable.from(o3).and(o4).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown } })
val p3 = RxJoinObservable.from(o5).and(o6).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown } })
val p4 = RxJoinObservable.from(o7).and(o8).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 4)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2,p3,p4).toObservable
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
    val p1 = RxJoinObservable.from(o1).and(o2).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p2 = RxJoinObservable.from(o3).and(o4).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p3 = RxJoinObservable.from(o5).and(o6).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p4 = RxJoinObservable.from(o7).and(o8).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p5 = RxJoinObservable.from(o9).and(o10).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p6 = RxJoinObservable.from(o11).and(o12).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p7 = RxJoinObservable.from(o13).and(o14).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val p8 = RxJoinObservable.from(o15).and(o16).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 8)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2,p3,p4,p5,p6,p7,p8).toObservable
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
val o17 = s17.observeOn(schedulerToUse)
val o18 = s18.observeOn(schedulerToUse)
val o19 = s19.observeOn(schedulerToUse)
val o20 = s20.observeOn(schedulerToUse)
val o21 = s21.observeOn(schedulerToUse)
val o22 = s22.observeOn(schedulerToUse)
val o23 = s23.observeOn(schedulerToUse)
val o24 = s24.observeOn(schedulerToUse)
val o25 = s25.observeOn(schedulerToUse)
val o26 = s26.observeOn(schedulerToUse)
val o27 = s27.observeOn(schedulerToUse)
val o28 = s28.observeOn(schedulerToUse)
val o29 = s29.observeOn(schedulerToUse)
val o30 = s30.observeOn(schedulerToUse)
val o31 = s31.observeOn(schedulerToUse)
val o32 = s32.observeOn(schedulerToUse)

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val p1 = RxJoinObservable.from(o1).and(o2).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p2 = RxJoinObservable.from(o3).and(o4).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p3 = RxJoinObservable.from(o5).and(o6).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p4 = RxJoinObservable.from(o7).and(o8).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p5 = RxJoinObservable.from(o9).and(o10).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p6 = RxJoinObservable.from(o11).and(o12).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p7 = RxJoinObservable.from(o13).and(o14).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p8 = RxJoinObservable.from(o15).and(o16).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p9 = RxJoinObservable.from(o17).and(o18).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p10 = RxJoinObservable.from(o19).and(o20).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p11 = RxJoinObservable.from(o21).and(o22).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p12 = RxJoinObservable.from(o23).and(o24).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p13 = RxJoinObservable.from(o25).and(o26).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p14 = RxJoinObservable.from(o27).and(o28).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p15 = RxJoinObservable.from(o29).and(o30).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val p16 = RxJoinObservable.from(o31).and(o32).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 16)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14,p15,p16).toObservable
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
val s33 = Subject[Int]
val s34 = Subject[Int]
val s35 = Subject[Int]
val s36 = Subject[Int]
val s37 = Subject[Int]
val s38 = Subject[Int]
val s39 = Subject[Int]
val s40 = Subject[Int]
val s41 = Subject[Int]
val s42 = Subject[Int]
val s43 = Subject[Int]
val s44 = Subject[Int]
val s45 = Subject[Int]
val s46 = Subject[Int]
val s47 = Subject[Int]
val s48 = Subject[Int]
val s49 = Subject[Int]
val s50 = Subject[Int]
val s51 = Subject[Int]
val s52 = Subject[Int]
val s53 = Subject[Int]
val s54 = Subject[Int]
val s55 = Subject[Int]
val s56 = Subject[Int]
val s57 = Subject[Int]
val s58 = Subject[Int]
val s59 = Subject[Int]
val s60 = Subject[Int]
val s61 = Subject[Int]
val s62 = Subject[Int]
val s63 = Subject[Int]
val s64 = Subject[Int]

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
val o17 = s17.observeOn(schedulerToUse)
val o18 = s18.observeOn(schedulerToUse)
val o19 = s19.observeOn(schedulerToUse)
val o20 = s20.observeOn(schedulerToUse)
val o21 = s21.observeOn(schedulerToUse)
val o22 = s22.observeOn(schedulerToUse)
val o23 = s23.observeOn(schedulerToUse)
val o24 = s24.observeOn(schedulerToUse)
val o25 = s25.observeOn(schedulerToUse)
val o26 = s26.observeOn(schedulerToUse)
val o27 = s27.observeOn(schedulerToUse)
val o28 = s28.observeOn(schedulerToUse)
val o29 = s29.observeOn(schedulerToUse)
val o30 = s30.observeOn(schedulerToUse)
val o31 = s31.observeOn(schedulerToUse)
val o32 = s32.observeOn(schedulerToUse)
val o33 = s33.observeOn(schedulerToUse)
val o34 = s34.observeOn(schedulerToUse)
val o35 = s35.observeOn(schedulerToUse)
val o36 = s36.observeOn(schedulerToUse)
val o37 = s37.observeOn(schedulerToUse)
val o38 = s38.observeOn(schedulerToUse)
val o39 = s39.observeOn(schedulerToUse)
val o40 = s40.observeOn(schedulerToUse)
val o41 = s41.observeOn(schedulerToUse)
val o42 = s42.observeOn(schedulerToUse)
val o43 = s43.observeOn(schedulerToUse)
val o44 = s44.observeOn(schedulerToUse)
val o45 = s45.observeOn(schedulerToUse)
val o46 = s46.observeOn(schedulerToUse)
val o47 = s47.observeOn(schedulerToUse)
val o48 = s48.observeOn(schedulerToUse)
val o49 = s49.observeOn(schedulerToUse)
val o50 = s50.observeOn(schedulerToUse)
val o51 = s51.observeOn(schedulerToUse)
val o52 = s52.observeOn(schedulerToUse)
val o53 = s53.observeOn(schedulerToUse)
val o54 = s54.observeOn(schedulerToUse)
val o55 = s55.observeOn(schedulerToUse)
val o56 = s56.observeOn(schedulerToUse)
val o57 = s57.observeOn(schedulerToUse)
val o58 = s58.observeOn(schedulerToUse)
val o59 = s59.observeOn(schedulerToUse)
val o60 = s60.observeOn(schedulerToUse)
val o61 = s61.observeOn(schedulerToUse)
val o62 = s62.observeOn(schedulerToUse)
val o63 = s63.observeOn(schedulerToUse)
val o64 = s64.observeOn(schedulerToUse)

    val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)
    val p1 = RxJoinObservable.from(o1).and(o2).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p2 = RxJoinObservable.from(o3).and(o4).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p3 = RxJoinObservable.from(o5).and(o6).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p4 = RxJoinObservable.from(o7).and(o8).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p5 = RxJoinObservable.from(o9).and(o10).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p6 = RxJoinObservable.from(o11).and(o12).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p7 = RxJoinObservable.from(o13).and(o14).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p8 = RxJoinObservable.from(o15).and(o16).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p9 = RxJoinObservable.from(o17).and(o18).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p10 = RxJoinObservable.from(o19).and(o20).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p11 = RxJoinObservable.from(o21).and(o22).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p12 = RxJoinObservable.from(o23).and(o24).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p13 = RxJoinObservable.from(o25).and(o26).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p14 = RxJoinObservable.from(o27).and(o28).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p15 = RxJoinObservable.from(o29).and(o30).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p16 = RxJoinObservable.from(o31).and(o32).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p17 = RxJoinObservable.from(o33).and(o34).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p18 = RxJoinObservable.from(o35).and(o36).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p19 = RxJoinObservable.from(o37).and(o38).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p20 = RxJoinObservable.from(o39).and(o40).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p21 = RxJoinObservable.from(o41).and(o42).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p22 = RxJoinObservable.from(o43).and(o44).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p23 = RxJoinObservable.from(o45).and(o46).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p24 = RxJoinObservable.from(o47).and(o48).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p25 = RxJoinObservable.from(o49).and(o50).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p26 = RxJoinObservable.from(o51).and(o52).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p27 = RxJoinObservable.from(o53).and(o54).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p28 = RxJoinObservable.from(o55).and(o56).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p29 = RxJoinObservable.from(o57).and(o58).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p30 = RxJoinObservable.from(o59).and(o60).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p31 = RxJoinObservable.from(o61).and(o62).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val p32 = RxJoinObservable.from(o63).and(o64).then((x: Int, xx: Int) => { counter.incrementAndGet()
    if (counter.get == (internalSize * 32)) { latch.countDown } })
val obs = RxJoinObservable.when(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p13,p14,p15,p16,p17,p18,p19,p20,p21,p22,p23,p24,p25,p26,p27,p28,p29,p30,p31,p32).toObservable
    obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())
    
    
    j = j + 1
  }
        }
      }
      //////////////////////////////////////////////////////////////////////////////////////
  }


}
