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
  def binaryAndJoinLatched() = {
    import java.util.concurrent.TimeUnit
    import java.util.concurrent.CountDownLatch

    val size = randomNonZeroEvenInteger(maxListSize)

    val o1 = Observable.just(1).repeat(size).subscribeOn(newThreadScheduler).onBackpressureBuffer.observeOn(newThreadScheduler).p
    val o2 = Observable.just(2).repeat(size).subscribeOn(newThreadScheduler).onBackpressureBuffer.observeOn(newThreadScheduler).p

    val latch = new CountDownLatch(size)
    val obs = join {
      case o1(x) && o2(y) => Next(())
      case o1.done && o2.done => Done
    }
    obs.subscribe((_: Unit) => { latch.countDown() }, (e: Throwable) => assert(false), () => ())

    try {
      assert(latch.await(5, TimeUnit.SECONDS))
      // assert(result.size == size)
    } catch {
      case _: Throwable => assert(false)
    }
    ()
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
  def binaryAndOrJoinFive() = {
    val full = randomNonZeroEvenInteger(maxListSize)
    val half = full / 2

    val o1 = Observable.just(List.fill(full)(1): _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
    val o2 = Observable.just(List.fill(half)(2): _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
    val o3 = Observable.just(List.fill(full)(3): _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
    val o4 = Observable.just(List.fill(half)(4): _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
    val o5 = Observable.just(List.fill(half)(5): _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p

    val obs = join {
      case o1(x) && o2(y) && o3(z) => Next(true)
      case o4(x) && o5(y) && o3(z) => Next(false)
      case o1.done && o2.done && o3.done && o4.done && o5.done => Done
    }

    val result = obs.toBlocking.toList
    assert(result.filter(identity).size == half)
    assert(result.filter(x => !x).size == half)
  }

  // This test was written because I could not figure out why it would not run in the benchmarks.
  // The reason was that we only support up to 64 observables as we use longs and bit-shifting to
  // give they observables ids. Longs have 64 bits
  @Test
  def deterministicBenchmark {
    import rx.lang.scala.Subject
    import java.util.concurrent.CountDownLatch
    import java.util.concurrent.TimeUnit
    implicit val checkOrder = scala.async.Join.InOrder

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
    // val s64 = Subject[Int]
    val s65 = Subject[Int]

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
    val o34 = s34.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o35 = s35.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o36 = s36.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o37 = s37.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o38 = s38.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o39 = s39.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o40 = s40.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o41 = s41.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o42 = s42.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o43 = s43.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o44 = s44.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o45 = s45.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o46 = s46.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o47 = s47.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o48 = s48.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o49 = s49.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o50 = s50.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o51 = s51.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o52 = s52.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o53 = s53.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o54 = s54.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o55 = s55.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o56 = s56.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o57 = s57.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o58 = s58.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o59 = s59.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o60 = s60.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o61 = s61.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o62 = s62.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o63 = s63.onBackpressureBuffer.observeOn(schedulerToUse).p
    // val o64 = s64.onBackpressureBuffer.observeOn(schedulerToUse).p
    val o65 = s65.onBackpressureBuffer.observeOn(schedulerToUse).p

    val latch = new CountDownLatch(524288)
    // val latch = new CountDownLatch(507904)

    val obs = join {
      case o1(x) && o2(xx) && o65(xxx) => { Next(1) }
      case o3(x) && o4(xx) && o65(xxx) => { Next(1) }
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
      case o33(x) && o34(xx) => { Next(1) }
      case o35(x) && o36(xx) => { Next(1) }
      case o37(x) && o38(xx) => { Next(1) }
      case o39(x) && o40(xx) => { Next(1) }
      case o41(x) && o42(xx) => { Next(1) }
      case o43(x) && o44(xx) => { Next(1) }
      case o45(x) && o46(xx) => { Next(1) }
      case o47(x) && o48(xx) => { Next(1) }
      case o49(x) && o50(xx) => { Next(1) }
      case o51(x) && o52(xx) => { Next(1) }
      case o53(x) && o54(xx) => { Next(1) }
      case o55(x) && o56(xx) => { Next(1) }
      case o57(x) && o58(xx) => { Next(1) }
      case o59(x) && o60(xx) => { Next(1) }
      case o61(x) && o62(xx) => { Next(1) }
      case o63(x)  => { Next(1) }
    }.serialize
    obs.subscribe((_: Int) => latch.countDown, (_: Throwable) => (), () => ())
    val thread1 = sendIndexFromThread(s1, 16384)
    val thread2 = sendIndexFromThread(s2, 16384)
    val thread3 = sendIndexFromThread(s3, 16384)
    val thread4 = sendIndexFromThread(s4, 16384)
    val thread5 = sendIndexFromThread(s5, 16384)
    val thread6 = sendIndexFromThread(s6, 16384)
    val thread7 = sendIndexFromThread(s7, 16384)
    val thread8 = sendIndexFromThread(s8, 16384)
    val thread9 = sendIndexFromThread(s9, 16384)
    val thread10 = sendIndexFromThread(s10, 16384)
    val thread11 = sendIndexFromThread(s11, 16384)
    val thread12 = sendIndexFromThread(s12, 16384)
    val thread13 = sendIndexFromThread(s13, 16384)
    val thread14 = sendIndexFromThread(s14, 16384)
    val thread15 = sendIndexFromThread(s15, 16384)
    val thread16 = sendIndexFromThread(s16, 16384)
    val thread17 = sendIndexFromThread(s17, 16384)
    val thread18 = sendIndexFromThread(s18, 16384)
    val thread19 = sendIndexFromThread(s19, 16384)
    val thread20 = sendIndexFromThread(s20, 16384)
    val thread21 = sendIndexFromThread(s21, 16384)
    val thread22 = sendIndexFromThread(s22, 16384)
    val thread23 = sendIndexFromThread(s23, 16384)
    val thread24 = sendIndexFromThread(s24, 16384)
    val thread25 = sendIndexFromThread(s25, 16384)
    val thread26 = sendIndexFromThread(s26, 16384)
    val thread27 = sendIndexFromThread(s27, 16384)
    val thread28 = sendIndexFromThread(s28, 16384)
    val thread29 = sendIndexFromThread(s29, 16384)
    val thread30 = sendIndexFromThread(s30, 16384)
    val thread31 = sendIndexFromThread(s31, 16384)
    val thread32 = sendIndexFromThread(s32, 16384)
    val thread33 = sendIndexFromThread(s33, 16384)
    val thread34 = sendIndexFromThread(s34, 16384)
    val thread35 = sendIndexFromThread(s35, 16384)
    val thread36 = sendIndexFromThread(s36, 16384)
    val thread37 = sendIndexFromThread(s37, 16384)
    val thread38 = sendIndexFromThread(s38, 16384)
    val thread39 = sendIndexFromThread(s39, 16384)
    val thread40 = sendIndexFromThread(s40, 16384)
    val thread41 = sendIndexFromThread(s41, 16384)
    val thread42 = sendIndexFromThread(s42, 16384)
    val thread43 = sendIndexFromThread(s43, 16384)
    val thread44 = sendIndexFromThread(s44, 16384)
    val thread45 = sendIndexFromThread(s45, 16384)
    val thread46 = sendIndexFromThread(s46, 16384)
    val thread47 = sendIndexFromThread(s47, 16384)
    val thread48 = sendIndexFromThread(s48, 16384)
    val thread49 = sendIndexFromThread(s49, 16384)
    val thread50 = sendIndexFromThread(s50, 16384)
    val thread51 = sendIndexFromThread(s51, 16384)
    val thread52 = sendIndexFromThread(s52, 16384)
    val thread53 = sendIndexFromThread(s53, 16384)
    val thread54 = sendIndexFromThread(s54, 16384)
    val thread55 = sendIndexFromThread(s55, 16384)
    val thread56 = sendIndexFromThread(s56, 16384)
    val thread57 = sendIndexFromThread(s57, 16384)
    val thread58 = sendIndexFromThread(s58, 16384)
    val thread59 = sendIndexFromThread(s59, 16384)
    val thread60 = sendIndexFromThread(s60, 16384)
    val thread61 = sendIndexFromThread(s61, 16384)
    val thread62 = sendIndexFromThread(s62, 16384)
    val thread63 = sendIndexFromThread(s63, 16384)
    // val thread64 = sendIndexFromThread(s64, 16384)

    val thread65 = sendIndexFromThread(s65, 32768)

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
    // thread64.start()
    thread65.start()

    try {
      assert(latch.await(5, TimeUnit.SECONDS))
    } finally {
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
      // thread64.join()
      thread65.join()
    }
  }

  // TODO: Figure out how to test the mixed-pattern semantics
  // @Test
  // def doneSemantics {
  //   val input1 = List.fill(randomNonZeroEvenInteger(maxListSize))(1)
  //   val input2 = List.fill(randomNonZeroEvenInteger(maxListSize))(2)

  //   val o1 = Observable.just(input1: _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
  //   val o2 = Observable.just(input2: _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p

  //   val obs = join {
  //     case o1(x) => Next(x)
  //     case o2(x) && o1.done => Next(x)
  //     case o2.done => Done
  //   }

  //   val result = obs.toBlocking.toList 
  //   assert(result == (input1 ++ input2))
  // }

  // 
  // TODO: Figure out why this does not work anymore after moving to Observable.create?
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
  //     case o1(x) && o2(y) => Next(1)
  //     case o1(x) && o3(y) => Next(2)
  //     case o1.done && o2.done && o3.done => Done
  //   }

  //   obs.subscribe(println(_))

  //   s2.onNext(2, 1)
  //   s3.onNext(3, 1)
  //   s1.onNext(1, 2)
  //   s1.onNext(1, 2)
  //   s1.onCompleted(3)
  //   s2.onCompleted(3)
  //   s3.onCompleted(3)

  //   testScheduler.triggerActions()

  //   assert(obs.toBlocking.toList == List(1, 2))
  // }
}