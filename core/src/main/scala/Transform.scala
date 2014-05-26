import rx.lang.scala.Observable
import rx.lang.scala.Subscription

import concurrent.duration._
import scala.collection.mutable
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
object Transform extends App {

  val obs1 = Observable.interval(200 millis)
  val obs2 = Observable.interval(400 millis)
  val obs3 = Observable.interval(100 millis)

  val observables = List(obs1, obs2, obs3)
  val subscriptions = observables.map { obs =>
    obs.subscribe(msg => resolveMessage(obs, msg))
  }

  val channels = new mutable.AnyRefMap[Observable[Any], mutable.Queue[Any]]()

  var ids = new mutable.AnyRefMap[Observable[Any], Int]()
  observables.zipWithIndex.foreach {
    case (obs, index) => ids.put(obs, 1 << index)
  }

  def getId[A](obs: Observable[A]): Int = ids.getOrElse(obs, throw Exception)

  class BitField(val value: Int) {
    def set(mask: Int) = new BitField(value | mask)
    def clear(mask: Int) = new BitField(value & ~mask)
    def matches(mask: Int): Boolean = (~value & mask) == 0
    def isSet(mask: Int): Boolean = (value & mask) == mask
  }
  object BitField {
    def empty = new BitField(0)
  }

  class Pattern[A, B](val target: Int, val body: (A => B)) extends BitField(target)
  object Pattern {
    def apply[A, B](target: Int, body: (A => B)) = new Pattern(target, body)
  }

  val patterns = List(
    Pattern(getId(obs1) | getId(obs2), { case (x, y) => x + y }),
    Pattern(getId(obs3), { case (x) => x })
  )

  val continuation: (Any => Unit) = ???

  var state = BitField.empty

  def resolveMessage[A](sender: Observable[A], msg: A) = {
    patterns.synchronized {
      val senderId = getId(sender)
      state.set(senderId)
      patterns.find(p => p.matches(state.value)) match {
        case Some(p) => {
          state.clear(p.value)
          // How to map the arguments to the body?
          val queues = channels.map({ case (obs, _) => getId(obs) })
                               .filter({ case (obs, _) => p.isSet(obs) })
                               .toList
          Future {
              val result = p.body(???)
              continuation(result)
          }
        }
        case None => {
          channels.get(sender) match {
            case Some(c) => c += msg
            case None => channels += (sender -> mutable.Queue(msg))
          }
        }
      }
    }
  }
}