import rx.lang.scala.{Subject, Observable, Subscription}

import concurrent.duration._
import scala.collection.mutable
import scala.concurrent.{Awaitable, Await, Lock}

/* Example source of transform:
* async {
*   val result = join {
*     case obs1(x) & obs2(y) => x + y
*     case obs1(x) & obs3(y) => x - y
*     case obs4(4) => println ("got 4!"); 4
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

  val obs1_id = 1 << 0
  val obs2_id = 1 << 1
  val obs3_id = 1 << 2
  val obs4_id = 1 << 3

  // TODO: The Any type should correspond to the Observable type
  var channels = Map[Int, mutable.Queue[Any]]()

  // TODO: This can also be calculated at compile time
  val pattern1 = obs1_id | obs2_id
  val pattern2 = obs1_id | obs3_id
  val pattern3 = obs4_id

  val pattern1_continuation = (v1: Long, v2: Long) => v1 + v2
  val pattern2_continuation = (v1: Long, v2: Long) => v1 - v2

  val pattern3_continuation = () => { 4 }

  val pattern3_condition = (v1: Long) => v1 == 4

  val continuation: (Long => Unit) = result => { println(s"Result: $result") }

  var state = BitField.empty
  val stateLock = new Lock()

  def stateOfQueues(channels: Map[Int, mutable.Queue[_]]): BitField = {
    new BitField(channels.foldLeft(0) { case (acc, (id, queue)) => if (!queue.isEmpty) acc | id else acc})
  }

  def dequeue(obs_id: Int): Long = channels.get(obs_id) match {
    case Some(q) => q.dequeue().asInstanceOf[Long]
  }

  obs1.subscribe(
    next => {
      stateLock.acquire()
      // TODO: state.set(obs1_id) =
      val possibleState = state.set(obs1_id)
      // Check all patterns for a match

      // Obs 1 | Obs 2
      if (possibleState.matches(pattern1)) {
        // Dequeue the required messages
        val obs2_value = dequeue(obs2_id)
        // Adjust the state to the queue contents
        state = stateOfQueues(channels)
        val result = pattern1_continuation(next, obs2_value)
        stateLock.release()
        continuation(result)
      // Obs1 | Obs 3
      } else if (possibleState.matches(pattern2)) {
        val obs3_value = dequeue(obs3_id)
        state = stateOfQueues(channels)
        stateLock.release()
        val result = pattern2_continuation(next, obs3_value)
        continuation(result)
      } else {
        // TODO: This can be further inlined by chosing the correct queue
        channels.get(obs1_id) match {
          case Some(q) => q.enqueue(next)
          case None => channels += (obs1_id -> mutable.Queue(next))
        }
        state = possibleState
        stateLock.release()
      }
    }
  )

  obs2.subscribe(
    next => {
      stateLock.acquire()
      val possibleState = state.set(obs2_id)
      // Check all patterns for a match
      // Obs 1 | Obs 2
      if (possibleState.matches(pattern1)) {
        // Dequeue the required messages
        val obs1_value = dequeue(obs1_id)
        // Adjust the state to the queue contents
        state = stateOfQueues(channels)
        val result = pattern1_continuation(obs1_value, next)
        stateLock.release()
        continuation(result)
      } else {
        channels.get(obs2_id) match {
          case Some(q) => q.enqueue(next)
          case None => channels += (obs2_id -> mutable.Queue(next))
        }
        state = possibleState
        stateLock.release()
      }
    }
  )

  obs3.subscribe(
    next => {
      stateLock.acquire()
      val possibleState = state.set(obs3_id)
      // Check all patterns for a match

      // Obs 1 | Obs 3
      if (possibleState.matches(pattern2)) {
        // Dequeue the required messages
        val obs1_value = dequeue(obs1_id)
        // Adjust the state to the queue contents
        state = stateOfQueues(channels)
        val result = pattern1_continuation(obs1_value, next)
        stateLock.release()
        continuation(result)
      } else {
        channels.get(obs3_id) match {
          case Some(q) => q.enqueue(next)
          case None => channels += (obs3_id -> mutable.Queue(next))
        }
        state = possibleState
        stateLock.release()
      }
    }
  )

  obs4.subscribe(
    next => {
      stateLock.acquire()
      val possibleState = state.set(obs4_id)
      // case Obs4(4)
      if (possibleState.matches(pattern3)) {
        if (pattern3_condition(next)) {
          val result = pattern3_continuation()
          stateLock.release()
          continuation(result)
        } else {
          stateLock.release()
        }
      } else {
        channels.get(obs4_id) match {
          case Some(q) => q.enqueue(next)
          case None => channels += (obs4_id -> mutable.Queue(next))
        }
        state = possibleState
        stateLock.release()
      }
    }
  )

  def printState() {
    println(s"State: $state\n Queues: ${channels.map({ case (k, v) => v.toString()}).mkString(" ")}")
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
}


// TODO: When, and how to unsubscribe?