import scala.language.implicitConversions

val subjectPrefix = "s"
val observablePrefix = "o"
val threadPrefix = "thread"
val rxPatternPrefix = "p"

implicit def rangeToList(r: Range) = r.toList

def repeat(n: Int, sfn: Int=>String) = ((for (i <- (1 to n)) yield sfn(i)) mkString "\n") ++ "\n"

def subjects(n: Int) = repeat(n, (i: Int) => s"val $subjectPrefix$i = Subject[Int]")
def joinObservables(n: Int) = repeat(n, (i: Int) =>  s"val $observablePrefix$i = $subjectPrefix$i.observeOn(schedulerToUse).p")
def threadDefinitions(n: Int) = repeat(n, (i: Int) => s"val $threadPrefix$i = sendIndexFromThread($subjectPrefix$i, internalSize)")
def threadStarts(n: Int) = repeat(n, (i: Int) => s"$threadPrefix$i.start()")
def threadJoins(n: Int) = repeat(n, (i: Int) => s"$threadPrefix$i.join()")
def observableNames(ids: List[Int]) = ids.map(i => s"$observablePrefix$i")
def generateCaseUs(obs: List[String]) = {
  val pattern = obs.zipWithIndex.map({ case (name, index) => 
      val bidingVariable = List.fill(index + 1)('x').mkString
      s"$name($bidingVariable)" 
    }).mkString(" && ")
    (body: String) => s"case $pattern => { $body }"
}

def observables(n: Int) = repeat(n, (i: Int) =>  s"val $observablePrefix$i = $subjectPrefix$i.observeOn(schedulerToUse)")
def patternNames(ids: List[Int]) = ids.map(id => s"$rxPatternPrefix$id")
def whenStatement(names: List[String]) = s"val obs = RxJoinObservable.when(${names.mkString(",")}).toObservable"

def generatePattern(obs: List[String]) = {
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
  val firstHalf = observableNames(1 to (n / 2))
  val secondHalf = observableNames((n / 2) + 1 to n)
  val cases = List(generateCaseUs(firstHalf), generateCaseUs(secondHalf.toList)).map(caze => 
    caze("""counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }
    Next(())"""
  )).mkString("\n")
  generateTestUs(n, cases, base)
}

def twoCasesIndependentRxJava(n: Int, base: Boolean = false): String = {
  val names = patternNames(1 to 2)
  val firstHalf = observableNames(1 to (n / 2))
  val secondHalf = observableNames((n / 2) + 1 to n)
  val patterns = List(generatePattern(firstHalf), generatePattern(secondHalf))
  val cazes  = names.zip(patterns).map({ case (name, pattern) => 
    pattern(name, """counter.incrementAndGet()
    if (counter.get == (internalSize * 2)) { latch.countDown }""")
  }).mkString("\n")
  generateTestRxJava(n, s"$cazes\n${whenStatement(names.toList)}", base)
}

def twoCasesIndependentUsBase(n: Int) = twoCasesIndependentUs(n, true)
def twoCasesIndependentRxJavaBase(n: Int) = twoCasesIndependentRxJava(n, true)

def NCasesTwoIndependentUs(n: Int, base: Boolean = false): String  = {
  val cases = (for (i <- (1 to (n * 2) by 2)) yield generateCaseUs(observableNames(i to i + 1))).map(caze => 
    caze(s"""counter.incrementAndGet()
    if (counter.get == (internalSize * $n)) { latch.countDown }
    Next(())"""
  )).mkString("\n")
  generateTestUs(n * 2, cases, base)
}

def NCasesTwoIndependentRxJava(n: Int, base: Boolean = false): String = {
  val names = patternNames(1 to n)
  val patterns = for (i <- 1 to (n*2) by 2) yield generatePattern(observableNames(i to i + 1))
  val cazes = names.zip(patterns).map({ case (name, pattern) => 
    pattern(name, s"""counter.incrementAndGet()
    if (counter.get == (internalSize * $n)) { latch.countDown }""")
  }).mkString("\n")
  generateTestRxJava(n * 2, s"$cazes\n${whenStatement(names.toList)}", base)
}

def NCasesTwoIndependentUsBase(n: Int) = NCasesTwoIndependentUs(n, true)
def NCasesTwoIndependentRxJavaBase(n: Int) = NCasesTwoIndependentRxJava(n, true)

object InfiniteIterator {
  def just[A](element: A) = new Iterator[A] {
    def hasNext = true
    def next = element
  }
  def countFrom(start: Int) = new Iterator[Int] {
    var state = start - 1
    def hasNext = true
    def next = {
      state = state + 1
      state
    }
  }
}

def pairToList[A](p: (A, A)): List[A] = List(p._1, p._2)

// n is the total number of 2-cases, and d is the number of cases in which the first observable will be part of
def NDependentCasesUs(n: Int, d: Int, base: Boolean = false): String = {

  if (d > n || d == 0) ???

  val ones = InfiniteIterator.just(1).take(d)
  val others = InfiniteIterator.countFrom(2)

  val dependendCases = ones.zip(others).map(pairToList)
  val indpendendCases = others.take(2*n - d).grouped(2)

  val combined = (dependendCases ++ indpendendCases).take(n).toList

  val combinedWithNames = combined.map(pair => generateCaseUs(observableNames(pair.toList)))

  val patterns = combinedWithNames.map(caze => 
    caze(s"""counter.incrementAndGet()
    if (counter.get == internalSize) { latch.countDown }
    Next(())"""
  )).mkString("\n")

  val numberOfObservables = 2*n - d + 1
  generateTestUs(numberOfObservables, patterns, base)
}

def NDependentCasesRxJava(n: Int, d: Int, base: Boolean = false): String = {
  if (d > n || d == 0) ???

  val names = patternNames(1 to n)

  val ones = InfiniteIterator.just(1).take(d)
  val others = InfiniteIterator.countFrom(2)

  val dependendCases = ones.zip(others).map(pairToList)
  val indpendendCases = others.take(2*n - d).grouped(2)

  val combined = (dependendCases ++ indpendendCases).take(n).toList

  val combinedWithNames = combined.map(pair => generatePattern(observableNames(pair.toList)))

  val cazes = names.zip(combinedWithNames).map({ case (name, pattern) => 
    pattern(name, s"""counter.incrementAndGet()
    if (counter.get == internalSize) { latch.countDown }""")
  }).mkString("\n")
  val numberOfObservables = 2*n - d + 1

  generateTestRxJava(numberOfObservables, s"$cazes\n${whenStatement(names.toList)}", base)
}

def performanceTest(name: String, body: String, 
  minWarmupRuns: Int = 64,
  maxWarmupRuns: Int = 2048,
  benchRuns: Int = 2048,
  independentSamples: Int = 8) = s"""
performance of "$name" config (
  exec.minWarmupRuns -> $minWarmupRuns,
  exec.maxWarmupRuns -> $maxWarmupRuns,
  exec.benchRuns -> $benchRuns,
  exec.independentSamples -> $independentSamples) in 
{
  $body
}
"""

def check(number: Int, body: String, usingArgument: String = "XVALUE") = 
s"""if ($usingArgument == $number) {
  $body
}"""

def using(name: String, body: String, using: String = "XAXIS", usingArgument: String = "XVALUE") = 
s"""using($using) curve ("$name") in { $usingArgument =>
  $body
}"""

val ranges = List(2, 4, 8, 16, 32)
val theirName = "ReactiveX"
val newLine = "\n"

val rxJavaImports = s"""import rx.lang.scala.ImplicitFunctionConversions._
import rx.lang.scala.JavaConversions._"""

def generateFullTest(name: String, ourBody: String, theirBody: String) = {
  val deterministic = using("Non-Deterministic Choice", ourBody)
  val impl =  "implicit val checkOrder = scala.async.Join.InOrder"
  val nondeterministic = using("Deterministic Choice", impl ++ newLine ++ ourBody)
  val them = using("ReactiveX", rxJavaImports ++ newLine ++ theirBody)
  val body = deterministic ++ newLine ++ nondeterministic ++ newLine ++ them
  performanceTest(name, body) 
}

def fullyIndependentTwoCaseOut = {
  val ourBody = ranges.map(r => check(r, twoCasesIndependentUs(r))) mkString("\n")
  val theirBody = ranges.take(4).map(r => check(r, twoCasesIndependentRxJava(r))) mkString("\n")
  generateFullTest("twoCasesIndependend", ourBody, theirBody)
}

def fullyIndependentTwoCaseBaseOut = {
  val ourBody = ranges.map(r => check(r, twoCasesIndependentUsBase(r))) mkString("\n")
  val theirBody = ranges.take(4).map(r => check(r, twoCasesIndependentRxJavaBase(r))) mkString("\n")
  generateFullTest("twoCasesIndependendBase", ourBody, theirBody)
}

def NCasesTwoIndependentOut = {
  val ourBody = ranges.map(r => check(r, NCasesTwoIndependentUs(r))) mkString("\n")
  val theirBody = ranges.map(r => check(r, NCasesTwoIndependentRxJava(r))) mkString("\n")
  generateFullTest("NCaseTwoIndependent", ourBody, theirBody)
}

def NCasesTwoIndependentBaseOut = {
  val ourBody = ranges.map(r => check(r, NCasesTwoIndependentUsBase(r))) mkString("\n")
  val theirBody = ranges.map(r => check(r, NCasesTwoIndependentRxJavaBase(r))) mkString("\n")
  generateFullTest("NCaseTwoIndependentBase", ourBody, theirBody)
}

def NDependentCasesOut = {
  val ourBody = ranges.map(r => check(r, NDependentCasesUs(32, r))) mkString("\n")
  val theirBody = ranges.map(r => check(r, NDependentCasesRxJava(32, r))) mkString("\n")
  generateFullTest("NDependentCases", ourBody, theirBody)
}

def NDependentCasesBaseOut = {
  val ourBody = ranges.map(r => check(r, NDependentCasesUs(32, r, true))) mkString("\n")
  val theirBody = ranges.map(r => check(r, NDependentCasesRxJava(32, r, true))) mkString("\n")
  generateFullTest("NDependentCasesBase", ourBody, theirBody)
}

val internalSize = 1024
val iterations = 1

// $fullyIndependentTwoCaseOut
// $fullyIndependentTwoCaseBaseOut
// $NCasesTwoIndependentOut
// $NCasesTwoIndependentBaseOut

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

  val XAXIS = Gen.enumeration("XAXIS")(2, 4, 8, 16, 32)

  val iterations = $iterations
  val internalSize = $internalSize

  $NDependentCasesOut
  $NDependentCasesBaseOut
}"""
println(out)
