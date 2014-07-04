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

  def join[A](pf: PartialFunction[Pattern[_], A]): A = macro joinImpl[A]

  def joinImpl[A: c.WeakTypeTag](c: blackbox.Context)(pf: c.Tree): c.Tree = {
    import c.universe._
    /* Functionality of join:
        Analysis:
        1. Dead-code elimination (-> How to throw errors?)
        2. Determinism check
        Functionality:
        3. Transform
    */
    val q"{ case ..$cases }" = pf
    val bodies = cases map { c => c.body }
    val rawPatternTrees = cases map { c => c.pat }

    // TODO: This probably stinks...
    def getPatternObjects(t: Tree): List[Tree] = {
      var patternObjects = List[Tree]()
      def recPat(t: Tree): Unit = t match {
        case pq"$ref(..$pats)" => {
          if (!ref.symbol.isModule) {
            patternObjects = ref :: patternObjects
          }
          pats.foreach(p => recPat(p))
        }
        case _ =>
      }
      recPat(t)
      patternObjects
    }

    // Create a List of the trees representing the patterns
    // Tree -> List[List[Tree]]
    val treePattern = rawPatternTrees.map { p => getPatternObjects(p) }

    // Look up the symbols of the observables involved in the patterns
    // List[List[Tree]] -> List[List[(Symbol, Tree)]]
    val symbolTreePattern = treePattern.map { ts => ts.map { t => (t.symbol, t) } }

    // Create a map from observable symbols to their tree representation
    // List[List[(Symbol, Tree)] -> List[(Symbol, Tree)] -> Map[Symbol, Tree]
    val symbolsToTrees = symbolTreePattern.flatten.toMap

    // For every observable create a unique id, and create a map from the symbol to the id
    // Map[Symbol, Tree] -> Map[(Symbol, Tree), Int] -> Map[Symbol, Int]
    val symbolsToIds = symbolsToTrees.zipWithIndex
      .map { case ((s, _), i) => (s, 1 << i) }

    // Create a representation of patterns with observable-symbols
    // List[List[(Symbol, Tree)]] -> List[List[Symbol]]
    val symbolPattern = symbolTreePattern.map { ps => ps.map { case (s, t) => s } }

    // Create for every pattern its id by "or"-ing together the ids of all involved observables
    // List[List[Symbol]] -> List[(List[Int], List[Symbol])] -> List[(Int, List[Symbol])]
    val patterns = symbolPattern
      .map { ss => (ss.map { s => symbolsToIds.get(s).get }, ss) }
      .map { case (ids, ss) => (ids.foldLeft(0) { (acc, i) => acc | i }, ss) }

    val tpe = weakTypeOf[A]

    val stateVar = TermName(c.freshName("state"))
    val stateLockVal = TermName(c.freshName("stateLock"))

    // TODO: be more functional somehow?
    // TODO: lock-handling is a nasty business...
    // TODO: what does every symbol have? Maybe make it *one* map.
    // TODO: code clean up, somehow.

    val symbolstoQueues = symbolsToIds.map {
      case (sym, id) => sym -> TermName(c.freshName(s"obs${id}_queue"))
    }

    // TODO: Replace Int here with the observable type
    val queueDeclarations = symbolstoQueues.map { case (sym, name) =>
          q"""
            val $name = mutable.Queue[Int]()
          """
      }

    def generatePatternChecks(obsSym: Symbol, possibleStateVal: TermName) = {
      val ownPatterns = patterns.filter { case (pid, syms) => syms.exists { s => s == obsSym } }
      ownPatterns.map {
        case (pid, syms) => {
          val others = syms.filter { s => s != obsSym }
          val dequedMessageVals = others.map { sym => sym -> TermName(c.freshName("message")) }
          val dequedMessageValsDecl = dequedMessageVals.map { case (sym, name) => 
            val queue = symbolstoQueues.get(sym).get
            // We return a pair of trees because if written toghether a block is created which
            // is not removed later when using ..$. The problem then is that the dequeued vars are
            // in the block scope and cannot be accessed outside of it.
            (q"val $name = $queue.dequeue()", 
            q"""
            if ($queue.isEmpty) {
              $stateVar = $stateVar & ~${symbolsToIds.get(sym).get}
            }""")
          }
          // The following code uses the dequeued items to execute the pattern-body: we replace
          // all occurences of the pattern "bind variables" (like the "x" in "O(x)) with the symbol
          // of the value holding the dequeued messages. We use the compiler internal symbol table,
          // and thus have to cast all trees, and symbols in the internal types.
          val symtable = c.universe.asInstanceOf[scala.reflect.internal.SymbolTable]
          val ids = dequedMessageVals.map { case (_, name) => Ident(name).asInstanceOf[symtable.Tree]}
          val symsToReplace = dequedMessageVals.map { case (sym, _) => sym.asInstanceOf[symtable.Symbol]}
          val substituter = new symtable.TreeSubstituter(symsToReplace, ids)
          val transformedBody = substituter.transform(body.asInstanceOf[symtable.Tree])
          val checkedTransformedBody = c.resetLocalAttrs(transformedBody.asInstanceOf[c.universe.Tree])
          q"""
          if ((~$possibleStateVal & $pid) == 0) {
            ..${dequedMessageValsDecl.map(p => p._1)}
            ..${dequedMessageValsDecl.map(p => p._2)}
            $checkedTransformedBody
            $stateLockVal.release()
            break
          }
          """
        }
      }
    }
    // TODO: how to get the types of the observables?
    // TODO: Fix the wrong $tpe with the actual observable type
    val subscriptions = symbolsToTrees.map {
      case (obsSym, tree) => {
        val possibleStateVal = TermName(c.freshName("possibleStateVal"))
        q"""
        $tree.observable.subscribe { new _root_.rx.functions.Action1[$tpe] {
            def call(next: $tpe) = {
              $stateLockVal.acquire()
              val $possibleStateVal = $stateVar | ${symbolsToIds.get(obsSym).get}
              breakable {
                ..${generatePatternChecks(obsSym, possibleStateVal)}
                // reaching here means no pattern has matched:
                ${symbolstoQueues.get(obsSym).get}.enqueue(next)
                println(${symbolstoQueues.get(obsSym).get})
                $stateVar = $possibleStateVal
                $stateLockVal.release()
              }
            }
          }
        }
      """
      }
    }
    // TODO: Maybe find a better way than breaks? if-else generation?
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
