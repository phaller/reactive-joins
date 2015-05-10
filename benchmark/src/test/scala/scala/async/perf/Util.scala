package scala.async.tests

object Util {
  import rx.lang.scala.schedulers._
  import scala.util.Random
  import rx.lang.scala.Subject

  val random = new Random

  def randomNonZeroEvenInteger(max: Int) = 2 * (random.nextInt(max / 2) + 1)

  val newThreadScheduler = NewThreadScheduler()
  // defines which scheduler to use in the benchmarks
  def schedulerToUse = newThreadScheduler

  val maxListSize = 10000

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

  // Uncomment this to run the tests with the deterministic lock-transform. 
  // The default transform is the non-deterministic lock-free transform.
  // implicit val checkOrder = scala.async.Join.InOrder
}