package scala.async.tests

object Util {
  import rx.lang.scala.schedulers._
  import scala.util.Random

  val random = new Random

  def randomNonZeroEvenInteger(max: Int) = 2 * (random.nextInt(max / 2) + 1)

  val newThreadScheduler = NewThreadScheduler()
  // defines which scheduler to use in the benchmarks
  def schedulerToUse = newThreadScheduler

  val maxListSize = 10000

  // Uncomment this to run the tests with the deterministic lock-transform. 
  // The default transform is the non-deterministic lock-free transform.
  // implicit val checkOrder = scala.async.Join.InOrder
}