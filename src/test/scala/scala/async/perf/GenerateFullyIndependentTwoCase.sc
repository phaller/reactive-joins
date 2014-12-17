
val subjectPrefix = "s"
val observablePrefix = "o"
val threadPrefix = "thread"
val rxPatternPrefix = "p"

def nTimes(n: Int) = (sfn: Int=>String) => ((for (i <- (1 to n)) yield sfn(i)) mkString "\n") ++ "\n"

def twoCasesIndependentUs(n: Int): String  = {

  def repeat = if (n % 2 == 0) nTimes(n) else ??? // What? I have deadlines!

  def subjects = repeat((i: Int) => s"val $subjectPrefix$i = Subject[Int]")
  def joinObservables = repeat((i: Int) =>  s"val $observablePrefix$i = $subjectPrefix$i.observeOn(newThreadScheduler).p")
  def threadDefinitions = repeat((i: Int) => s"val $threadPrefix$i = sendIndexFromThread($subjectPrefix$i, internalSize)")
  def threadStarts = repeat((i: Int) => s"$threadPrefix$i.start()")
  def threadJoins = repeat((i: Int) => s"$threadPrefix$i.join()")
  def observableNames(r: Range) = for (i <- r) yield s"$observablePrefix$i"

  def generateCase(r: Range) = {
    val pattern = observableNames(r).zipWithIndex.map({ case (name, index) => 
        val bidingVariable = List.fill(index + 1)('x').mkString
        s"$name($bidingVariable)" 
      }).mkString(" && ")
      (body: String) => s"case $pattern => { $body }"
  }

  val cases = 
  List(generateCase(1 to (n/2)), generateCase((n/2)+1 to n)).map(caze => 
    caze("""counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(())"""
  )).mkString("\n")

  s"""

var j = 0
while (j < iterations) {

${subjects}
${joinObservables}

val gotAll = new CountDownLatch(internalSize)
val latch = new CountDownLatch(1)
val counter = new AtomicInteger(0)

val obs = join {
$cases
}.serialize

obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())

${threadDefinitions}
${threadStarts}
latch.await
gotAll.await

${threadJoins}
j = j + 1
}
"""
}

def twoCasesIndependentUsBase(n: Int): String  = {

  def repeat = if (n % 2 == 0) nTimes(n) else ??? // What? I have deadlines!

  def subjects = repeat((i: Int) => s"val $subjectPrefix$i = Subject[Int]")
  def joinObservables = repeat((i: Int) =>  s"val $observablePrefix$i = $subjectPrefix$i.observeOn(newThreadScheduler).p")
  def observableNames(r: Range) = for (i <- r) yield s"$observablePrefix$i"
  def threadDefinitions = repeat((i: Int) => s"val $threadPrefix$i = sendIndexFromThread($subjectPrefix$i, internalSize)")

  def generateCase(r: Range) = {
    val pattern = observableNames(r).zipWithIndex.map({ case (name, index) => 
        val bidingVariable = List.fill(index + 1)('x').mkString
        s"$name($bidingVariable)" 
      }).mkString(" && ")
      (body: String) => s"case $pattern => { $body }"
  }

  val cases = 
  List(generateCase(1 to (n/2)), generateCase((n/2)+1 to n)).map(caze => 
    caze("""counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(())"""
  )).mkString("\n")

  s"""

var j = 0
while (j < iterations) {

${subjects}
${joinObservables}

val gotAll = new CountDownLatch(internalSize)
val latch = new CountDownLatch(1)
val counter = new AtomicInteger(0)

val obs = join {
$cases
}.serialize

obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())

j = j + 1
}
"""
}
def twoCasesIndependentRxJava(n: Int): String = {

  def repeat = if (n % 2 == 0) nTimes(n) else ??? // What? I have deadlines!

  def subjects = repeat((i: Int) => s"val $subjectPrefix$i = Subject[Int]")
  def observables = repeat((i: Int) =>  s"val $observablePrefix$i = $subjectPrefix$i.observeOn(newThreadScheduler)")
  def threadDefinitions = repeat((i: Int) => s"val $threadPrefix$i = sendIndexFromThread($subjectPrefix$i, internalSize)")
  def threadStarts = repeat((i: Int) => s"$threadPrefix$i.start()")
  def threadJoins = repeat((i: Int) => s"$threadPrefix$i.join()")

  def patternNames(r: Range) = for (i <- r) yield s"$rxPatternPrefix$i"
  def observableNames(r: Range) = for (i <- r) yield s"$observablePrefix$i"

  def generatePattern(r: Range) = {
    val obs = observableNames(r)
    val first = s"from(${obs.head})"
    val rest = obs.tail.map(o => s".and($o)")
    val fromAnd = first ++ rest.mkString
    val fnSignature = obs.zipWithIndex.map({ case(o, index) => 
      val argName = List.fill(index + 1)('x').mkString
      s"$argName: Int"
    }).mkString(", ")
    (name: String, body: String) => s"val $name = RxJoinObservable.$fromAnd.then(($fnSignature) => { $body })"
  }

  val names = patternNames(1 to 2)

  val patterns = List(generatePattern(1 to (n/2)), generatePattern((n/2)+1 to n))

  val cazes  = names.zip(patterns).map({ case (name, pattern) => 
    pattern(name, """counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }""")
  }).mkString("\n")
 
  val obs = s"val obs = RxJoinObservable.when(${names.mkString(",")}).toObservable"

s"""
var j = 0
while (j < iterations) {

${subjects}
${observables}

val gotAll = new CountDownLatch(internalSize)
val latch = new CountDownLatch(1)
val counter = new AtomicInteger(0)

$cazes

$obs

obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())

${threadDefinitions}
${threadStarts}
latch.await
gotAll.await

${threadJoins}
j = j + 1
}
"""
}

def twoCasesIndependentRxJavaBase(n: Int): String = {

  def repeat = if (n % 2 == 0) nTimes(n) else ??? // What? I have deadlines!

  def subjects = repeat((i: Int) => s"val $subjectPrefix$i = Subject[Int]")
  def observables = repeat((i: Int) =>  s"val $observablePrefix$i = $subjectPrefix$i.observeOn(newThreadScheduler)")
  def threadDefinitions = repeat((i: Int) => s"val $threadPrefix$i = sendIndexFromThread($subjectPrefix$i, internalSize)")
  def threadStarts = repeat((i: Int) => s"$threadPrefix$i.start()")
  def threadJoins = repeat((i: Int) => s"$threadPrefix$i.join()")

  def patternNames(r: Range) = for (i <- r) yield s"$rxPatternPrefix$i"
  def observableNames(r: Range) = for (i <- r) yield s"$observablePrefix$i"

  def generatePattern(r: Range) = {
    val obs = observableNames(r)
    val first = s"from(${obs.head})"
    val rest = obs.tail.map(o => s".and($o)")
    val fromAnd = first ++ rest.mkString
    val fnSignature = obs.zipWithIndex.map({ case(o, index) => 
      val argName = List.fill(index + 1)('x').mkString
      s"$argName: Int"
    }).mkString(", ")
    (name: String, body: String) => s"val $name = RxJoinObservable.$fromAnd.then(($fnSignature) => { $body })"
  }

  val names = patternNames(1 to 2)

  val patterns = List(generatePattern(1 to (n/2)), generatePattern((n/2)+1 to n))

  val cazes  = names.zip(patterns).map({ case (name, pattern) => 
    pattern(name, """counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }""")
  }).mkString("\n")
 
  val obs = s"val obs = RxJoinObservable.when(${names.mkString(",")}).toObservable"

s"""
var j = 0
while (j < iterations) {

${subjects}
${observables}

val gotAll = new CountDownLatch(internalSize)
val latch = new CountDownLatch(1)
val counter = new AtomicInteger(0)

$cazes

$obs

obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())

j = j + 1
}
"""
}

def NCasesTwoIndependentUs(n: Int): String  = {

  def repeat = if (n % 2 == 0) nTimes(n * 2) else ??? // What? I have deadlines!

  def subjects = repeat((i: Int) => s"val $subjectPrefix$i = Subject[Int]")
  def joinObservables = repeat((i: Int) =>  s"val $observablePrefix$i = $subjectPrefix$i.observeOn(newThreadScheduler).p")
  def threadDefinitions = repeat((i: Int) => s"val $threadPrefix$i = sendIndexFromThread($subjectPrefix$i, internalSize)")
  def threadStarts = repeat((i: Int) => s"$threadPrefix$i.start()")
  def threadJoins = repeat((i: Int) => s"$threadPrefix$i.join()")
  def observableNames(r: Range) = for (i <- r) yield s"$observablePrefix$i"

  def generateCase(r: Range) = {
    val pattern = observableNames(r).zipWithIndex.map({ case (name, index) => 
        val bidingVariable = List.fill(index + 1)('x').mkString
        s"$name($bidingVariable)" 
      }).mkString(" && ")
      (body: String) => s"case $pattern => { $body }"
  }

  val cases = (for (i <- (1 to (n * 2) by 2)) yield generateCase(i to i + 1)).map(caze => 
    caze(s"""counter.incrementAndGet()
    if (counter.get == (internalSize * $n)) { latch.countDown }
    Next(())"""
  )).mkString("\n")

  s"""

var j = 0
while (j < iterations) {

${subjects}
${joinObservables}

val gotAll = new CountDownLatch(internalSize)
val latch = new CountDownLatch(1)
val counter = new AtomicInteger(0)

val obs = join {
$cases
}.serialize

obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())

${threadDefinitions}
${threadStarts}
latch.await
gotAll.await

${threadJoins}
j = j + 1
}
"""
}

def NCasesTwoIndependentRxJava(n: Int): String = {

  def repeat = if (n % 2 == 0) nTimes(n * 2) else ??? // What? I have deadlines!

  def subjects = repeat((i: Int) => s"val $subjectPrefix$i = Subject[Int]")
  def observables = repeat((i: Int) =>  s"val $observablePrefix$i = $subjectPrefix$i.observeOn(newThreadScheduler)")
  def threadDefinitions = repeat((i: Int) => s"val $threadPrefix$i = sendIndexFromThread($subjectPrefix$i, internalSize)")
  def threadStarts = repeat((i: Int) => s"$threadPrefix$i.start()")
  def threadJoins = repeat((i: Int) => s"$threadPrefix$i.join()")

  def patternNames(r: Range) = for (i <- r) yield s"$rxPatternPrefix$i"
  def observableNames(r: Range) = for (i <- r) yield s"$observablePrefix$i"

  def generatePattern(r: Range) = {
    val obs = observableNames(r)
    val first = s"from(${obs.head})"
    val rest = obs.tail.map(o => s".and($o)")
    val fromAnd = first ++ rest.mkString
    val fnSignature = obs.zipWithIndex.map({ case(o, index) => 
      val argName = List.fill(index + 1)('x').mkString
      s"$argName: Int"
    }).mkString(", ")
    (name: String, body: String) => s"val $name = RxJoinObservable.$fromAnd.then(($fnSignature) => { $body })"
  }

  val names = patternNames(1 to n)

  val patterns = for (i <- 1 to (n*2) by 2) yield generatePattern(i to i + 1)

  val cazes = names.zip(patterns).map({ case (name, pattern) => 
    pattern(name, s"""counter.incrementAndGet()
    if (counter.get == (internalSize * $n)) { latch.countDown }""")
  }).mkString("\n")
 
  val obs = s"val obs = RxJoinObservable.when(${names.mkString(",")}).toObservable"

s"""
var j = 0
while (j < iterations) {

${subjects}
${observables}

val gotAll = new CountDownLatch(internalSize)
val latch = new CountDownLatch(1)
val counter = new AtomicInteger(0)

$cazes

$obs

obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())

${threadDefinitions}
${threadStarts}
latch.await
gotAll.await

${threadJoins}
j = j + 1
}
"""
}

val out = s"""package scala.async.tests

import scala.async.Join._
import rx.functions._
import rx.lang.scala._
import rx.observables.{ JoinObservable => RxJoinObservable }
import scala.async.tests.Util._
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import rx.lang.scala.Subject
import rx.lang.scala.subjects.ReplaySubject
import org.scalameter.api._

class RxReactBench extends PerformanceTest.OfflineReport {

 implicit def scalaFunction8ToRxFunc8[A, B, C, D, E, F, G, H, I](fn: (A, B, C, D, E, F, G, H) => I): Func8[A, B, C, D, E, F, G, H, I] =
  new Func8[A, B, C, D, E, F, G, H, I] {
    def call(a: A, b: B, c: C, d: D, e :E, f: F, g: G, h: H) = fn(a, b, c, d, e, f, g, h)
  }

  def sendIndexFromThread(s: Subject[Int], repeats: Int) = new Thread(new Runnable {
    def run() {
      var i = 0
      while (i < repeats) {
        s.onNext(i)
        i = i + 1
      }
      s.onCompleted()
    }
  })

  val independentObservables = Gen.enumeration("Observables")(2, 4, 8, 16, 32)
  val iterations = 1
  val internalSize = 1024

  performance of "fullyIndependentTwoCase" config (
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 1000,
    exec.independentSamples -> 1) in 
  {

      using(independentObservables) curve ("Us") in { independentObservablesNumber =>

        if (independentObservablesNumber == 2) {
          ${twoCasesIndependentUs(2)}
        }
        if (independentObservablesNumber == 4) {
          ${twoCasesIndependentUs(4)}
        }
        if (independentObservablesNumber == 8) {
          ${twoCasesIndependentUs(8)}
        }
        if (independentObservablesNumber == 16) {
          ${twoCasesIndependentUs(16)}
        }
        if (independentObservablesNumber == 32) {
          ${twoCasesIndependentUs(32)}
        }

      }
      using(independentObservables) curve ("ReactiveX") in { independentObservablesNumber =>
        import rx.lang.scala.ImplicitFunctionConversions._
        import rx.lang.scala.JavaConversions._

        if (independentObservablesNumber == 2) {
          ${twoCasesIndependentRxJava(2)}
        }

        if (independentObservablesNumber == 4) {
          ${twoCasesIndependentRxJava(4)}
        }

        if (independentObservablesNumber == 8) {
          ${twoCasesIndependentRxJava(8)}
        }

        if (independentObservablesNumber == 16) {
          ${twoCasesIndependentRxJava(16)}
        }

    
      }
  }

  performance of "fullyIndependentTwoCaseBase" config (
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 30,
    exec.independentSamples -> 1) in 
  {

      using(independentObservables) curve ("Us") in { independentObservablesNumber =>

        if (independentObservablesNumber == 2) {
          ${twoCasesIndependentUsBase(2)}
        }
        if (independentObservablesNumber == 4) {
          ${twoCasesIndependentUsBase(4)}
        }
        if (independentObservablesNumber == 8) {
          ${twoCasesIndependentUsBase(8)}
        }
        if (independentObservablesNumber == 16) {
          ${twoCasesIndependentUsBase(16)}
        }
        if (independentObservablesNumber == 32) {
          ${twoCasesIndependentUsBase(32)}
        }

      }
      using(independentObservables) curve ("ReactiveX") in { independentObservablesNumber =>
        import rx.lang.scala.ImplicitFunctionConversions._
        import rx.lang.scala.JavaConversions._

        if (independentObservablesNumber == 2) {
          ${twoCasesIndependentRxJavaBase(2)}
        }

        if (independentObservablesNumber == 4) {
          ${twoCasesIndependentRxJavaBase(4)}
        }

        if (independentObservablesNumber == 8) {
          ${twoCasesIndependentRxJavaBase(8)}
        }

        if (independentObservablesNumber == 16) {
          ${twoCasesIndependentRxJavaBase(16)}
        }

    
      }
  }

   performance of "NCaseTwoIndependent" config (
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 30,
    exec.independentSamples -> 1) in 
  {

      using(independentObservables) curve ("Us") in { independentObservablesNumber =>

        if (independentObservablesNumber == 2) {
          ${NCasesTwoIndependentUs(2)}
        }
        if (independentObservablesNumber == 4) {
          ${NCasesTwoIndependentUs(4)}
        }
        if (independentObservablesNumber == 8) {
          ${NCasesTwoIndependentUs(8)}
        }
        if (independentObservablesNumber == 16) {
          ${NCasesTwoIndependentUs(16)}
        }
        if (independentObservablesNumber == 32) {
          ${NCasesTwoIndependentUs(32)}
        }

      }
      using(independentObservables) curve ("ReactiveX") in { independentObservablesNumber =>
        import rx.lang.scala.ImplicitFunctionConversions._
        import rx.lang.scala.JavaConversions._

        if (independentObservablesNumber == 2) {
          ${NCasesTwoIndependentRxJava(2)}
        }

        if (independentObservablesNumber == 4) {
          ${NCasesTwoIndependentRxJava(4)}
        }

        if (independentObservablesNumber == 8) {
          ${NCasesTwoIndependentRxJava(8)}
        }

        if (independentObservablesNumber == 16) {
          ${NCasesTwoIndependentRxJava(16)}
        }

        if (independentObservablesNumber == 32) {
          ${NCasesTwoIndependentRxJava(32)}
        }
    
      }
  }

}"""
println(out)