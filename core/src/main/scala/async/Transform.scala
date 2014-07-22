import rx.lang.scala.{ Subject, Observable, Subscription }

import concurrent.duration._
import scala.collection.mutable
import scala.concurrent.{ Awaitable, Await, Lock }
import scala.util.control.Breaks._

/* Example source of transform:
* async {
*   val result = join {
*     case obs1(x) & obs2(y) => x + y
*     case obs1(x) & obs3(y) => x - y
*     case obs1(x) & obs4.error(e) => println ("error")
*     case obs1(x) & obs4.done => println("done")
*   }
*   println(result)
* }
* */

// TODO: Set-up macro debug environment

// The BitField manipulations can be embedded

class BitField(val value: Int) {
  def set(mask: Int) = new BitField(value | mask)
  def clear(mask: Int) = new BitField(value & ~mask)
  def matches(mask: Int): Boolean = (~value & mask) == 0
  override def toString = value.toString
}
object BitField {
  def empty = new BitField(0)
}

object Transform extends App {

  val obs1 = Subject[Long]()
  val obs2 = Subject[Long]()
  val obs3 = Subject[Long]()
  val obs4 = Subject[Long]()

  val obs1_next_id = 1 << 0
  val obs1_error_id = 1 << 1
  val obs1_done_id = 1 << 2

  val obs2_next_id = 1 << 3
  val obs2_error_id = 1 << 4
  val obs2_done_id = 1 << 5

  val obs3_next_id = 1 << 6
  val obs3_error_id = 1 << 7
  val obs3_done_id = 1 << 8

  val obs4_next_id = 1 << 9
  val obs4_error_id = 1 << 10
  val obs4_done_id = 1 << 11

  // TODO: The Any type should correspond to the Observable type
  var channels = Map[Int, mutable.Queue[Any]]()

  // TODO: This can also be calculated at compile time
  val pattern1 = obs1_next_id | obs2_next_id
  val pattern2 = obs1_next_id | obs3_next_id
  val pattern3 = obs4_error_id | obs1_next_id
  val pattern4 = obs1_next_id | obs4_done_id

  val pattern1_continuation = (v1: Long, v2: Long) => v1 + v2
  val pattern2_continuation = (v1: Long, v2: Long) => v1 - v2
  val pattern3_continuation = (v1: Long, e: Throwable) => { println(e.toString); v1 }
  val pattern4_continuation = (v1: Long) => { println("Done on Obs4"); v1 }

  val pattern3_condition = (v1: Long) => v1 == 4

  val continuation: (Long => Unit) = result => { println(s"Result: $result") }

  var state = BitField.empty
  val stateLock = new Lock()

  def stateOfQueues(channels: Map[Int, mutable.Queue[_]]): BitField = {
    new BitField(channels.foldLeft(0) { case (acc, (id, queue)) => if (!queue.isEmpty) acc | id else acc })
  }

  def dequeue(obs_id: Int): Long = channels.get(obs_id) match {
    case Some(q) => q.dequeue().asInstanceOf[Long]
  }

  obs1.subscribe(
    next => {
      stateLock.acquire()
      // TODO: state.set(obs1_next_id) =
      val possibleState = state.set(obs1_next_id)
      // Check all patterns for a match

      // Obs 1 | Obs 2
      breakable {
        if (possibleState.matches(pattern1)) {
          // Dequeue the required messages
          val obs2_value = dequeue(obs2_next_id)
          // Adjust the state to the queue contents
          state = stateOfQueues(channels)
          val result = pattern1_continuation(next, obs2_value)
          stateLock.release()
          continuation(result)
          break
        }
        // Obs1 | Obs 3
        if (possibleState.matches(pattern2)) {
          val obs3_value = dequeue(obs3_next_id)
          state = stateOfQueues(channels)
          stateLock.release()
          val result = pattern2_continuation(next, obs3_value)
          continuation(result)
          break
        }
        
        if (possibleState.matches(pattern3)) {
          state = stateOfQueues(channels)
          // TODO: how to adjust the state since error/sdone are only thrown once?
          // just reset it? how will this all play out in the async/await library?
          // maybe we need to keep it, since the error is one of the "end"-states?
          state = state.clear(obs4_error_id)
          stateLock.release
          val result = pattern3_continuation(next, obs4_error_buffer)
          continuation(result)
          break
        }

        if (possibleState.matches(pattern4)) {
          state = stateOfQueues(channels)
          // TODO: do we need to reset the state here?
          // sate = state.clear(obs4_done_id)
          stateLock.release
          val result = pattern4_continuation(next)
        }

        // TODO: This can be further inlined by chosing the correct queue
        channels.get(obs1_next_id) match {
          case Some(q) => q.enqueue(next)
          case None => channels += (obs1_next_id -> mutable.Queue(next))
        }
        state = possibleState
        stateLock.release()
      }
    })

  obs2.subscribe(
    next => {
      stateLock.acquire()
      val possibleState = state.set(obs2_next_id)
      // Check all patterns for a match
      // Obs 1 | Obs 2
      breakable {
        if (possibleState.matches(pattern1)) {
          // Dequeue the required messages
          val obs1_value = dequeue(obs1_next_id)
          // Adjust the state to the queue contents
          state = stateOfQueues(channels)
          val result = pattern1_continuation(obs1_value, next)
          stateLock.release()
          continuation(result)
          break
        }
        channels.get(obs2_next_id) match {
          case Some(q) => q.enqueue(next)
          case None => channels += (obs2_next_id -> mutable.Queue(next))
        }
        state = possibleState
        stateLock.release()
      }
    })

  obs3.subscribe(
    next => {
      stateLock.acquire()
      val possibleState = state.set(obs3_next_id)
      // Check all patterns for a match

      // Obs 1 | Obs 3
      breakable {
        if (possibleState.matches(pattern2)) {
          // Dequeue the required messages
          val obs1_value = dequeue(obs1_next_id)
          // Adjust the state to the queue contents
          state = stateOfQueues(channels)
          val result = pattern1_continuation(obs1_value, next)
          stateLock.release()
          continuation(result)
          break
        }
        channels.get(obs3_next_id) match {
          case Some(q) => q.enqueue(next)
          case None =>
            channels += (obs3_next_id -> mutable.Queue(next))
        }
        state = possibleState
        stateLock.release()
      }
    })

 // if (pattern3_condition(next)) {
 //            val result = pattern3_continuation()
 //            stateLock.release()
 //            continuation(result)
 //          } else {
 //            stateLock.release()
 //          }

  var obs4_error_buffer: Throwable = null

  obs4.subscribe(
    next => {},
    err => {
      stateLock.acquire()
      val possibleState = state.set(obs4_error_id)
      // case Obs1(x) & Obs4.error(x)
      breakable {
        if (possibleState.matches(pattern3)) {
          val obs1_value = dequeue(obs1_next_id)
          val result = pattern3_continuation(obs1_value, err)
          // TODO: how to set the state?
          stateLock.release
          continuation(result)
          break
        }
        obs4_error_buffer = err
        state = possibleState
        stateLock.release()
      }
    },
    () => {
      stateLock.acquire()
      val possibleState = state.set(obs4_done_id)
      breakable {
        if (possibleState.matches(pattern4)) {
          val obs1_value = dequeue(obs1_next_id)
          val result = pattern4_continuation(obs1_value)
          // TODO: how to set the state?
          stateLock.release
          continuation(result)
          break
        }
        state = possibleState // Note: this sets the "done" flag to true 
        stateLock.release()
      }
  })

  def printState() {
    println(s"State: $state\n Queues: ${channels.map({ case (k, v) => v.toString() }).mkString(" ")}")
  }

  printState()
  obs1.onNext(1L)
  printState()
  obs1.onNext(1L)
  printState()
  obs2.onNext(3L)
  printState()
  obs2.onNext(3L)
  printState()
  obs2.onNext(3L)
  printState()
  obs3.onNext(4L)
  printState()
  obs1.onNext(4L)
  printState()
  obs4.onNext(4L)
  printState()
  obs4.onNext(2L)
  printState()
  obs4.onError(new Exception("Fuuuuuuuuuu"))
}

// TODO: When, and how to unsubscribe?