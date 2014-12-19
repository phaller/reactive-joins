// package scala.async.tests

// import Util._
// import org.junit.Test

// import scala.async.Join._
// import rx.lang.scala.Observable

// class BackpressureTests {

//   @Test
//   def setBufferSizeTest() = {
//     val size = randomNonZeroEvenInteger(maxListSize)
//     val input = List.fill(size)(randomNonZeroEvenInteger(maxListSize))

//     val o1 = Observable.just(input: _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
//     val o2 = Observable.just(input: _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p

//     implicit val bufferSize = BufferSize(1)

//     val obs = join {
//       case o1(x) && o2(y) => Next(x + y)
//       case o1.done && o2.done => Done
//     }

//     val result = obs.toBlocking.toList
//     assert(result != input)
//     assert(result.size == input.size)
//   }

//   @Test
//   def testSubjectsAsyncBridge() = {
//     import java.util.concurrent.CountDownLatch
//     import java.util.concurrent.atomic.AtomicInteger
//     import java.util.concurrent.TimeUnit

//     import rx.lang.scala.Subject
//     import rx.lang.scala.subjects.ReplaySubject
//     import rx.lang.scala.ImplicitFunctionConversions._
//     import rx.lang.scala.JavaConversions._

//     val size = 2048

//     val s1 = Subject[Int]
//     val s2 = Subject[Int]
//     val s3 = Subject[Int]
//     val s4 = Subject[Int]
//     val s5 = Subject[Int]
//     val s6 = Subject[Int]

//     val latch = new CountDownLatch(1)
//     val counter = new AtomicInteger(0)

//     val o1 = s1.onBackpressureBuffer.observeOn(newThreadScheduler).p
//     val o2 = s2.onBackpressureBuffer.observeOn(newThreadScheduler).p
//     val o3 = s3.onBackpressureBuffer.observeOn(newThreadScheduler).p
//     val o4 = s4.onBackpressureBuffer.observeOn(newThreadScheduler).p
//     val o5 = s5.onBackpressureBuffer.observeOn(newThreadScheduler).p
//     val o6 = s6.onBackpressureBuffer.observeOn(newThreadScheduler).p

//     val obs = join {
//       case o1(x) && o2(y) && o3(z) => {
//         counter.incrementAndGet()
//         if (counter.get == (size * 2)) { latch.countDown }
//         Pass
//       }
//       case o4(x) && o5(y) && o6(z) => {
//         counter.incrementAndGet()
//         if (counter.get == (size * 2)) { latch.countDown }
//         Pass
//       }
//     }

//     obs.subscribe((_: Unit) => (),
//       (_: Throwable) => (),
//       () => ())

//     def sendIndexFromThread(s: Subject[Int], repeats: Int) = new Thread(new Runnable {
//       def run() {
//         var i = 0
//         while (i < repeats) {
//           s.onNext(i)
//           i = i + 1
//         }
//         s.onCompleted()
//       }
//     })

//     val thread1 = sendIndexFromThread(s1, size)
//     val thread2 = sendIndexFromThread(s2, size)
//     val thread3 = sendIndexFromThread(s3, size)
//     val thread4 = sendIndexFromThread(s4, size)
//     val thread5 = sendIndexFromThread(s5, size)
//     val thread6 = sendIndexFromThread(s6, size)

//     thread1.start()
//     thread2.start()
//     thread3.start()
//     thread4.start()
//     thread5.start()
//     thread6.start()

//     try {
//       latch.await(5, TimeUnit.SECONDS)
//     } finally {
//       assert(counter.get == (size * 2))
//       thread1.join()
//       thread2.join()
//       thread3.join()
//       thread4.join()
//       thread5.join()
//       thread6.join()
//     }
//     ()
//   }

// }