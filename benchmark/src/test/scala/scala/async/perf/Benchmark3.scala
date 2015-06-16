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

object Benchmark3 extends PerformanceTest {

  lazy val executor = SeparateJvmsExecutor(
    new Executor.Warmer.Default,
    Aggregator.average,
    new Measurer.Default)
  lazy val reporter = MyDsvReporter(',')

  lazy val persistor = Persistor.None

  implicit def scalaFunction8ToRxFunc8[A, B, C, D, E, F, G, H, I](fn: (A, B, C, D, E, F, G, H) => I): Func8[A, B, C, D, E, F, G, H, I] =
    new Func8[A, B, C, D, E, F, G, H, I] {
      def call(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H) = fn(a, b, c, d, e, f, g, h)
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

  val NDependentCasesOutRange = Gen.enumeration("Choices")(2, 4, 8, 16)

  performance of "NDependentCases" config (
    // exec.minWarmupRuns -> 1024,
    // exec.maxWarmupRuns -> 2048,
    exec.benchRuns -> 2048,
    exec.independentSamples -> 2) in
    {
      using(NDependentCasesOutRange) curve ("Non-Deterministic Choice") in { nDependent =>
        if (nDependent == 2) {

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

          val o1 = s1.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o2 = s2.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o3 = s3.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o4 = s4.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o5 = s5.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o6 = s6.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o7 = s7.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o8 = s8.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o9 = s9.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o10 = s10.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o11 = s11.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o12 = s12.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o13 = s13.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o14 = s14.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o15 = s15.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o16 = s16.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o17 = s17.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o18 = s18.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o19 = s19.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o20 = s20.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o21 = s21.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o22 = s22.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o23 = s23.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o24 = s24.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o25 = s25.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o26 = s26.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o27 = s27.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o28 = s28.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o29 = s29.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o30 = s30.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o31 = s31.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o32 = s32.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o33 = s33.onBackpressureBuffer.observeOn(schedulerToUse).p

          val latch = new CountDownLatch(524288)
          val obs = join {
            case o1(x) && o2(xx) && o33(xxx) => { Next(1) }
            case o3(x) && o4(xx) && o33(xxx) => { Next(1) }
            case o5(x) && o6(xx) => { Next(1) }
            case o7(x) && o8(xx) => { Next(1) }
            case o9(x) && o10(xx) => { Next(1) }
            case o11(x) && o12(xx) => { Next(1) }
            case o13(x) && o14(xx) => { Next(1) }
            case o15(x) && o16(xx) => { Next(1) }
            case o17(x) && o18(xx) => { Next(1) }
            case o19(x) && o20(xx) => { Next(1) }
            case o21(x) && o22(xx) => { Next(1) }
            case o23(x) && o24(xx) => { Next(1) }
            case o25(x) && o26(xx) => { Next(1) }
            case o27(x) && o28(xx) => { Next(1) }
            case o29(x) && o30(xx) => { Next(1) }
            case o31(x) && o32(xx) => { Next(1) }
          }.serialize
          obs.subscribe((_: Int) => latch.countDown, (_: Throwable) => (), () => ())
          val thread1 = sendIndexFromThread(s1, 32768)
          val thread2 = sendIndexFromThread(s2, 32768)
          val thread3 = sendIndexFromThread(s3, 32768)
          val thread4 = sendIndexFromThread(s4, 32768)
          val thread5 = sendIndexFromThread(s5, 32768)
          val thread6 = sendIndexFromThread(s6, 32768)
          val thread7 = sendIndexFromThread(s7, 32768)
          val thread8 = sendIndexFromThread(s8, 32768)
          val thread9 = sendIndexFromThread(s9, 32768)
          val thread10 = sendIndexFromThread(s10, 32768)
          val thread11 = sendIndexFromThread(s11, 32768)
          val thread12 = sendIndexFromThread(s12, 32768)
          val thread13 = sendIndexFromThread(s13, 32768)
          val thread14 = sendIndexFromThread(s14, 32768)
          val thread15 = sendIndexFromThread(s15, 32768)
          val thread16 = sendIndexFromThread(s16, 32768)
          val thread17 = sendIndexFromThread(s17, 32768)
          val thread18 = sendIndexFromThread(s18, 32768)
          val thread19 = sendIndexFromThread(s19, 32768)
          val thread20 = sendIndexFromThread(s20, 32768)
          val thread21 = sendIndexFromThread(s21, 32768)
          val thread22 = sendIndexFromThread(s22, 32768)
          val thread23 = sendIndexFromThread(s23, 32768)
          val thread24 = sendIndexFromThread(s24, 32768)
          val thread25 = sendIndexFromThread(s25, 32768)
          val thread26 = sendIndexFromThread(s26, 32768)
          val thread27 = sendIndexFromThread(s27, 32768)
          val thread28 = sendIndexFromThread(s28, 32768)
          val thread29 = sendIndexFromThread(s29, 32768)
          val thread30 = sendIndexFromThread(s30, 32768)
          val thread31 = sendIndexFromThread(s31, 32768)
          val thread32 = sendIndexFromThread(s32, 32768)

          val thread33 = sendIndexFromThread(s33, 65536)
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

          latch.await
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

        }
        if (nDependent == 4) {

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

          val o1 = s1.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o2 = s2.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o3 = s3.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o4 = s4.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o5 = s5.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o6 = s6.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o7 = s7.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o8 = s8.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o9 = s9.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o10 = s10.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o11 = s11.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o12 = s12.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o13 = s13.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o14 = s14.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o15 = s15.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o16 = s16.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o17 = s17.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o18 = s18.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o19 = s19.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o20 = s20.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o21 = s21.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o22 = s22.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o23 = s23.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o24 = s24.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o25 = s25.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o26 = s26.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o27 = s27.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o28 = s28.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o29 = s29.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o30 = s30.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o31 = s31.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o32 = s32.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o33 = s33.onBackpressureBuffer.observeOn(schedulerToUse).p

          val latch = new CountDownLatch(524288)
          val obs = join {
            case o1(x) && o2(xx) && o33(xxx) => { Next(1) }
            case o3(x) && o4(xx) && o33(xxx) => { Next(1) }
            case o5(x) && o6(xx) && o33(xxx) => { Next(1) }
            case o7(x) && o8(xx) && o33(xxx) => { Next(1) }
            case o9(x) && o10(xx) => { Next(1) }
            case o11(x) && o12(xx) => { Next(1) }
            case o13(x) && o14(xx) => { Next(1) }
            case o15(x) && o16(xx) => { Next(1) }
            case o17(x) && o18(xx) => { Next(1) }
            case o19(x) && o20(xx) => { Next(1) }
            case o21(x) && o22(xx) => { Next(1) }
            case o23(x) && o24(xx) => { Next(1) }
            case o25(x) && o26(xx) => { Next(1) }
            case o27(x) && o28(xx) => { Next(1) }
            case o29(x) && o30(xx) => { Next(1) }
            case o31(x) && o32(xx) => { Next(1) }
          }.serialize
          obs.subscribe((_: Int) => latch.countDown, (_: Throwable) => (), () => ())
          val thread1 = sendIndexFromThread(s1, 32768)
          val thread2 = sendIndexFromThread(s2, 32768)
          val thread3 = sendIndexFromThread(s3, 32768)
          val thread4 = sendIndexFromThread(s4, 32768)
          val thread5 = sendIndexFromThread(s5, 32768)
          val thread6 = sendIndexFromThread(s6, 32768)
          val thread7 = sendIndexFromThread(s7, 32768)
          val thread8 = sendIndexFromThread(s8, 32768)
          val thread9 = sendIndexFromThread(s9, 32768)
          val thread10 = sendIndexFromThread(s10, 32768)
          val thread11 = sendIndexFromThread(s11, 32768)
          val thread12 = sendIndexFromThread(s12, 32768)
          val thread13 = sendIndexFromThread(s13, 32768)
          val thread14 = sendIndexFromThread(s14, 32768)
          val thread15 = sendIndexFromThread(s15, 32768)
          val thread16 = sendIndexFromThread(s16, 32768)
          val thread17 = sendIndexFromThread(s17, 32768)
          val thread18 = sendIndexFromThread(s18, 32768)
          val thread19 = sendIndexFromThread(s19, 32768)
          val thread20 = sendIndexFromThread(s20, 32768)
          val thread21 = sendIndexFromThread(s21, 32768)
          val thread22 = sendIndexFromThread(s22, 32768)
          val thread23 = sendIndexFromThread(s23, 32768)
          val thread24 = sendIndexFromThread(s24, 32768)
          val thread25 = sendIndexFromThread(s25, 32768)
          val thread26 = sendIndexFromThread(s26, 32768)
          val thread27 = sendIndexFromThread(s27, 32768)
          val thread28 = sendIndexFromThread(s28, 32768)
          val thread29 = sendIndexFromThread(s29, 32768)
          val thread30 = sendIndexFromThread(s30, 32768)
          val thread31 = sendIndexFromThread(s31, 32768)
          val thread32 = sendIndexFromThread(s32, 32768)

          val thread33 = sendIndexFromThread(s33, 131072)
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

          latch.await
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

        }
        if (nDependent == 8) {

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

          val o1 = s1.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o2 = s2.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o3 = s3.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o4 = s4.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o5 = s5.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o6 = s6.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o7 = s7.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o8 = s8.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o9 = s9.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o10 = s10.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o11 = s11.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o12 = s12.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o13 = s13.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o14 = s14.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o15 = s15.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o16 = s16.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o17 = s17.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o18 = s18.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o19 = s19.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o20 = s20.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o21 = s21.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o22 = s22.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o23 = s23.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o24 = s24.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o25 = s25.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o26 = s26.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o27 = s27.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o28 = s28.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o29 = s29.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o30 = s30.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o31 = s31.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o32 = s32.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o33 = s33.onBackpressureBuffer.observeOn(schedulerToUse).p

          val latch = new CountDownLatch(524288)
          val obs = join {
            case o1(x) && o2(xx) && o33(xxx) => { Next(1) }
            case o3(x) && o4(xx) && o33(xxx) => { Next(1) }
            case o5(x) && o6(xx) && o33(xxx) => { Next(1) }
            case o7(x) && o8(xx) && o33(xxx) => { Next(1) }
            case o9(x) && o10(xx) && o33(xxx) => { Next(1) }
            case o11(x) && o12(xx) && o33(xxx) => { Next(1) }
            case o13(x) && o14(xx) && o33(xxx) => { Next(1) }
            case o15(x) && o16(xx) && o33(xxx) => { Next(1) }
            case o17(x) && o18(xx) => { Next(1) }
            case o19(x) && o20(xx) => { Next(1) }
            case o21(x) && o22(xx) => { Next(1) }
            case o23(x) && o24(xx) => { Next(1) }
            case o25(x) && o26(xx) => { Next(1) }
            case o27(x) && o28(xx) => { Next(1) }
            case o29(x) && o30(xx) => { Next(1) }
            case o31(x) && o32(xx) => { Next(1) }
          }.serialize
          obs.subscribe((_: Int) => latch.countDown, (_: Throwable) => (), () => ())
          val thread1 = sendIndexFromThread(s1, 32768)
          val thread2 = sendIndexFromThread(s2, 32768)
          val thread3 = sendIndexFromThread(s3, 32768)
          val thread4 = sendIndexFromThread(s4, 32768)
          val thread5 = sendIndexFromThread(s5, 32768)
          val thread6 = sendIndexFromThread(s6, 32768)
          val thread7 = sendIndexFromThread(s7, 32768)
          val thread8 = sendIndexFromThread(s8, 32768)
          val thread9 = sendIndexFromThread(s9, 32768)
          val thread10 = sendIndexFromThread(s10, 32768)
          val thread11 = sendIndexFromThread(s11, 32768)
          val thread12 = sendIndexFromThread(s12, 32768)
          val thread13 = sendIndexFromThread(s13, 32768)
          val thread14 = sendIndexFromThread(s14, 32768)
          val thread15 = sendIndexFromThread(s15, 32768)
          val thread16 = sendIndexFromThread(s16, 32768)
          val thread17 = sendIndexFromThread(s17, 32768)
          val thread18 = sendIndexFromThread(s18, 32768)
          val thread19 = sendIndexFromThread(s19, 32768)
          val thread20 = sendIndexFromThread(s20, 32768)
          val thread21 = sendIndexFromThread(s21, 32768)
          val thread22 = sendIndexFromThread(s22, 32768)
          val thread23 = sendIndexFromThread(s23, 32768)
          val thread24 = sendIndexFromThread(s24, 32768)
          val thread25 = sendIndexFromThread(s25, 32768)
          val thread26 = sendIndexFromThread(s26, 32768)
          val thread27 = sendIndexFromThread(s27, 32768)
          val thread28 = sendIndexFromThread(s28, 32768)
          val thread29 = sendIndexFromThread(s29, 32768)
          val thread30 = sendIndexFromThread(s30, 32768)
          val thread31 = sendIndexFromThread(s31, 32768)
          val thread32 = sendIndexFromThread(s32, 32768)

          val thread33 = sendIndexFromThread(s33, 262144)
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

          latch.await
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

        }
        if (nDependent == 16) {

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

          val o1 = s1.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o2 = s2.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o3 = s3.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o4 = s4.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o5 = s5.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o6 = s6.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o7 = s7.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o8 = s8.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o9 = s9.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o10 = s10.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o11 = s11.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o12 = s12.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o13 = s13.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o14 = s14.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o15 = s15.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o16 = s16.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o17 = s17.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o18 = s18.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o19 = s19.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o20 = s20.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o21 = s21.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o22 = s22.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o23 = s23.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o24 = s24.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o25 = s25.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o26 = s26.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o27 = s27.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o28 = s28.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o29 = s29.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o30 = s30.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o31 = s31.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o32 = s32.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o33 = s33.onBackpressureBuffer.observeOn(schedulerToUse).p

          val latch = new CountDownLatch(524288)
          val obs = join {
            case o1(x) && o2(xx) && o33(xxx) => { Next(1) }
            case o3(x) && o4(xx) && o33(xxx) => { Next(1) }
            case o5(x) && o6(xx) && o33(xxx) => { Next(1) }
            case o7(x) && o8(xx) && o33(xxx) => { Next(1) }
            case o9(x) && o10(xx) && o33(xxx) => { Next(1) }
            case o11(x) && o12(xx) && o33(xxx) => { Next(1) }
            case o13(x) && o14(xx) && o33(xxx) => { Next(1) }
            case o15(x) && o16(xx) && o33(xxx) => { Next(1) }
            case o17(x) && o18(xx) && o33(xxx) => { Next(1) }
            case o19(x) && o20(xx) && o33(xxx) => { Next(1) }
            case o21(x) && o22(xx) && o33(xxx) => { Next(1) }
            case o23(x) && o24(xx) && o33(xxx) => { Next(1) }
            case o25(x) && o26(xx) && o33(xxx) => { Next(1) }
            case o27(x) && o28(xx) && o33(xxx) => { Next(1) }
            case o29(x) && o30(xx) && o33(xxx) => { Next(1) }
            case o31(x) && o32(xx) && o33(xxx) => { Next(1) }
          }.serialize
          obs.subscribe((_: Int) => latch.countDown, (_: Throwable) => (), () => ())
          val thread1 = sendIndexFromThread(s1, 32768)
          val thread2 = sendIndexFromThread(s2, 32768)
          val thread3 = sendIndexFromThread(s3, 32768)
          val thread4 = sendIndexFromThread(s4, 32768)
          val thread5 = sendIndexFromThread(s5, 32768)
          val thread6 = sendIndexFromThread(s6, 32768)
          val thread7 = sendIndexFromThread(s7, 32768)
          val thread8 = sendIndexFromThread(s8, 32768)
          val thread9 = sendIndexFromThread(s9, 32768)
          val thread10 = sendIndexFromThread(s10, 32768)
          val thread11 = sendIndexFromThread(s11, 32768)
          val thread12 = sendIndexFromThread(s12, 32768)
          val thread13 = sendIndexFromThread(s13, 32768)
          val thread14 = sendIndexFromThread(s14, 32768)
          val thread15 = sendIndexFromThread(s15, 32768)
          val thread16 = sendIndexFromThread(s16, 32768)
          val thread17 = sendIndexFromThread(s17, 32768)
          val thread18 = sendIndexFromThread(s18, 32768)
          val thread19 = sendIndexFromThread(s19, 32768)
          val thread20 = sendIndexFromThread(s20, 32768)
          val thread21 = sendIndexFromThread(s21, 32768)
          val thread22 = sendIndexFromThread(s22, 32768)
          val thread23 = sendIndexFromThread(s23, 32768)
          val thread24 = sendIndexFromThread(s24, 32768)
          val thread25 = sendIndexFromThread(s25, 32768)
          val thread26 = sendIndexFromThread(s26, 32768)
          val thread27 = sendIndexFromThread(s27, 32768)
          val thread28 = sendIndexFromThread(s28, 32768)
          val thread29 = sendIndexFromThread(s29, 32768)
          val thread30 = sendIndexFromThread(s30, 32768)
          val thread31 = sendIndexFromThread(s31, 32768)
          val thread32 = sendIndexFromThread(s32, 32768)

          val thread33 = sendIndexFromThread(s33, 524288)
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

          latch.await
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

        }
      }
      using(NDependentCasesOutRange) curve ("Deterministic Choice") in { nDependent =>
        implicit val checkOrder = scala.async.Join.InOrder
        if (nDependent == 2) {

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

          val o1 = s1.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o2 = s2.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o3 = s3.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o4 = s4.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o5 = s5.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o6 = s6.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o7 = s7.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o8 = s8.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o9 = s9.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o10 = s10.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o11 = s11.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o12 = s12.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o13 = s13.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o14 = s14.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o15 = s15.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o16 = s16.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o17 = s17.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o18 = s18.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o19 = s19.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o20 = s20.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o21 = s21.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o22 = s22.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o23 = s23.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o24 = s24.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o25 = s25.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o26 = s26.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o27 = s27.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o28 = s28.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o29 = s29.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o30 = s30.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o31 = s31.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o32 = s32.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o33 = s33.onBackpressureBuffer.observeOn(schedulerToUse).p

          val latch = new CountDownLatch(524288)
          val obs = join {
            case o1(x) && o2(xx) && o33(xxx) => { Next(1) }
            case o3(x) && o4(xx) && o33(xxx) => { Next(1) }
            case o5(x) && o6(xx) => { Next(1) }
            case o7(x) && o8(xx) => { Next(1) }
            case o9(x) && o10(xx) => { Next(1) }
            case o11(x) && o12(xx) => { Next(1) }
            case o13(x) && o14(xx) => { Next(1) }
            case o15(x) && o16(xx) => { Next(1) }
            case o17(x) && o18(xx) => { Next(1) }
            case o19(x) && o20(xx) => { Next(1) }
            case o21(x) && o22(xx) => { Next(1) }
            case o23(x) && o24(xx) => { Next(1) }
            case o25(x) && o26(xx) => { Next(1) }
            case o27(x) && o28(xx) => { Next(1) }
            case o29(x) && o30(xx) => { Next(1) }
            case o31(x) && o32(xx) => { Next(1) }
          }.serialize
          obs.subscribe((_: Int) => latch.countDown, (_: Throwable) => (), () => ())
          val thread1 = sendIndexFromThread(s1, 32768)
          val thread2 = sendIndexFromThread(s2, 32768)
          val thread3 = sendIndexFromThread(s3, 32768)
          val thread4 = sendIndexFromThread(s4, 32768)
          val thread5 = sendIndexFromThread(s5, 32768)
          val thread6 = sendIndexFromThread(s6, 32768)
          val thread7 = sendIndexFromThread(s7, 32768)
          val thread8 = sendIndexFromThread(s8, 32768)
          val thread9 = sendIndexFromThread(s9, 32768)
          val thread10 = sendIndexFromThread(s10, 32768)
          val thread11 = sendIndexFromThread(s11, 32768)
          val thread12 = sendIndexFromThread(s12, 32768)
          val thread13 = sendIndexFromThread(s13, 32768)
          val thread14 = sendIndexFromThread(s14, 32768)
          val thread15 = sendIndexFromThread(s15, 32768)
          val thread16 = sendIndexFromThread(s16, 32768)
          val thread17 = sendIndexFromThread(s17, 32768)
          val thread18 = sendIndexFromThread(s18, 32768)
          val thread19 = sendIndexFromThread(s19, 32768)
          val thread20 = sendIndexFromThread(s20, 32768)
          val thread21 = sendIndexFromThread(s21, 32768)
          val thread22 = sendIndexFromThread(s22, 32768)
          val thread23 = sendIndexFromThread(s23, 32768)
          val thread24 = sendIndexFromThread(s24, 32768)
          val thread25 = sendIndexFromThread(s25, 32768)
          val thread26 = sendIndexFromThread(s26, 32768)
          val thread27 = sendIndexFromThread(s27, 32768)
          val thread28 = sendIndexFromThread(s28, 32768)
          val thread29 = sendIndexFromThread(s29, 32768)
          val thread30 = sendIndexFromThread(s30, 32768)
          val thread31 = sendIndexFromThread(s31, 32768)
          val thread32 = sendIndexFromThread(s32, 32768)

          val thread33 = sendIndexFromThread(s33, 65536)
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

          latch.await
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

        }
        if (nDependent == 4) {

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

          val o1 = s1.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o2 = s2.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o3 = s3.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o4 = s4.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o5 = s5.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o6 = s6.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o7 = s7.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o8 = s8.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o9 = s9.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o10 = s10.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o11 = s11.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o12 = s12.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o13 = s13.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o14 = s14.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o15 = s15.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o16 = s16.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o17 = s17.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o18 = s18.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o19 = s19.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o20 = s20.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o21 = s21.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o22 = s22.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o23 = s23.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o24 = s24.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o25 = s25.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o26 = s26.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o27 = s27.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o28 = s28.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o29 = s29.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o30 = s30.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o31 = s31.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o32 = s32.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o33 = s33.onBackpressureBuffer.observeOn(schedulerToUse).p

          val latch = new CountDownLatch(524288)
          val obs = join {
            case o1(x) && o2(xx) && o33(xxx) => { Next(1) }
            case o3(x) && o4(xx) && o33(xxx) => { Next(1) }
            case o5(x) && o6(xx) && o33(xxx) => { Next(1) }
            case o7(x) && o8(xx) && o33(xxx) => { Next(1) }
            case o9(x) && o10(xx) => { Next(1) }
            case o11(x) && o12(xx) => { Next(1) }
            case o13(x) && o14(xx) => { Next(1) }
            case o15(x) && o16(xx) => { Next(1) }
            case o17(x) && o18(xx) => { Next(1) }
            case o19(x) && o20(xx) => { Next(1) }
            case o21(x) && o22(xx) => { Next(1) }
            case o23(x) && o24(xx) => { Next(1) }
            case o25(x) && o26(xx) => { Next(1) }
            case o27(x) && o28(xx) => { Next(1) }
            case o29(x) && o30(xx) => { Next(1) }
            case o31(x) && o32(xx) => { Next(1) }
          }.serialize
          obs.subscribe((_: Int) => latch.countDown, (_: Throwable) => (), () => ())
          val thread1 = sendIndexFromThread(s1, 32768)
          val thread2 = sendIndexFromThread(s2, 32768)
          val thread3 = sendIndexFromThread(s3, 32768)
          val thread4 = sendIndexFromThread(s4, 32768)
          val thread5 = sendIndexFromThread(s5, 32768)
          val thread6 = sendIndexFromThread(s6, 32768)
          val thread7 = sendIndexFromThread(s7, 32768)
          val thread8 = sendIndexFromThread(s8, 32768)
          val thread9 = sendIndexFromThread(s9, 32768)
          val thread10 = sendIndexFromThread(s10, 32768)
          val thread11 = sendIndexFromThread(s11, 32768)
          val thread12 = sendIndexFromThread(s12, 32768)
          val thread13 = sendIndexFromThread(s13, 32768)
          val thread14 = sendIndexFromThread(s14, 32768)
          val thread15 = sendIndexFromThread(s15, 32768)
          val thread16 = sendIndexFromThread(s16, 32768)
          val thread17 = sendIndexFromThread(s17, 32768)
          val thread18 = sendIndexFromThread(s18, 32768)
          val thread19 = sendIndexFromThread(s19, 32768)
          val thread20 = sendIndexFromThread(s20, 32768)
          val thread21 = sendIndexFromThread(s21, 32768)
          val thread22 = sendIndexFromThread(s22, 32768)
          val thread23 = sendIndexFromThread(s23, 32768)
          val thread24 = sendIndexFromThread(s24, 32768)
          val thread25 = sendIndexFromThread(s25, 32768)
          val thread26 = sendIndexFromThread(s26, 32768)
          val thread27 = sendIndexFromThread(s27, 32768)
          val thread28 = sendIndexFromThread(s28, 32768)
          val thread29 = sendIndexFromThread(s29, 32768)
          val thread30 = sendIndexFromThread(s30, 32768)
          val thread31 = sendIndexFromThread(s31, 32768)
          val thread32 = sendIndexFromThread(s32, 32768)

          val thread33 = sendIndexFromThread(s33, 131072)
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

          latch.await
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

        }
        if (nDependent == 8) {

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

          val o1 = s1.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o2 = s2.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o3 = s3.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o4 = s4.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o5 = s5.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o6 = s6.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o7 = s7.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o8 = s8.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o9 = s9.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o10 = s10.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o11 = s11.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o12 = s12.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o13 = s13.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o14 = s14.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o15 = s15.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o16 = s16.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o17 = s17.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o18 = s18.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o19 = s19.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o20 = s20.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o21 = s21.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o22 = s22.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o23 = s23.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o24 = s24.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o25 = s25.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o26 = s26.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o27 = s27.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o28 = s28.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o29 = s29.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o30 = s30.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o31 = s31.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o32 = s32.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o33 = s33.onBackpressureBuffer.observeOn(schedulerToUse).p

          val latch = new CountDownLatch(524288)
          val obs = join {
            case o1(x) && o2(xx) && o33(xxx) => { Next(1) }
            case o3(x) && o4(xx) && o33(xxx) => { Next(1) }
            case o5(x) && o6(xx) && o33(xxx) => { Next(1) }
            case o7(x) && o8(xx) && o33(xxx) => { Next(1) }
            case o9(x) && o10(xx) && o33(xxx) => { Next(1) }
            case o11(x) && o12(xx) && o33(xxx) => { Next(1) }
            case o13(x) && o14(xx) && o33(xxx) => { Next(1) }
            case o15(x) && o16(xx) && o33(xxx) => { Next(1) }
            case o17(x) && o18(xx) => { Next(1) }
            case o19(x) && o20(xx) => { Next(1) }
            case o21(x) && o22(xx) => { Next(1) }
            case o23(x) && o24(xx) => { Next(1) }
            case o25(x) && o26(xx) => { Next(1) }
            case o27(x) && o28(xx) => { Next(1) }
            case o29(x) && o30(xx) => { Next(1) }
            case o31(x) && o32(xx) => { Next(1) }
          }.serialize
          obs.subscribe((_: Int) => latch.countDown, (_: Throwable) => (), () => ())
          val thread1 = sendIndexFromThread(s1, 32768)
          val thread2 = sendIndexFromThread(s2, 32768)
          val thread3 = sendIndexFromThread(s3, 32768)
          val thread4 = sendIndexFromThread(s4, 32768)
          val thread5 = sendIndexFromThread(s5, 32768)
          val thread6 = sendIndexFromThread(s6, 32768)
          val thread7 = sendIndexFromThread(s7, 32768)
          val thread8 = sendIndexFromThread(s8, 32768)
          val thread9 = sendIndexFromThread(s9, 32768)
          val thread10 = sendIndexFromThread(s10, 32768)
          val thread11 = sendIndexFromThread(s11, 32768)
          val thread12 = sendIndexFromThread(s12, 32768)
          val thread13 = sendIndexFromThread(s13, 32768)
          val thread14 = sendIndexFromThread(s14, 32768)
          val thread15 = sendIndexFromThread(s15, 32768)
          val thread16 = sendIndexFromThread(s16, 32768)
          val thread17 = sendIndexFromThread(s17, 32768)
          val thread18 = sendIndexFromThread(s18, 32768)
          val thread19 = sendIndexFromThread(s19, 32768)
          val thread20 = sendIndexFromThread(s20, 32768)
          val thread21 = sendIndexFromThread(s21, 32768)
          val thread22 = sendIndexFromThread(s22, 32768)
          val thread23 = sendIndexFromThread(s23, 32768)
          val thread24 = sendIndexFromThread(s24, 32768)
          val thread25 = sendIndexFromThread(s25, 32768)
          val thread26 = sendIndexFromThread(s26, 32768)
          val thread27 = sendIndexFromThread(s27, 32768)
          val thread28 = sendIndexFromThread(s28, 32768)
          val thread29 = sendIndexFromThread(s29, 32768)
          val thread30 = sendIndexFromThread(s30, 32768)
          val thread31 = sendIndexFromThread(s31, 32768)
          val thread32 = sendIndexFromThread(s32, 32768)

          val thread33 = sendIndexFromThread(s33, 262144)
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

          latch.await
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

        }
        if (nDependent == 16) {

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

          val o1 = s1.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o2 = s2.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o3 = s3.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o4 = s4.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o5 = s5.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o6 = s6.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o7 = s7.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o8 = s8.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o9 = s9.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o10 = s10.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o11 = s11.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o12 = s12.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o13 = s13.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o14 = s14.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o15 = s15.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o16 = s16.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o17 = s17.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o18 = s18.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o19 = s19.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o20 = s20.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o21 = s21.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o22 = s22.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o23 = s23.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o24 = s24.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o25 = s25.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o26 = s26.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o27 = s27.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o28 = s28.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o29 = s29.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o30 = s30.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o31 = s31.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o32 = s32.onBackpressureBuffer.observeOn(schedulerToUse).p
          val o33 = s33.onBackpressureBuffer.observeOn(schedulerToUse).p

          val latch = new CountDownLatch(524288)
          val obs = join {
            case o1(x) && o2(xx) && o33(xxx) => { Next(1) }
            case o3(x) && o4(xx) && o33(xxx) => { Next(1) }
            case o5(x) && o6(xx) && o33(xxx) => { Next(1) }
            case o7(x) && o8(xx) && o33(xxx) => { Next(1) }
            case o9(x) && o10(xx) && o33(xxx) => { Next(1) }
            case o11(x) && o12(xx) && o33(xxx) => { Next(1) }
            case o13(x) && o14(xx) && o33(xxx) => { Next(1) }
            case o15(x) && o16(xx) && o33(xxx) => { Next(1) }
            case o17(x) && o18(xx) && o33(xxx) => { Next(1) }
            case o19(x) && o20(xx) && o33(xxx) => { Next(1) }
            case o21(x) && o22(xx) && o33(xxx) => { Next(1) }
            case o23(x) && o24(xx) && o33(xxx) => { Next(1) }
            case o25(x) && o26(xx) && o33(xxx) => { Next(1) }
            case o27(x) && o28(xx) && o33(xxx) => { Next(1) }
            case o29(x) && o30(xx) && o33(xxx) => { Next(1) }
            case o31(x) && o32(xx) && o33(xxx) => { Next(1) }
          }.serialize
          obs.subscribe((_: Int) => latch.countDown, (_: Throwable) => (), () => ())
          val thread1 = sendIndexFromThread(s1, 32768)
          val thread2 = sendIndexFromThread(s2, 32768)
          val thread3 = sendIndexFromThread(s3, 32768)
          val thread4 = sendIndexFromThread(s4, 32768)
          val thread5 = sendIndexFromThread(s5, 32768)
          val thread6 = sendIndexFromThread(s6, 32768)
          val thread7 = sendIndexFromThread(s7, 32768)
          val thread8 = sendIndexFromThread(s8, 32768)
          val thread9 = sendIndexFromThread(s9, 32768)
          val thread10 = sendIndexFromThread(s10, 32768)
          val thread11 = sendIndexFromThread(s11, 32768)
          val thread12 = sendIndexFromThread(s12, 32768)
          val thread13 = sendIndexFromThread(s13, 32768)
          val thread14 = sendIndexFromThread(s14, 32768)
          val thread15 = sendIndexFromThread(s15, 32768)
          val thread16 = sendIndexFromThread(s16, 32768)
          val thread17 = sendIndexFromThread(s17, 32768)
          val thread18 = sendIndexFromThread(s18, 32768)
          val thread19 = sendIndexFromThread(s19, 32768)
          val thread20 = sendIndexFromThread(s20, 32768)
          val thread21 = sendIndexFromThread(s21, 32768)
          val thread22 = sendIndexFromThread(s22, 32768)
          val thread23 = sendIndexFromThread(s23, 32768)
          val thread24 = sendIndexFromThread(s24, 32768)
          val thread25 = sendIndexFromThread(s25, 32768)
          val thread26 = sendIndexFromThread(s26, 32768)
          val thread27 = sendIndexFromThread(s27, 32768)
          val thread28 = sendIndexFromThread(s28, 32768)
          val thread29 = sendIndexFromThread(s29, 32768)
          val thread30 = sendIndexFromThread(s30, 32768)
          val thread31 = sendIndexFromThread(s31, 32768)
          val thread32 = sendIndexFromThread(s32, 32768)

          val thread33 = sendIndexFromThread(s33, 524288)
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

          latch.await
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

        }
      }
      using(NDependentCasesOutRange) curve ("ReactiveX") in { nDependent =>
        import rx.lang.scala.ImplicitFunctionConversions._
        import rx.lang.scala.JavaConversions._
        if (nDependent == 2) {

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

          val o1 = s1.onBackpressureBuffer.observeOn(schedulerToUse)
          val o2 = s2.onBackpressureBuffer.observeOn(schedulerToUse)
          val o3 = s3.onBackpressureBuffer.observeOn(schedulerToUse)
          val o4 = s4.onBackpressureBuffer.observeOn(schedulerToUse)
          val o5 = s5.onBackpressureBuffer.observeOn(schedulerToUse)
          val o6 = s6.onBackpressureBuffer.observeOn(schedulerToUse)
          val o7 = s7.onBackpressureBuffer.observeOn(schedulerToUse)
          val o8 = s8.onBackpressureBuffer.observeOn(schedulerToUse)
          val o9 = s9.onBackpressureBuffer.observeOn(schedulerToUse)
          val o10 = s10.onBackpressureBuffer.observeOn(schedulerToUse)
          val o11 = s11.onBackpressureBuffer.observeOn(schedulerToUse)
          val o12 = s12.onBackpressureBuffer.observeOn(schedulerToUse)
          val o13 = s13.onBackpressureBuffer.observeOn(schedulerToUse)
          val o14 = s14.onBackpressureBuffer.observeOn(schedulerToUse)
          val o15 = s15.onBackpressureBuffer.observeOn(schedulerToUse)
          val o16 = s16.onBackpressureBuffer.observeOn(schedulerToUse)
          val o17 = s17.onBackpressureBuffer.observeOn(schedulerToUse)
          val o18 = s18.onBackpressureBuffer.observeOn(schedulerToUse)
          val o19 = s19.onBackpressureBuffer.observeOn(schedulerToUse)
          val o20 = s20.onBackpressureBuffer.observeOn(schedulerToUse)
          val o21 = s21.onBackpressureBuffer.observeOn(schedulerToUse)
          val o22 = s22.onBackpressureBuffer.observeOn(schedulerToUse)
          val o23 = s23.onBackpressureBuffer.observeOn(schedulerToUse)
          val o24 = s24.onBackpressureBuffer.observeOn(schedulerToUse)
          val o25 = s25.onBackpressureBuffer.observeOn(schedulerToUse)
          val o26 = s26.onBackpressureBuffer.observeOn(schedulerToUse)
          val o27 = s27.onBackpressureBuffer.observeOn(schedulerToUse)
          val o28 = s28.onBackpressureBuffer.observeOn(schedulerToUse)
          val o29 = s29.onBackpressureBuffer.observeOn(schedulerToUse)
          val o30 = s30.onBackpressureBuffer.observeOn(schedulerToUse)
          val o31 = s31.onBackpressureBuffer.observeOn(schedulerToUse)
          val o32 = s32.onBackpressureBuffer.observeOn(schedulerToUse)
          val o33 = s33.onBackpressureBuffer.observeOn(schedulerToUse)

          val latch = new CountDownLatch(524288)
          val p1 = RxJoinObservable.from(o1).and(o2).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p2 = RxJoinObservable.from(o3).and(o4).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p3 = RxJoinObservable.from(o5).and(o6).then((x: Int, xx: Int) => { 1 })
          val p4 = RxJoinObservable.from(o7).and(o8).then((x: Int, xx: Int) => { 1 })
          val p5 = RxJoinObservable.from(o9).and(o10).then((x: Int, xx: Int) => { 1 })
          val p6 = RxJoinObservable.from(o11).and(o12).then((x: Int, xx: Int) => { 1 })
          val p7 = RxJoinObservable.from(o13).and(o14).then((x: Int, xx: Int) => { 1 })
          val p8 = RxJoinObservable.from(o15).and(o16).then((x: Int, xx: Int) => { 1 })
          val p9 = RxJoinObservable.from(o17).and(o18).then((x: Int, xx: Int) => { 1 })
          val p10 = RxJoinObservable.from(o19).and(o20).then((x: Int, xx: Int) => { 1 })
          val p11 = RxJoinObservable.from(o21).and(o22).then((x: Int, xx: Int) => { 1 })
          val p12 = RxJoinObservable.from(o23).and(o24).then((x: Int, xx: Int) => { 1 })
          val p13 = RxJoinObservable.from(o25).and(o26).then((x: Int, xx: Int) => { 1 })
          val p14 = RxJoinObservable.from(o27).and(o28).then((x: Int, xx: Int) => { 1 })
          val p15 = RxJoinObservable.from(o29).and(o30).then((x: Int, xx: Int) => { 1 })
          val p16 = RxJoinObservable.from(o31).and(o32).then((x: Int, xx: Int) => { 1 })
          val obs = RxJoinObservable.when(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16).toObservable
          obs.subscribe((_: Int) => latch.countDown, (_: Throwable) => (), () => ())
          val thread1 = sendIndexFromThread(s1, 32768)
          val thread2 = sendIndexFromThread(s2, 32768)
          val thread3 = sendIndexFromThread(s3, 32768)
          val thread4 = sendIndexFromThread(s4, 32768)
          val thread5 = sendIndexFromThread(s5, 32768)
          val thread6 = sendIndexFromThread(s6, 32768)
          val thread7 = sendIndexFromThread(s7, 32768)
          val thread8 = sendIndexFromThread(s8, 32768)
          val thread9 = sendIndexFromThread(s9, 32768)
          val thread10 = sendIndexFromThread(s10, 32768)
          val thread11 = sendIndexFromThread(s11, 32768)
          val thread12 = sendIndexFromThread(s12, 32768)
          val thread13 = sendIndexFromThread(s13, 32768)
          val thread14 = sendIndexFromThread(s14, 32768)
          val thread15 = sendIndexFromThread(s15, 32768)
          val thread16 = sendIndexFromThread(s16, 32768)
          val thread17 = sendIndexFromThread(s17, 32768)
          val thread18 = sendIndexFromThread(s18, 32768)
          val thread19 = sendIndexFromThread(s19, 32768)
          val thread20 = sendIndexFromThread(s20, 32768)
          val thread21 = sendIndexFromThread(s21, 32768)
          val thread22 = sendIndexFromThread(s22, 32768)
          val thread23 = sendIndexFromThread(s23, 32768)
          val thread24 = sendIndexFromThread(s24, 32768)
          val thread25 = sendIndexFromThread(s25, 32768)
          val thread26 = sendIndexFromThread(s26, 32768)
          val thread27 = sendIndexFromThread(s27, 32768)
          val thread28 = sendIndexFromThread(s28, 32768)
          val thread29 = sendIndexFromThread(s29, 32768)
          val thread30 = sendIndexFromThread(s30, 32768)
          val thread31 = sendIndexFromThread(s31, 32768)
          val thread32 = sendIndexFromThread(s32, 32768)

          val thread33 = sendIndexFromThread(s33, 65536)
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

          latch.await
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

        }
        if (nDependent == 4) {

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

          val o1 = s1.onBackpressureBuffer.observeOn(schedulerToUse)
          val o2 = s2.onBackpressureBuffer.observeOn(schedulerToUse)
          val o3 = s3.onBackpressureBuffer.observeOn(schedulerToUse)
          val o4 = s4.onBackpressureBuffer.observeOn(schedulerToUse)
          val o5 = s5.onBackpressureBuffer.observeOn(schedulerToUse)
          val o6 = s6.onBackpressureBuffer.observeOn(schedulerToUse)
          val o7 = s7.onBackpressureBuffer.observeOn(schedulerToUse)
          val o8 = s8.onBackpressureBuffer.observeOn(schedulerToUse)
          val o9 = s9.onBackpressureBuffer.observeOn(schedulerToUse)
          val o10 = s10.onBackpressureBuffer.observeOn(schedulerToUse)
          val o11 = s11.onBackpressureBuffer.observeOn(schedulerToUse)
          val o12 = s12.onBackpressureBuffer.observeOn(schedulerToUse)
          val o13 = s13.onBackpressureBuffer.observeOn(schedulerToUse)
          val o14 = s14.onBackpressureBuffer.observeOn(schedulerToUse)
          val o15 = s15.onBackpressureBuffer.observeOn(schedulerToUse)
          val o16 = s16.onBackpressureBuffer.observeOn(schedulerToUse)
          val o17 = s17.onBackpressureBuffer.observeOn(schedulerToUse)
          val o18 = s18.onBackpressureBuffer.observeOn(schedulerToUse)
          val o19 = s19.onBackpressureBuffer.observeOn(schedulerToUse)
          val o20 = s20.onBackpressureBuffer.observeOn(schedulerToUse)
          val o21 = s21.onBackpressureBuffer.observeOn(schedulerToUse)
          val o22 = s22.onBackpressureBuffer.observeOn(schedulerToUse)
          val o23 = s23.onBackpressureBuffer.observeOn(schedulerToUse)
          val o24 = s24.onBackpressureBuffer.observeOn(schedulerToUse)
          val o25 = s25.onBackpressureBuffer.observeOn(schedulerToUse)
          val o26 = s26.onBackpressureBuffer.observeOn(schedulerToUse)
          val o27 = s27.onBackpressureBuffer.observeOn(schedulerToUse)
          val o28 = s28.onBackpressureBuffer.observeOn(schedulerToUse)
          val o29 = s29.onBackpressureBuffer.observeOn(schedulerToUse)
          val o30 = s30.onBackpressureBuffer.observeOn(schedulerToUse)
          val o31 = s31.onBackpressureBuffer.observeOn(schedulerToUse)
          val o32 = s32.onBackpressureBuffer.observeOn(schedulerToUse)
          val o33 = s33.onBackpressureBuffer.observeOn(schedulerToUse)

          val latch = new CountDownLatch(524288)
          val p1 = RxJoinObservable.from(o1).and(o2).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p2 = RxJoinObservable.from(o3).and(o4).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p3 = RxJoinObservable.from(o5).and(o6).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p4 = RxJoinObservable.from(o7).and(o8).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p5 = RxJoinObservable.from(o9).and(o10).then((x: Int, xx: Int) => { 1 })
          val p6 = RxJoinObservable.from(o11).and(o12).then((x: Int, xx: Int) => { 1 })
          val p7 = RxJoinObservable.from(o13).and(o14).then((x: Int, xx: Int) => { 1 })
          val p8 = RxJoinObservable.from(o15).and(o16).then((x: Int, xx: Int) => { 1 })
          val p9 = RxJoinObservable.from(o17).and(o18).then((x: Int, xx: Int) => { 1 })
          val p10 = RxJoinObservable.from(o19).and(o20).then((x: Int, xx: Int) => { 1 })
          val p11 = RxJoinObservable.from(o21).and(o22).then((x: Int, xx: Int) => { 1 })
          val p12 = RxJoinObservable.from(o23).and(o24).then((x: Int, xx: Int) => { 1 })
          val p13 = RxJoinObservable.from(o25).and(o26).then((x: Int, xx: Int) => { 1 })
          val p14 = RxJoinObservable.from(o27).and(o28).then((x: Int, xx: Int) => { 1 })
          val p15 = RxJoinObservable.from(o29).and(o30).then((x: Int, xx: Int) => { 1 })
          val p16 = RxJoinObservable.from(o31).and(o32).then((x: Int, xx: Int) => { 1 })
          val obs = RxJoinObservable.when(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16).toObservable
          obs.subscribe((_: Int) => latch.countDown, (_: Throwable) => (), () => ())
          val thread1 = sendIndexFromThread(s1, 32768)
          val thread2 = sendIndexFromThread(s2, 32768)
          val thread3 = sendIndexFromThread(s3, 32768)
          val thread4 = sendIndexFromThread(s4, 32768)
          val thread5 = sendIndexFromThread(s5, 32768)
          val thread6 = sendIndexFromThread(s6, 32768)
          val thread7 = sendIndexFromThread(s7, 32768)
          val thread8 = sendIndexFromThread(s8, 32768)
          val thread9 = sendIndexFromThread(s9, 32768)
          val thread10 = sendIndexFromThread(s10, 32768)
          val thread11 = sendIndexFromThread(s11, 32768)
          val thread12 = sendIndexFromThread(s12, 32768)
          val thread13 = sendIndexFromThread(s13, 32768)
          val thread14 = sendIndexFromThread(s14, 32768)
          val thread15 = sendIndexFromThread(s15, 32768)
          val thread16 = sendIndexFromThread(s16, 32768)
          val thread17 = sendIndexFromThread(s17, 32768)
          val thread18 = sendIndexFromThread(s18, 32768)
          val thread19 = sendIndexFromThread(s19, 32768)
          val thread20 = sendIndexFromThread(s20, 32768)
          val thread21 = sendIndexFromThread(s21, 32768)
          val thread22 = sendIndexFromThread(s22, 32768)
          val thread23 = sendIndexFromThread(s23, 32768)
          val thread24 = sendIndexFromThread(s24, 32768)
          val thread25 = sendIndexFromThread(s25, 32768)
          val thread26 = sendIndexFromThread(s26, 32768)
          val thread27 = sendIndexFromThread(s27, 32768)
          val thread28 = sendIndexFromThread(s28, 32768)
          val thread29 = sendIndexFromThread(s29, 32768)
          val thread30 = sendIndexFromThread(s30, 32768)
          val thread31 = sendIndexFromThread(s31, 32768)
          val thread32 = sendIndexFromThread(s32, 32768)

          val thread33 = sendIndexFromThread(s33, 131072)
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

          latch.await
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

        }
        if (nDependent == 8) {

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

          val o1 = s1.onBackpressureBuffer.observeOn(schedulerToUse)
          val o2 = s2.onBackpressureBuffer.observeOn(schedulerToUse)
          val o3 = s3.onBackpressureBuffer.observeOn(schedulerToUse)
          val o4 = s4.onBackpressureBuffer.observeOn(schedulerToUse)
          val o5 = s5.onBackpressureBuffer.observeOn(schedulerToUse)
          val o6 = s6.onBackpressureBuffer.observeOn(schedulerToUse)
          val o7 = s7.onBackpressureBuffer.observeOn(schedulerToUse)
          val o8 = s8.onBackpressureBuffer.observeOn(schedulerToUse)
          val o9 = s9.onBackpressureBuffer.observeOn(schedulerToUse)
          val o10 = s10.onBackpressureBuffer.observeOn(schedulerToUse)
          val o11 = s11.onBackpressureBuffer.observeOn(schedulerToUse)
          val o12 = s12.onBackpressureBuffer.observeOn(schedulerToUse)
          val o13 = s13.onBackpressureBuffer.observeOn(schedulerToUse)
          val o14 = s14.onBackpressureBuffer.observeOn(schedulerToUse)
          val o15 = s15.onBackpressureBuffer.observeOn(schedulerToUse)
          val o16 = s16.onBackpressureBuffer.observeOn(schedulerToUse)
          val o17 = s17.onBackpressureBuffer.observeOn(schedulerToUse)
          val o18 = s18.onBackpressureBuffer.observeOn(schedulerToUse)
          val o19 = s19.onBackpressureBuffer.observeOn(schedulerToUse)
          val o20 = s20.onBackpressureBuffer.observeOn(schedulerToUse)
          val o21 = s21.onBackpressureBuffer.observeOn(schedulerToUse)
          val o22 = s22.onBackpressureBuffer.observeOn(schedulerToUse)
          val o23 = s23.onBackpressureBuffer.observeOn(schedulerToUse)
          val o24 = s24.onBackpressureBuffer.observeOn(schedulerToUse)
          val o25 = s25.onBackpressureBuffer.observeOn(schedulerToUse)
          val o26 = s26.onBackpressureBuffer.observeOn(schedulerToUse)
          val o27 = s27.onBackpressureBuffer.observeOn(schedulerToUse)
          val o28 = s28.onBackpressureBuffer.observeOn(schedulerToUse)
          val o29 = s29.onBackpressureBuffer.observeOn(schedulerToUse)
          val o30 = s30.onBackpressureBuffer.observeOn(schedulerToUse)
          val o31 = s31.onBackpressureBuffer.observeOn(schedulerToUse)
          val o32 = s32.onBackpressureBuffer.observeOn(schedulerToUse)
          val o33 = s33.onBackpressureBuffer.observeOn(schedulerToUse)

          val latch = new CountDownLatch(524288)
          val p1 = RxJoinObservable.from(o1).and(o2).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p2 = RxJoinObservable.from(o3).and(o4).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p3 = RxJoinObservable.from(o5).and(o6).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p4 = RxJoinObservable.from(o7).and(o8).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p5 = RxJoinObservable.from(o9).and(o10).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p6 = RxJoinObservable.from(o11).and(o12).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p7 = RxJoinObservable.from(o13).and(o14).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p8 = RxJoinObservable.from(o15).and(o16).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p9 = RxJoinObservable.from(o17).and(o18).then((x: Int, xx: Int) => { 1 })
          val p10 = RxJoinObservable.from(o19).and(o20).then((x: Int, xx: Int) => { 1 })
          val p11 = RxJoinObservable.from(o21).and(o22).then((x: Int, xx: Int) => { 1 })
          val p12 = RxJoinObservable.from(o23).and(o24).then((x: Int, xx: Int) => { 1 })
          val p13 = RxJoinObservable.from(o25).and(o26).then((x: Int, xx: Int) => { 1 })
          val p14 = RxJoinObservable.from(o27).and(o28).then((x: Int, xx: Int) => { 1 })
          val p15 = RxJoinObservable.from(o29).and(o30).then((x: Int, xx: Int) => { 1 })
          val p16 = RxJoinObservable.from(o31).and(o32).then((x: Int, xx: Int) => { 1 })
          val obs = RxJoinObservable.when(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16).toObservable
          obs.subscribe((_: Int) => latch.countDown, (_: Throwable) => (), () => ())
          val thread1 = sendIndexFromThread(s1, 32768)
          val thread2 = sendIndexFromThread(s2, 32768)
          val thread3 = sendIndexFromThread(s3, 32768)
          val thread4 = sendIndexFromThread(s4, 32768)
          val thread5 = sendIndexFromThread(s5, 32768)
          val thread6 = sendIndexFromThread(s6, 32768)
          val thread7 = sendIndexFromThread(s7, 32768)
          val thread8 = sendIndexFromThread(s8, 32768)
          val thread9 = sendIndexFromThread(s9, 32768)
          val thread10 = sendIndexFromThread(s10, 32768)
          val thread11 = sendIndexFromThread(s11, 32768)
          val thread12 = sendIndexFromThread(s12, 32768)
          val thread13 = sendIndexFromThread(s13, 32768)
          val thread14 = sendIndexFromThread(s14, 32768)
          val thread15 = sendIndexFromThread(s15, 32768)
          val thread16 = sendIndexFromThread(s16, 32768)
          val thread17 = sendIndexFromThread(s17, 32768)
          val thread18 = sendIndexFromThread(s18, 32768)
          val thread19 = sendIndexFromThread(s19, 32768)
          val thread20 = sendIndexFromThread(s20, 32768)
          val thread21 = sendIndexFromThread(s21, 32768)
          val thread22 = sendIndexFromThread(s22, 32768)
          val thread23 = sendIndexFromThread(s23, 32768)
          val thread24 = sendIndexFromThread(s24, 32768)
          val thread25 = sendIndexFromThread(s25, 32768)
          val thread26 = sendIndexFromThread(s26, 32768)
          val thread27 = sendIndexFromThread(s27, 32768)
          val thread28 = sendIndexFromThread(s28, 32768)
          val thread29 = sendIndexFromThread(s29, 32768)
          val thread30 = sendIndexFromThread(s30, 32768)
          val thread31 = sendIndexFromThread(s31, 32768)
          val thread32 = sendIndexFromThread(s32, 32768)

          val thread33 = sendIndexFromThread(s33, 262144)
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

          latch.await
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

        }
        if (nDependent == 16) {

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

          val o1 = s1.onBackpressureBuffer.observeOn(schedulerToUse)
          val o2 = s2.onBackpressureBuffer.observeOn(schedulerToUse)
          val o3 = s3.onBackpressureBuffer.observeOn(schedulerToUse)
          val o4 = s4.onBackpressureBuffer.observeOn(schedulerToUse)
          val o5 = s5.onBackpressureBuffer.observeOn(schedulerToUse)
          val o6 = s6.onBackpressureBuffer.observeOn(schedulerToUse)
          val o7 = s7.onBackpressureBuffer.observeOn(schedulerToUse)
          val o8 = s8.onBackpressureBuffer.observeOn(schedulerToUse)
          val o9 = s9.onBackpressureBuffer.observeOn(schedulerToUse)
          val o10 = s10.onBackpressureBuffer.observeOn(schedulerToUse)
          val o11 = s11.onBackpressureBuffer.observeOn(schedulerToUse)
          val o12 = s12.onBackpressureBuffer.observeOn(schedulerToUse)
          val o13 = s13.onBackpressureBuffer.observeOn(schedulerToUse)
          val o14 = s14.onBackpressureBuffer.observeOn(schedulerToUse)
          val o15 = s15.onBackpressureBuffer.observeOn(schedulerToUse)
          val o16 = s16.onBackpressureBuffer.observeOn(schedulerToUse)
          val o17 = s17.onBackpressureBuffer.observeOn(schedulerToUse)
          val o18 = s18.onBackpressureBuffer.observeOn(schedulerToUse)
          val o19 = s19.onBackpressureBuffer.observeOn(schedulerToUse)
          val o20 = s20.onBackpressureBuffer.observeOn(schedulerToUse)
          val o21 = s21.onBackpressureBuffer.observeOn(schedulerToUse)
          val o22 = s22.onBackpressureBuffer.observeOn(schedulerToUse)
          val o23 = s23.onBackpressureBuffer.observeOn(schedulerToUse)
          val o24 = s24.onBackpressureBuffer.observeOn(schedulerToUse)
          val o25 = s25.onBackpressureBuffer.observeOn(schedulerToUse)
          val o26 = s26.onBackpressureBuffer.observeOn(schedulerToUse)
          val o27 = s27.onBackpressureBuffer.observeOn(schedulerToUse)
          val o28 = s28.onBackpressureBuffer.observeOn(schedulerToUse)
          val o29 = s29.onBackpressureBuffer.observeOn(schedulerToUse)
          val o30 = s30.onBackpressureBuffer.observeOn(schedulerToUse)
          val o31 = s31.onBackpressureBuffer.observeOn(schedulerToUse)
          val o32 = s32.onBackpressureBuffer.observeOn(schedulerToUse)
          val o33 = s33.onBackpressureBuffer.observeOn(schedulerToUse)

          val latch = new CountDownLatch(524288)
          val p1 = RxJoinObservable.from(o1).and(o2).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p2 = RxJoinObservable.from(o3).and(o4).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p3 = RxJoinObservable.from(o5).and(o6).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p4 = RxJoinObservable.from(o7).and(o8).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p5 = RxJoinObservable.from(o9).and(o10).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p6 = RxJoinObservable.from(o11).and(o12).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p7 = RxJoinObservable.from(o13).and(o14).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p8 = RxJoinObservable.from(o15).and(o16).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p9 = RxJoinObservable.from(o17).and(o18).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p10 = RxJoinObservable.from(o19).and(o20).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p11 = RxJoinObservable.from(o21).and(o22).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p12 = RxJoinObservable.from(o23).and(o24).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p13 = RxJoinObservable.from(o25).and(o26).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p14 = RxJoinObservable.from(o27).and(o28).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p15 = RxJoinObservable.from(o29).and(o30).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val p16 = RxJoinObservable.from(o31).and(o32).and(o33).then((x: Int, xx: Int, xxx: Int) => { 1 })
          val obs = RxJoinObservable.when(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16).toObservable
          obs.subscribe((_: Int) => latch.countDown, (_: Throwable) => (), () => ())
          val thread1 = sendIndexFromThread(s1, 32768)
          val thread2 = sendIndexFromThread(s2, 32768)
          val thread3 = sendIndexFromThread(s3, 32768)
          val thread4 = sendIndexFromThread(s4, 32768)
          val thread5 = sendIndexFromThread(s5, 32768)
          val thread6 = sendIndexFromThread(s6, 32768)
          val thread7 = sendIndexFromThread(s7, 32768)
          val thread8 = sendIndexFromThread(s8, 32768)
          val thread9 = sendIndexFromThread(s9, 32768)
          val thread10 = sendIndexFromThread(s10, 32768)
          val thread11 = sendIndexFromThread(s11, 32768)
          val thread12 = sendIndexFromThread(s12, 32768)
          val thread13 = sendIndexFromThread(s13, 32768)
          val thread14 = sendIndexFromThread(s14, 32768)
          val thread15 = sendIndexFromThread(s15, 32768)
          val thread16 = sendIndexFromThread(s16, 32768)
          val thread17 = sendIndexFromThread(s17, 32768)
          val thread18 = sendIndexFromThread(s18, 32768)
          val thread19 = sendIndexFromThread(s19, 32768)
          val thread20 = sendIndexFromThread(s20, 32768)
          val thread21 = sendIndexFromThread(s21, 32768)
          val thread22 = sendIndexFromThread(s22, 32768)
          val thread23 = sendIndexFromThread(s23, 32768)
          val thread24 = sendIndexFromThread(s24, 32768)
          val thread25 = sendIndexFromThread(s25, 32768)
          val thread26 = sendIndexFromThread(s26, 32768)
          val thread27 = sendIndexFromThread(s27, 32768)
          val thread28 = sendIndexFromThread(s28, 32768)
          val thread29 = sendIndexFromThread(s29, 32768)
          val thread30 = sendIndexFromThread(s30, 32768)
          val thread31 = sendIndexFromThread(s31, 32768)
          val thread32 = sendIndexFromThread(s32, 32768)

          val thread33 = sendIndexFromThread(s33, 524288)
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

          latch.await
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

        }
      }
    }
}
