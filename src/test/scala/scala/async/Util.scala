package scala.async.tests

object Util {
  import rx.lang.scala.schedulers.NewThreadScheduler
  import scala.util.Random

  val random = new Random

  def randomNonZeroEvenInteger(max: Int) = 2 * (random.nextInt(max / 2) + 1)

  val newThreadScheduler = NewThreadScheduler()

  val maxListSize = 50 
}