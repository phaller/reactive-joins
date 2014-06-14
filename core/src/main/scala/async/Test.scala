package scala.async

import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global
import scala.async.Join._
import rx.Observable

object Test extends App {

  val f = Observable.just(42)
  val g = Observable.just(43)
  val h = Observable.just(44)
  val k = Observable.just(45)

  /* Create pattern objects from observables using the "p" method */
  val (o1, o2, o3, o4) = (f.p, g.p, h.p, k.p)

  /* Coordinate observables with a join pattern! */
  val obs = join[Int] {
    case o1(x) && o2(y) && o3(z) => x + 1 //+ y + z
    case o1(y) && o2(z) && o4(x) => x
  }
  println(obs)
}
