import scala.language.implicitConversions

val subjectPrefix = "s"
val observablePrefix = "o"
val threadPrefix = "thread"
val rxPatternPrefix = "p"
val latchName = "latch"
val ranges = List(2, 4, 8, 16, 32)
val theirName = "ReactiveX"
val newLine = "\n"
val size = 16384

val rxJavaImports = s"""import rx.lang.scala.ImplicitFunctionConversions._
import rx.lang.scala.JavaConversions._"""

implicit def rangeToList(r: Range) = r.toList

def repeat(n: Int, sfn: Int=>String) = ((for (i <- (1 to n)) yield sfn(i)) mkString "\n") ++ "\n"

def subjects(n: Int) = repeat(n, (i: Int) => s"val $subjectPrefix$i = Subject[Int]")
def joinObservables(n: Int) = repeat(n, (i: Int) =>  s"val $observablePrefix$i = $subjectPrefix$i.onBackpressureBuffer.observeOn(schedulerToUse).p")
def threadDefine(i: Int, sendSize: Int) = s"val $threadPrefix$i = sendIndexFromThread($subjectPrefix$i, $sendSize)"
def threadDefinitions(n: Int, sendSize: Int = size) = repeat(n, (i: Int) => threadDefine(i, sendSize))
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

// Used for RxJava
def observables(n: Int) = repeat(n, (i: Int) =>  s"val $observablePrefix$i = $subjectPrefix$i.onBackpressureBuffer.observeOn(schedulerToUse)")
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

val observableSubscription = s"obs.subscribe((_: Int) => $latchName.countDown, (_: Throwable) => (), () => ())"

def generateTest(n: Int, observables: String, joinPart: String, awaitNumber: Int) = {
 s"""
  ${subjects(n)}
  $observables
  val $latchName = new CountDownLatch($awaitNumber)
  $joinPart
  $observableSubscription
  ${threadDefinitions(n)}
  ${threadStarts(n)}
  $latchName.await
  ${threadJoins(n)}
  """
}

def generateTestUs(n: Int, cases: String, awaitNumber: Int) = generateTest(n,
  joinObservables(n), 
  s"""val obs = join {
      $cases
    }.serialize""",
  awaitNumber)

def generateTestRxJava(n: Int, cases: String, awaitNumber: Int) = generateTest(n, observables(n), cases, awaitNumber)

def generateTwoCasesIndependentUsTest(n: Int, awaitNumber: Int): String  = {
  val firstHalf = observableNames(1 to (n / 2))
  val secondHalf = observableNames((n / 2) + 1 to n)
  val cases = List(generateCaseUs(firstHalf), generateCaseUs(secondHalf.toList)).map(caze => 
    caze("Next(1)"
  )).mkString("\n")
  generateTestUs(n, cases, awaitNumber)
}

def generateTwoCasesIndependentRxJavaTest(n: Int, awaitNumber: Int): String = {
  val names = patternNames(1 to 2)
  val firstHalf = observableNames(1 to (n / 2))
  val secondHalf = observableNames((n / 2) + 1 to n)
  val patterns = List(generatePattern(firstHalf), generatePattern(secondHalf))
  val cazes  = names.zip(patterns).map({ case (name, pattern) => 
    pattern(name, "1")
  }).mkString("\n")
  generateTestRxJava(n, s"$cazes\n${whenStatement(names.toList)}", awaitNumber)
}

def generateNCasesTwoIndependentUsTest(n: Int, awaitNumber: Int): String  = {
  val cases = (for (i <- (1 to (n * 2) by 2)) yield generateCaseUs(observableNames(i to i + 1))).map(caze => 
    caze(s"Next(1)"
  )).mkString("\n")
  generateTestUs(n * 2, cases, awaitNumber)
}

def generateNCasesTwoIndependentRxJavaTest(n: Int, awaitNumber: Int): String = {
  val names = patternNames(1 to n)
  val patterns = for (i <- 1 to (n*2) by 2) yield generatePattern(observableNames(i to i + 1))
  val cazes = names.zip(patterns).map({ case (name, pattern) => 
    pattern(name, s"1")
  }).mkString("\n")
  generateTestRxJava(n * 2, s"$cazes\n${whenStatement(names.toList)}", awaitNumber)
}

def generateNDependentCasesUsTest(n: Int, d: Int, awaitNumber: Int): String = {
  val twoN = n * 2
  val indpendendCasesOfTwo = for (i <- 1 to twoN by 2) yield (i to i + 1).toList
  val (toBeDependend, rest) = indpendendCasesOfTwo.splitAt(d)
  val dependend = toBeDependend.map(l => l ++ List(twoN + 1))
  val combined = dependend ++ rest
  
  val combinedWithNames = combined.map(l => generateCaseUs(observableNames(l)))
  val cases = combinedWithNames.map(caze => 
    caze(s"Next(1)")).mkString("\n")

  val observables = joinObservables(twoN + 1)
  val joinPart = s"""val obs = join {
      $cases
    }.serialize"""
  val twoNPlusOne = twoN + 1
  s"""
  ${subjects(twoNPlusOne)}
  $observables
  val $latchName = new CountDownLatch($awaitNumber)
  $joinPart
  $observableSubscription
  ${threadDefinitions(twoN)}
  ${threadDefine(twoNPlusOne, d*size)}
  ${threadStarts(twoNPlusOne)}
  $latchName.await
  ${threadJoins(twoNPlusOne)}
  """
}

def generateNDependentCasesRxJavaTest(n: Int, d: Int, awaitNumber: Int): String = {
  val twoN = n * 2
  val names = patternNames(1 to n)

  val indpendendCasesOfTwo = for (i <- 1 to twoN by 2) yield (i to i + 1).toList
  val (toBeDependend, rest) = indpendendCasesOfTwo.splitAt(d)
  val dependend = toBeDependend.map(l => l ++ List(twoN + 1))
  val combined = dependend ++ rest
  
  val combinedWithNames = combined.map(l => generatePattern(observableNames(l)))

  val cases = names.zip(combinedWithNames).map({ case (name, pattern) => 
    pattern(name, s"1")
  }).mkString("\n")
  val obs = observables(twoN + 1)

  val twoNPlusOne = twoN + 1
  s"""
  ${subjects(twoNPlusOne)}
  $obs
  val $latchName = new CountDownLatch($awaitNumber)
  $cases
  ${whenStatement(names.toList)}
  $observableSubscription
  ${threadDefinitions(twoN)}
  ${threadDefine(twoNPlusOne, d*size)}
  ${threadStarts(twoNPlusOne)}
  $latchName.await
  ${threadJoins(twoNPlusOne)}
  """
}

def performanceTest(name: String, body: String, 
  minWarmupRuns: Int = 1024,
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

case class UsingArguments(name: String, argument: String)

def check(number: Int, body: String)(implicit usingArguments: UsingArguments) = 
s"""if (${usingArguments.argument} == $number) {
  $body
}"""

def using(name: String, body: String)(implicit usingArguments: UsingArguments) = 
s"""using(${usingArguments.name}) curve ("$name") in { ${usingArguments.argument} =>
  $body
}"""

def assembleTest(name: String, ourBody: String, theirBody: String, includeDeterminstic: Boolean = true)(implicit usingArguments: UsingArguments) = {
  val deterministic = using("Non-Deterministic Choice", ourBody)
  var nondeterministic = ""
  if (includeDeterminstic) {
    val impl =  "implicit val checkOrder = scala.async.Join.InOrder"
    nondeterministic = using("Deterministic Choice", impl ++ newLine ++ ourBody)
  }
  val them = using("ReactiveX", rxJavaImports ++ newLine ++ theirBody)
  val body = deterministic ++ newLine ++ nondeterministic ++ newLine ++ them
  performanceTest(name, body) 
}

def twoCasesNObservablesOut = {
  implicit val xaxis = UsingArguments("twoCasesNObservablesOutRange", "nObservables")
  val ourBody = ranges.map(r => check(r, generateTwoCasesIndependentUsTest(r, size * 2))) mkString("\n")
  val theirBody = ranges.take(4).map(r => check(r, generateTwoCasesIndependentRxJavaTest(r, size * 2))) mkString("\n")
  assembleTest("twoCasesIndependend", ourBody, theirBody)
}

def NCasesTwoIndependentOut = {
  implicit val xaxis = UsingArguments("nCasesTwoIndependentOutRange", "nChoices")
  val ourBody = ranges.map(r => check(r, generateNCasesTwoIndependentUsTest(r, size * r))) mkString("\n")
  val theirBody = ranges.map(r => check(r, generateNCasesTwoIndependentRxJavaTest(r, size * r))) mkString("\n")
  assembleTest("NCaseTwoIndependent", ourBody, theirBody)
}

def NDependentCasesOut = {
  val cases = 32
  implicit val xaxis = UsingArguments("NDependentCasesOutRange", "nDependent")
  val ourBody = ranges.map(r => check(r, generateNDependentCasesUsTest(cases, r, size * cases))) mkString("\n")
  val theirBody = ranges.map(r => check(r, generateNDependentCasesRxJavaTest(cases, r, size * cases))) mkString("\n")
  assembleTest("NDependentCases", ourBody, theirBody, false)
}

// $twoCasesNObservablesOut
// $NCasesTwoIndependentOut
// $NDependentCasesOut
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

object Benchmarks extends PerformanceTest {

 lazy val executor = SeparateJvmsExecutor(
    new Executor.Warmer.Default,
    Aggregator.average,
    new Measurer.Default)
  lazy val reporter = MyDsvReporter(',')

  lazy val persistor = Persistor.None

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

  val twoCasesNObservablesOutRange = Gen.enumeration("Observables")(2, 4, 8, 16, 32)
  val nCasesTwoIndependentOutRange = Gen.enumeration("Choices")(2, 4, 8, 16, 32)
  val NDependentCasesOutRange = Gen.enumeration("Choices")(2, 4, 8, 16, 32)

  $twoCasesNObservablesOut
  $NCasesTwoIndependentOut
  $NDependentCasesOut
}"""
println(out)
