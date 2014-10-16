// package scala.async.tests

// import Util._
// import org.junit.Test

// import scala.async.Join._
// import rx.lang.scala.Observable

// class JoinResultTests {

  @Test
  def joinResultImplementedCorrectly() = {
    val input = (1 to randomNonZeroEvenInteger(maxListSize)).toList
    val o1 = Observable.just(input: _*).p

    var received = false
    val obs = join {
      case o1(x) if !received => 
        received = true
        Pass
      case o1(x) if received => Next(x)
      case o1.done => Done
    }
    
    assert(obs.toBlocking.toList == input.tail)
  }

//   @Test
//   def joinResultImplicitPassWorks() = {
//     val input = (1 to randomNonZeroEvenInteger(maxListSize)).toList
//     val o1 = Observable.just(input: _*).p

//     var received = false
//     val obs = join {
//       case o1(x) if !received => received = true
//       case o1(x) if received => Next(x)
//       case o1.done => Done
//     }
    
//     assert(obs.toBlocking.toList == input.tail)
//   }

//   @Test
//   def coVariantJoinReturn() = {
//     sealed trait Animal
//     case class Dog(name: String) extends Animal
//     case class Chicken(name: String) extends Animal

//     val numberOfEvents = randomNonZeroEvenInteger(maxListSize)

//     val o1 = Observable.just(List.fill(numberOfEvents)(1): _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p
//     val o2 = Observable.just(List.fill(numberOfEvents)(2): _*).subscribeOn(newThreadScheduler).observeOn(newThreadScheduler).p

//     val obs: Observable[Animal] = join {
//       case o1(x) => Next(Dog("Lassie"))
//       case o2(x) => Next(Chicken("Pgack"))
//       case o1.done && o2.done => Done
//     }

//     val result = obs.toBlocking.toList
//     assert(result.count(_.isInstanceOf[Dog]) == numberOfEvents)
//     assert(result.count(_.isInstanceOf[Chicken]) == numberOfEvents)
//     assert(result.count(_.isInstanceOf[Animal]) == 2 * numberOfEvents)
//   }
// }