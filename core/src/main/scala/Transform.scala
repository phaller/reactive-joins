import rx.lang.scala.Observable
import rx.lang.scala.Subscription

import concurrent.duration._
import scala.collection.immutable.Queue
import scala.concurrent.Future

/* Example source of transform:
* async {
*   val result = join {
*     case obs1(x) & obs2(y) => x + y
*     case obs3(x) => x + 1
*   }
*   println(result)
* }
* */

class BitField(val value: Int) {
  def set(mask: Int) = new BitField(value | mask)
  def clear(mask: Int) = new BitField(value & ~mask)
  def matches(mask: Int): Boolean = (~value & mask) == 0
  def isSet(mask: Int): Boolean = (value & mask) == mask
}
object BitField {
  def empty = new BitField(0)
}

object Transform extends App {

  val obs1 = Observable.interval(200 millis)
  val obs2 = Observable.interval(400 millis)
  val obs3 = Observable.interval(100 millis)

  val obs1_id = 1 << 0
  val obs2_id = 1 << 1
  val obs3_id = 1 << 2

  // TODO: The Any type should correspond to the Observable type
  var channels = Map[Int, Queue[Any]]()

  val pattern1 = obs1_id | obs2_id
  val pattern2 = obs3_id

  val pattern1_continuation = (v1: Long, v2: Long) => v1 + v2
  val pattern2_continuation = (v1: Long) => v1

  val continuation: (Any => Unit) = ???

  var state = BitField.empty

  obs1.subscribe(
    next => {
      state.synchronized {
        if (state.set(obs1_id).matches(pattern1)) {
          val obs2_value = channels.get(obs2_id) match {
            case Some(x: Long) => x
          }
          Future {
            val result = pattern1_continuation(next, obs2_value)
            continuation(result)
          }
        } else {
          channels.get(obs1_id) match {
            case Some(q) => q.enqueue(next)
            case None => channels += (obs1_id -> Queue(next))
          }
          state.set(obs1_id)
        }
      }
    }
  )

  obs2.subscribe(
    next => {
      state.synchronized {
        if (state.set(obs2_id).matches(pattern1)) {
          val obs1_value = channels.get(obs1_id) match {
            case Some(x: Long) => x
          }
          Future {
            val result = pattern1_continuation(obs1_value, next)
            continuation(result)
          }
        } else {
          channels.get(obs2_id) match {
            case Some(q) => q.enqueue(next)
            case None => channels += (obs2_id -> Queue(next))
          }
          state.set(obs2_id)
        }
      }
    }
  )

  obs3.subscribe(
    next => {
      state.synchronized {
        if (state.set(obs3_id).matches(pattern2)) {
          Future {
            val result = pattern2_continuation(next)
            continuation(result)
          }
        }
      }
    }
  )


}