package scala.async

import language.experimental.macros
import scala.reflect.macros.blackbox
import scala.language.implicitConversions
import rx.Observable
import rx.functions.Action1

object Join {
  /* Code related for enabling our syntax in partial functions */
  class PatternAPI[A](val observable: Observable[A]) {
    def unapply(x: Any): Option[A] = ???
    object error {
      def unapply(x: Any): Option[A] = ???
    }
    case object done extends PatternAPI(observable)
  }

  object PatternAPI {
    def apply[A](o: Observable[A]) = new PatternAPI(o)
  }

  object && {
    def unapply(obj: Any): Option[(PatternAPI[_], PatternAPI[_])] = ???
  }

  object || {
    def unapply(obj: Any): Option[(PatternAPI[_], PatternAPI[_])] = ???
  }

  implicit class ObservableJoinOps[A](o: Observable[A]) {
    def p: PatternAPI[A] = PatternAPI(o)
  }

  /* The macros implementing the transform */

  // Code review: should we abstract over the following two?

  // class ThrowableBuffer(val identifier: TermName, val declaration: Tree)
  // class Queue(val identifier: TermName, val declaration: Tree)
  // // object Queue {
  // //   def apply[A](prefix: Any) = new Queue(
  // //     TermName(c.freshName(s"${prefix.toString}_queue")),
  // //     q"val $name = mutable.Queue[$A]()")
  // // }

  // trait Event {
  //   def source: Symbol
  //   def patterns: List[Pattern]
  // }

  // val eventsToIds: Map[Event, Long]
  // val eventsToQueues: Map[Next[Any], Queue[Any]]
  // val eventsToBuffers: Map[Error, ThrowableBuffer]
  // val patternsToIds: Map[Pattern, Long]

  // YOU WERE (ALSO) HERE: two problems
  // - Is it possible to have the same observable twice in the same pattern? NO? Why not?
  // - Extra buffers for the done? No, we can just store it in the id.
  // - The error message needs to be buffered! E.g. if o2 fires first in: o1.error(e) && o2(x) =>
  // - Not giving the observables an id means that we cannot allow things like o3(x) && o3.error(x) - anyway: what does o(x) && o(x) supposed to mean anyway? (x,x)?
  // - We will also need to replace the throwable in case of an error.
  // - Do we also need to rescan after a successful match? 
  // -> Maybe write it in the manual transform first?

  def join[A](pf: PartialFunction[PatternAPI[_], A]): A = macro joinImpl[A]

  def joinImpl[A: c.WeakTypeTag](c: blackbox.Context)(pf: c.Tree): c.Tree = {
    import c.universe._

    /* TODO: Figure out how to put this somewhere else */
    sealed trait PatternTree
    sealed trait BinaryOperator extends PatternTree
    case class And(left: PatternTree, right: PatternTree) extends BinaryOperator
    case class Or(left: PatternTree, right: PatternTree) extends BinaryOperator
    sealed trait Event extends PatternTree {
      def source: Symbol
    }
    case class Next(source: Symbol) extends Event
    case class NextFilter(source: Symbol, filter: Constant) extends Event
    case class Error(source: Symbol) extends Event
    case class Done(source: Symbol) extends Event

    def transformToPatternTree(tree: Tree): (PatternTree, Map[Event, Symbol]) = {
      tree match {
        case pq"$ref(..$pats)" if pats.size == 2 => 
          val (left, leftBindings) = transformToPatternTree(pats(0))
          val (right, rightBindings) = transformToPatternTree(pats(1))
          val combinedBindings = leftBindings ++ rightBindings
          // TODO: Find better way of distinguishing && and ||
          ref.symbol.typeSignature.toString match {
            case t if t.contains("&&.type") => (And(left, right), combinedBindings)
            case t if t.contains("||.type") => (Or(left, right), combinedBindings)
          }
        case pq"$ref(..$pats)" if pats.size == 1 => pats.head match {
          case b @ Bind(_, _) => ref match {
            case Select(s @ _, TermName("error")) => 
              val error  = Error(s.symbol)
              (error, Map(error -> b.symbol))
            case _ => 
              val next = Next(ref.symbol)
              (next, Map(next -> b.symbol))
          }
          case Literal(c @ Constant(_)) => (NextFilter(ref.symbol, c), Map[Event, Symbol]())
        }
        case pq"$ref" => ref match {
          case Select(obs @ Select(_, _), TermName("done")) => (Done(obs.symbol), Map[Event, Symbol]())
        }
      } 
    }

    // def semanticAnalysis(patternTree: PatternTree): Boolean = ???

    // def orElimination(patternTree: PatternTree): PatternTree = ???

    // TODO: How to report errors? Expects patternTree to contain no or nodes
    // case Or(_, _) => // TODO: Error-state
    def extractEvents(patternTree: PatternTree): Set[Event] = patternTree match {
      case And(l , r) => extractEvents(l) ++ extractEvents(r)
      case other: Event => Set(other)
    }

    case class Pattern(events: Set[Event], bindings: Map[Event, Symbol], bodyTree: Tree, guardTree: Tree)

    val q"{ case ..$cases }" = pf
    
    val patterns = cases map(c => { 
      val (patternTree, bindings) = transformToPatternTree(c.pat)
      val events = extractEvents(patternTree)
      Pattern(events, bindings, c.body, c.guard)
    })
  
    val allEvents = (patterns flatMap { case Pattern(es, _, _, _) => es }).toSet
    println(allEvents)
    
    // val observablesToEvents = observables map(s => s -> (allEvents filter(e => e.source == s)))
    // can be created. Need to create ids for the events and patterns.
/*

    // For every observable create a unique id, and create a map from the symbol to the id
    val symbolsToIds = symbolsToTrees.zipWithIndex
      .map { case ((s, _), i) => (s, 1 << i) }

    // Create a representation of patterns with observable-symbols
    val symbolPattern = symbolTreePattern.map { ps => ps.map { case (s, t) => s } }

    // Create for every pattern its id by "or"-ing together the ids of all involved observables
    // Regarding the zips: I hope the list combinators keep the order of the lists
    val patterns = symbolPattern
      .map { ss => (ss.map { s => symbolsToIds.get(s).get }, ss) } // get the ids of the observables
      .map { case (ids, ss) => (ids.foldLeft(0) { (acc, i) => acc | i }, ss) } // calculate pattern-id from the ids
      .zip { cases.map(c => c.body) } // add the body of the pattern to the pattern
      .zip(symbolsToBindTree) // add the mapping from observables to their pattern-bind-variables
      .map { case (((pid, obs), body), pVars) => pid -> (obs, body, pVars.toMap) } // tidy up a bit

    def getFirstTypeArgument(sym: Symbol) = {
      val NullaryMethodType(tpe) = sym.typeSignature
      val TypeRef(_, _, obsTpe :: Nil) = tpe
      obsTpe
    }

    val symbolstoQueues = symbolsToIds.map {
      case (sym, id) => sym -> TermName(c.freshName(s"obs${id}_queue"))
    }

    val queueDeclarations = symbolstoQueues.map {
      case (sym, name) =>
        val obsTpe = getFirstTypeArgument(sym)
        q"val $name = mutable.Queue[$obsTpe]()"
    }

    val stateVar = TermName(c.freshName("state"))
    val stateLockVal = TermName(c.freshName("stateLock"))

    def generatePatternChecks(nextMessage: Tree, obsSym: Symbol, possibleStateVal: TermName) = {
      val ownPatterns = patterns.filter { case (_, (syms, _, _)) => syms.exists { s => s == obsSym } }
      ownPatterns.map {
        case (pid, (syms, body, pVars)) => {
          val others = syms.filter { s => s != obsSym }
          val dequedMessageVals = others.map { sym => sym -> TermName(c.freshName("message")) }
          val dequedMessageValsDecl = dequedMessageVals.map {
            case (sym, name) =>
              val queue = symbolstoQueues.get(sym).get
              // We return a pair of trees because if written toghether a block is created which
              // is not removed later when using ..$. The problem then is that the dequeued vars are
              // in the block scope and cannot be accessed outside of it.
              (q"val $name = $queue.dequeue()",
                q""" if ($queue.isEmpty) {
                        $stateVar = $stateVar & ~${symbolsToIds.get(sym).get}
                     }""")
          }
          // The following code uses the dequeued items to execute the pattern-body: we replace
          // all occurences of the pattern "bind variables" (like the "x" in "O(x)) with the symbol
          // of the value holding the dequeued messages. We use the compiler internal symbol table,
          // and thus have to cast all trees, and symbols in the internal types
          val symtable = c.universe.asInstanceOf[scala.reflect.internal.SymbolTable]
          val ids = dequedMessageVals.map { case (_, name) => Ident(name).asInstanceOf[symtable.Tree] }
          val symsToReplace = dequedMessageVals.map { case (sym, _) => pVars.get(sym).get.head.symbol.asInstanceOf[symtable.Symbol] }
          val nextMessageTree = nextMessage.asInstanceOf[symtable.Tree]
          val nextMessageSymbol = pVars.get(obsSym).get.head.symbol.asInstanceOf[symtable.Symbol]
          val dequeuedMessageSubstituter = new symtable.TreeSubstituter(nextMessageSymbol :: symsToReplace, nextMessageTree :: ids)
          val transformedBody = dequeuedMessageSubstituter.transform(body.asInstanceOf[symtable.Tree])
          val checkedTransformedBody = c.untypecheck(transformedBody.asInstanceOf[c.universe.Tree])
          q"""
          if ((~$possibleStateVal & $pid) == 0) {
            ..${dequedMessageValsDecl.map(p => p._1)}
            ..${dequedMessageValsDecl.map(p => p._2)}
            $stateLockVal.release()
            println("calculated result: " + $checkedTransformedBody)
            break
          }
          """
        }
      }
    }
    val subscriptions = symbolsToTrees.map {
      case (obsSym, tree) => {
        val possibleStateVal = TermName(c.freshName("possibleStateVal"))
        val nextMessage = Ident(TermName(c.freshName("nextMessage")))
        val obsTpe = getFirstTypeArgument(obsSym)
        q"""
        $tree.observable.subscribe { new _root_.rx.functions.Action1[$obsTpe] {
            def call($nextMessage: $obsTpe) = {
              $stateLockVal.acquire()
              val $possibleStateVal = $stateVar | ${symbolsToIds.get(obsSym).get}
              breakable {
                ..${generatePatternChecks(nextMessage, obsSym, possibleStateVal)}
                // reaching here means no pattern has matched:
                ${symbolstoQueues.get(obsSym).get}.enqueue($nextMessage)
                $stateVar = $possibleStateVal
                $stateLockVal.release()
              }
            }
          }
        }
      """
      }
    }
    val out = q"""
      import _root_.scala.util.control.Breaks._
      import _root_.scala.collection.mutable
      // state handling
      var $stateVar = 0
      val $stateLockVal = new _root_.scala.concurrent.Lock()
      ..$queueDeclarations     
      // declare the observable subscriptions
      ..$subscriptions
      0
    """
    // println(out)
    out
    */
    val out = q"""
    0
    """
    println(out)
    out
  }
}
