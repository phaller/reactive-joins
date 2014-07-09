package scala.async

import language.experimental.macros
import scala.reflect.macros.blackbox
import scala.language.implicitConversions
import rx.Observable
import rx.functions.Action1

object Join {
  /* Code related for enabling our syntax in partial functions */
  class Pattern[A](val observable: Observable[A]) {
    def unapply(x: Any): Option[A] = ???
    object error {
      def unapply(x: Any): Option[A] = ???
    }
  }

  object Pattern {
    def apply[A](o: Observable[A]) = new Pattern(o)
  }

  object && {
    def unapply(obj: Any): Option[(Pattern[_], Pattern[_])] = ???
  }

  object || {
    def unapply(obj: Any): Option[(Pattern[_], Pattern[_])] = ???
  }

  implicit class ObservableJoinOps[A](o: Observable[A]) {
    def p: Pattern[A] = Pattern(o)
  }

  /* The macros implementing the transform */
  def async[A](body: => Any): Observable[A] = macro asyncImpl[A]

  def asyncImpl[A: c.WeakTypeTag](c: blackbox.Context)(body: c.Tree): c.Tree = {
    import c.universe._
    q""
  }

  // case class ObservableInfo(id: Int,
  //                           symbol: Symbol
  //                           patterns: List[PatternInfo],
  //                           queue: Queue
  //                           messageType: Type,
  //                           dequeuedMessage: messageType,
  //                           )  
  //
  // case class PatternInfo(pattern: Tree, 
  //                        body: Tree,
  //                        observables: List[ObservableInfo],
  //                        patternVariables: Map[Symbol, Tree]
  //                        id: Int
  //                        condition: Tree)   

  def join[A](pf: PartialFunction[Pattern[_], A]): A = macro joinImpl[A]

  def joinImpl[A: c.WeakTypeTag](c: blackbox.Context)(pf: c.Tree): c.Tree = {
    import c.universe._
    /* Functionality of join:
        Analysis:
        1. Dead-code elimination (-> How to throw errors?)
        2. Determinism check
        3. Disallow all pattern-matching features which are not supported, like "|"
        Functionality:
        Transform
        unsubscribe?
    */
    val q"{ case ..$cases }" = pf
    val rawPatternTrees = cases map { c => c.pat }

    def getPatternObjects(t: Tree): List[(Tree, List[Tree])] = {
      var patternObjects = List[(Tree, List[Tree])]()
      def recPat(t: Tree): Unit = t match {
        case pq"$ref(..$pats)" => {
          if (!ref.symbol.isModule) {
            patternObjects = (ref, pats) :: patternObjects
          }
          pats.foreach(p => recPat(p))
        }
        case _ =>
      }
      recPat(t)
      patternObjects
    }

    // Create a List of the trees representing the patterns
    val treePattern = rawPatternTrees.map { p => getPatternObjects(p) }

    // Look up the symbols of the observables involved in the patterns
    val symbolTreePattern = treePattern.map(p => p.map(_._1)).map { ts => ts.map { t => (t.symbol, t) } }

    // Look up for the symbols of the pattern-variables, like the "x" in "case p(x)"
    val symbolsToBindTree = treePattern.map { p => p.map { case (t, pats) => t.symbol -> pats } }

    // Create a map from observable symbols to their tree representation
    val symbolsToTrees = symbolTreePattern.flatten.toMap

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
    println(out)
    out
  }
}
