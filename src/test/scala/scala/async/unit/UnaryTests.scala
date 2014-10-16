package scala.async.tests

import Util._
import org.junit.Test

import scala.async.Join._
import rx.lang.scala.Observable

// Test the join keyword when used with a single source Observable
class UnaryTests {
  @Test
  def unaryJoin() = {
    val input = (1 to randomNonZeroEvenInteger(maxListSize)).toList
    val fn = (x: Int) => x + 1
    val expected = input.map(fn)

    val o1 = Observable.just(input:_*).observeOn(newThreadScheduler).p
    
    val obs = join {
      case o1(x) => Next(fn(x))
      case o1.done => Done
    }

    obs.subscribe(println(_))
    readLine()
    ()
    // val result = obs.toBlocking.toList
    
    // assert(result == expected)
  }

  // @Test
  // def unaryJoinError() = {
  //   import scala.collection.JavaConversions._
   
  //   val size = randomNonZeroEvenInteger(2)
  //   val o1 = Observable.just(1 to size: _*).map(x => 
  //       if (x % size == 0) throw new Exception("") else x
  //   ).observeOn(newThreadScheduler).p

  //   val obs = join {
  //     case o1.error(e) => Next(e)
  //   }

  //   assert(obs.toBlocking.first.isInstanceOf[Throwable])
  // }

  // @Test
  // def unaryJoinDone() = {
  //   val input = (1 to randomNonZeroEvenInteger(maxListSize)).toList
  //   val o1 = Observable.just(input: _*).observeOn(newThreadScheduler).p
    
  //   val obs = join {
  //     case o1.done => Next(true)
  //   }
    
  //   assert(obs.toBlocking.first)
  // }

  // @Test
  // def unaryJoinDoneOutput() = {
  //   val input = (1 to randomNonZeroEvenInteger(maxListSize)).toList
  //   val o1 = Observable.just(input: _*).observeOn(newThreadScheduler).p
    
  //   val obs = join {
  //     case o1(x) => Done
  //   }
  //   obs.toBlocking
  //   assert(true)
  // }


  //  @Test
  // def unaryJoinGuard() = {
  //   val o1 = Observable.just(1, 2).p

  //   var received = false
  //   val obs = join {
  //     case o1(x) if !received => 
  //       received = true
  //       Next(true)
  //     case o1(x) if received => Done
  //   }
    
  //   assert(obs.toBlocking.first)
  // }

//  // TODO: Find a way to test this. Try Mockito again?
//  // @Test
//  //  def `unary join throw`() = {
//  //    val input = (1 to randomNonZeroEvenInteger(maxListSize)).toList
//  //    val o1 = Observable.items(input: _*).observeOn(newThreadScheduler).p
    
//  //    val obs = join {
//  //      case o1.done => throw new Exception("")
//  //    }
    
//  //  }

}