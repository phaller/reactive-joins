
val subjectPrefix = "s"
val observablePrefix = "o"
val threadPrefix = "thread"
val rxPatternPrefix = "p"

def repeat(n: Int, sfn: Int=>String) = ((for (i <- (1 to n)) yield sfn(i)) mkString "\n") ++ "\n"

def subjects(n: Int) = repeat(n, (i: Int) => s"val $subjectPrefix$i = Subject[Int]")
def joinObservables(n: Int) = repeat(n, (i: Int) =>  s"val $observablePrefix$i = $subjectPrefix$i.observeOn(schedulerToUse).p")
def threadDefinitions(n: Int) = repeat(n, (i: Int) => s"val $threadPrefix$i = sendIndexFromThread($subjectPrefix$i, internalSize)")
def threadStarts(n: Int) = repeat(n, (i: Int) => s"$threadPrefix$i.start()")
def threadJoins(n: Int) = repeat(n, (i: Int) => s"$threadPrefix$i.join()")
def observableNames(r: Range) = for (i <- r) yield s"$observablePrefix$i"
def generateCaseUs(r: Range) = {
  val pattern = observableNames(r).zipWithIndex.map({ case (name, index) => 
      val bidingVariable = List.fill(index + 1)('x').mkString
      s"$name($bidingVariable)" 
    }).mkString(" && ")
    (body: String) => s"case $pattern => { $body }"
}

def observables(n: Int) = repeat(n, (i: Int) =>  s"val $observablePrefix$i = $subjectPrefix$i.observeOn(schedulerToUse)")
def patternNames(r: Range) = for (i <- r) yield s"$rxPatternPrefix$i"
def whenStatement(names: List[String]) = s"val obs = RxJoinObservable.when(${names.mkString(",")}).toObservable"

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

def outerLoop(body: String) = s"""var j = 0
  while (j < iterations) {
    $body
    j = j + 1
  }"""

val synchronizationDefs = """val gotAll = new CountDownLatch(internalSize)
  val latch = new CountDownLatch(1)
  val counter = new AtomicInteger(0)"""

val observableSubscription = "obs.subscribe((_: Unit) => gotAll.countDown, (_: Throwable) => (), () => ())"
val latchAwait = """latch.await
gotAll.await"""

def generateTest(n: Int, observables: String, joinPart: String, base: Boolean) = {
  val full = if (base) "" else s"""${threadDefinitions(n)}
    ${threadStarts(n)}
    $latchAwait
    ${threadJoins(n)}
  """
  outerLoop(s"""
    ${subjects(n)}
    $observables
    $synchronizationDefs
    $joinPart
    $observableSubscription
    $full
    """)
}

def generateTestUs(n: Int, cases: String, base: Boolean) = generateTest(n,
  joinObservables(n), 
  s"""val obs = join {
      $cases
    }.serialize""",
    base)

def generateTestRxJava(n: Int, cases: String, base: Boolean) = generateTest(n, observables(n), cases, base)

def twoCasesIndependentUs(n: Int, base: Boolean = false): String  = {
  val cases = List(generateCaseUs(1 to (n/2)), generateCaseUs((n/2)+1 to n)).map(caze => 
    caze("""counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(())"""
  )).mkString("\n")
  generateTestUs(n, cases, base)
}

def twoCasesIndependentRxJava(n: Int, base: Boolean = false): String = {
  val names = patternNames(1 to 2)
  val patterns = List(generatePattern(1 to (n/2)), generatePattern((n/2)+1 to n))
  val cazes  = names.zip(patterns).map({ case (name, pattern) => 
    pattern(name, """counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }""")
  }).mkString("\n")
  generateTestRxJava(n, s"$cazes\n${whenStatement(names.toList)}", base)
}

def twoCasesIndependentUsBase(n: Int) = twoCasesIndependentUs(n, true)
def twoCasesIndependentRxJavaBase(n: Int) = twoCasesIndependentRxJava(n, true)

def NCasesTwoIndependentUs(n: Int, base: Boolean = false): String  = {
  val cases = (for (i <- (1 to (n * 2) by 2)) yield generateCaseUs(i to i + 1)).map(caze => 
    caze(s"""counter.incrementAndGet()
    if (counter.get == (internalSize * $n)) { latch.countDown }
    Next(())"""
  )).mkString("\n")
  generateTestUs(n * 2, cases, base)
}

def NCasesTwoIndependentRxJava(n: Int, base: Boolean = false): String = {
  val names = patternNames(1 to n)
  val patterns = for (i <- 1 to (n*2) by 2) yield generatePattern(i to i + 1)
  val cazes = names.zip(patterns).map({ case (name, pattern) => 
    pattern(name, s"""counter.incrementAndGet()
    if (counter.get == (internalSize * $n)) { latch.countDown }""")
  }).mkString("\n")
  generateTestRxJava(n * 2, s"$cazes\n${whenStatement(names.toList)}", base)
}

def NCasesTwoIndependentUsBase(n: Int) = NCasesTwoIndependentUs(n, true)
def NCasesTwoIndependentRxJavaBase(n: Int) = NCasesTwoIndependentRxJava(n, true)

val fullyIndependentTwoCaseOut = s"""
  performance of "fullyIndependentTwoCase" config (
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 1024,
    exec.benchRuns -> 1024,
    exec.independentSamples -> 5) in 
  {
      //////////////////////////////////////////////////////////////////////////////////////
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
      //////////////////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////////////////
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
      //////////////////////////////////////////////////////////////////////////////////////
  }
"""
val fullyIndependentTwoCaseBaseOut = s"""
  performance of "fullyIndependentTwoCaseBase" config (
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 1024,
    exec.benchRuns -> 1024,
    exec.independentSamples -> 5) in 
  {

      //////////////////////////////////////////////////////////////////////////////////////
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
      //////////////////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////////////////
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
      //////////////////////////////////////////////////////////////////////////////////////
  }
"""
val NCasesTwoIndependentOut = s"""
   performance of "NCaseTwoIndependent" config (
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 1024,
    exec.benchRuns -> 1024,
    exec.independentSamples -> 5) in {
      //////////////////////////////////////////////////////////////////////////////////////
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
      //////////////////////////////////////////////////////////////////////////////////////
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
      //////////////////////////////////////////////////////////////////////////////////////
  }
"""

val NCasesTwoIndependentBaseOut = s"""
   performance of "NCaseTwoIndependent" config (
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 1024,
    exec.benchRuns -> 1024,
    exec.independentSamples -> 5) in {
      //////////////////////////////////////////////////////////////////////////////////////
      using(independentObservables) curve ("Us") in { independentObservablesNumber =>

        if (independentObservablesNumber == 2) {
          ${NCasesTwoIndependentUsBase(2)}
        }
        if (independentObservablesNumber == 4) {
          ${NCasesTwoIndependentUsBase(4)}
        }
        if (independentObservablesNumber == 8) {
          ${NCasesTwoIndependentUsBase(8)}
        }
        if (independentObservablesNumber == 16) {
          ${NCasesTwoIndependentUsBase(16)}
        }
        if (independentObservablesNumber == 32) {
          ${NCasesTwoIndependentUsBase(32)}
        }

      }
      //////////////////////////////////////////////////////////////////////////////////////
      using(independentObservables) curve ("ReactiveX") in { independentObservablesNumber =>
        import rx.lang.scala.ImplicitFunctionConversions._
        import rx.lang.scala.JavaConversions._

        if (independentObservablesNumber == 2) {
          ${NCasesTwoIndependentRxJavaBase(2)}
        }

        if (independentObservablesNumber == 4) {
          ${NCasesTwoIndependentRxJavaBase(4)}
        }

        if (independentObservablesNumber == 8) {
          ${NCasesTwoIndependentRxJavaBase(8)}
        }

        if (independentObservablesNumber == 16) {
          ${NCasesTwoIndependentRxJavaBase(16)}
        }

        if (independentObservablesNumber == 32) {
          ${NCasesTwoIndependentRxJavaBase(32)}
        }
      }
      //////////////////////////////////////////////////////////////////////////////////////
  }
"""

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

  $fullyIndependentTwoCaseOut
  $fullyIndependentTwoCaseBaseOut
  $NCasesTwoIndependentOut
  $NCasesTwoIndependentBaseOut

}"""
println(out)