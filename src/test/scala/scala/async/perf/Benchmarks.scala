// package scala.async.tests

// import scala.async.Join._
// import rx.lang.scala._
// import rx.observables.{JoinObservable => RxJoinObservable}
// import scala.async.tests.Util._

// import org.scalameter.api._

// class RxReactBench extends PerformanceTest.OfflineReport {
  
//   val sumSizes = Gen.range("size")(100000, 100000, 100000)

//   performance of "zipMap" config(
//     exec.minWarmupRuns -> 50,
//     exec.maxWarmupRuns -> 100,
//     exec.benchRuns -> 30,
//     exec.independentSamples -> 1
//   ) in {

//     using(sumSizes) curve("AsyncJoins") in { size =>
//       // val s1 = Subject[Int]
//       // val s2 = Subject[Int]
//       // val s3 = Subject[Int]
//       // val s4 = Subject[Int]
//       // val s5 = Subject[Int]
//       // val s6 = Subject[Int]
 
//       // val o1 = s1.observeOn(newThreadScheduler).p
//       // val o2 = s2.observeOn(newThreadScheduler).p
//       // val o3 = s3.observeOn(newThreadScheduler).p
//       // val o4 = s4.observeOn(newThreadScheduler).p
//       // val o5 = s5.observeOn(newThreadScheduler).p
//       // val o6 = s6.observeOn(newThreadScheduler).p

//       val o1 = Observable.just(1).repeat(size).observeOn(newThreadScheduler).p
//       val o2 = Observable.just(2).repeat(size).observeOn(newThreadScheduler).p
//       val o3 = Observable.just(3).repeat(size).observeOn(newThreadScheduler).p
//       val o4 = Observable.just(4).repeat(size).observeOn(newThreadScheduler).p
//       val o5 = Observable.just(5).repeat(size).observeOn(newThreadScheduler).p
//       val o6 = Observable.just(6).repeat(size).observeOn(newThreadScheduler).p


//       val obs = join {
//         case o1(x) && o2(y) => Next(1)
//         case o1(x) && o3(y) => Next(2)
//         case o1(x) && o4(y) => Next(3)
//         case o1(x) && o5(y) => Next(4)
//         case o1(x) && o6(y) => Next(5)
//         case o2(x) && o3(y) => Next(6)
//         case o2(x) && o4(y) => Next(7)
//         case o2(x) && o5(y) => Next(8)
//         case o2(x) && o6(y) => Next(9)
//         case o3(x) && o4(y) => Next(10)
//         case o3(x) && o5(y) => Next(11)
//         case o3(x) && o6(y) => Next(12)
//         case o4(x) && o5(y) => Next(13)
//         case o4(x) && o6(y) => Next(14)
//         case o5(x) && o6(y) => Next(15)
//         case o1.done && o2.done && o3.done && o4.done && o5.done && o6.done => Done
//       }

//       obs.toBlocking.toList

//     }

//     using(sumSizes) curve("RxJoins") in { size =>
//       import rx.subjects._
//       import rx.lang.scala.ImplicitFunctionConversions._

//       // val s1 = PublishSubject.create[Int]()
//       // val s2 = PublishSubject.create[Int]()
//       // val s3 = PublishSubject.create[Int]()
//       // val s4 = PublishSubject.create[Int]()
//       // val s5 = PublishSubject.create[Int]()
//       // val s6 = PublishSubject.create[Int]()

//       // val o1 = s1.observeOn(rx.schedulers.Schedulers.newThread())
//       // val o2 = s2.observeOn(rx.schedulers.Schedulers.newThread())
//       // val o3 = s3.observeOn(rx.schedulers.Schedulers.newThread())
//       // val o4 = s4.observeOn(rx.schedulers.Schedulers.newThread())
//       // val o5 = s5.observeOn(rx.schedulers.Schedulers.newThread())
//       // val o6 = s6.observeOn(rx.schedulers.Schedulers.newThread())

//       // val o1 = Observable.just(1).repeat(size).observeOn(newThreadScheduler).asInstanceOf[].p
//       // val o2 = Observable.just(2).repeat(size).observeOn(newThreadScheduler).p
//       // val o3 = Observable.just(3).repeat(size).observeOn(newThreadScheduler).p
//       // val o4 = Observable.just(4).repeat(size).observeOn(newThreadScheduler).p
//       // val o5 = Observable.just(5).repeat(size).observeOn(newThreadScheduler).p
//       // val o6 = Observable.just(6).repeat(size).observeOn(newThreadScheduler).p

//       // val p1 = RxJoinObservable.from(o1).and(o2).then((x: Int, y: Int) => 1)
//       // val p2 = RxJoinObservable.from(o1).and(o3).then((x: Int, y: Int) => 2)
//       // val p3 = RxJoinObservable.from(o1).and(o4).then((x: Int, y: Int) => 3)
//       // val p4 = RxJoinObservable.from(o1).and(o5).then((x: Int, y: Int) => 4)
//       // val p5 = RxJoinObservable.from(o1).and(o6).then((x: Int, y: Int) => 5)
//       // val p6 = RxJoinObservable.from(o2).and(o3).then((x: Int, y: Int) => 6)
//       // val p7 = RxJoinObservable.from(o2).and(o4).then((x: Int, y: Int) => 7)
//       // val p8 = RxJoinObservable.from(o2).and(o5).then((x: Int, y: Int) => 8)
//       // val p9 = RxJoinObservable.from(o2).and(o6).then((x: Int, y: Int) => 9)
//       // val p10 = RxJoinObservable.from(o3).and(o4).then((x: Int, y: Int) => 10)
//       // val p11 = RxJoinObservable.from(o3).and(o5).then((x: Int, y: Int) => 11)
//       // val p12 = RxJoinObservable.from(o3).and(o6).then((x: Int, y: Int) => 12)
//       // val p13 = RxJoinObservable.from(o4).and(o5).then((x: Int, y: Int) => 13)
//       // val p14 = RxJoinObservable.from(o4).and(o6).then((x: Int, y: Int) => 14)
//       // val p15 = RxJoinObservable.from(o5).and(o6).then((x: Int, y: Int) => 15)

//       // val result = RxJoinObservable.when(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p12, p14, p15).toObservable
//       // result.toBlocking.last

//       // var i = 0
//       // while (i < size) {
//       //   s1.onNext(i)
//       //   s2.onNext(i)
//       //   s3.onNext(i)
//       //   s4.onNext(i)
//       //   s5.onNext(i)
//       //   s6.onNext(i)       
//       //   i += 1
//       // }
//     }
//   }

//   // performance of "MergezipMap" config(
//   //   exec.minWarmupRuns -> 50,
//   //   exec.maxWarmupRuns -> 100,
//   //   exec.benchRuns -> 30,
//   //   exec.independentSamples -> 1
//   // ) in {

//   //   // using(sumSizes) curve("AsyncJoins") in { size =>
//   //   //   val s1 = Subject[Int]
//   //   //   val s2 = Subject[Int]
//   //   //   val s3 = Subject[Int]

//   //   //   val o1 = s1.observeOn(newThreadScheduler).p
//   //   //   val o2 = s2.observeOn(newThreadScheduler).p
//   //   //   val o3 = s2.observeOn(newThreadScheduler).p

//   //   //   val obs = join {
//   //   //     case o1(x) && o2(y) => Next(x + y)
//   //   //     case o1(x) && o3(y) => Next(x + y)
//   //   //     case o1.done && o2.done => Done
//   //   //   }
//   //   //   var i = 0
//   //   //   while (i < size) {
//   //   //     s1.onNext(i)
//   //   //     s2.onNext(i)
//   //   //     s3.onNext(i)
//   //   //     i += 1
//   //   //   }
//   //   // }

//   //   // using(sumSizes) curve("RxJoins") in { size =>
//   //   //   import rx.subjects._
//   //   //   import rx.lang.scala.ImplicitFunctionConversions._

//   //   //   val s1 = PublishSubject.create[Int]()
//   //   //   val s2 = PublishSubject.create[Int]()
//   //   //   val s3 = PublishSubject.create[Int]()

//   //   //   val first = RxJoinObservable.from(s1.observeOn(rx.schedulers.Schedulers.newThread())).and(s2.observeOn(rx.schedulers.Schedulers.newThread())).then((x: Int, y: Int) => (x + y))
//   //   //   val second = RxJoinObservable.from(s1.observeOn(rx.schedulers.Schedulers.newThread())).and(s3.observeOn(rx.schedulers.Schedulers.newThread())).then((x: Int, y: Int) => (x + y))
//   //   //   val result = RxJoinObservable.when(first, second).toObservable

//   //   //   var i = 0
//   //   //   while (i < size) {
//   //   //     s1.onNext(i)
//   //   //     s2.onNext(i)
//   //   //     s3.onNext(i)
//   //   //     i += 1
//   //   //   }
//   //   // }
//   // }
// }