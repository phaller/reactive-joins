// package scala.async

import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import scala.async.Join._
import rx.Observable

object Test extends App {

  // val f = Observable.never[Int]()
  val f = Observable.just(20)
  val g = Observable.just("")
  val h = Observable.just(44)
  val k = Observable.just(45)

  /* Create pattern objects from observables using the "p" method */

  // TODO: Consider Pattern { ... } instead
  val (o1, o2, o3, o4) = (f.p, g.p, h.p, k.p)

  /* Coordinate observables with a join pattern! */

  val obs = join {
    case o1(x) && o3(y) && o4(z) => x + y
  }
  println(obs)
  scala.io.StdIn.readLine()
}