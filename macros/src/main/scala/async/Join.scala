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
  }
  object Pattern {
    def apply[A](o: Observable[A]) = new Pattern(o)
  }
  object && {
    def unapply(obj: Any): Option[(Pattern[_] , Pattern[_])] = ???
  }
  object ||  {
    def unapply(obj: Any): Option[(Pattern[_], Pattern[_])] = ???
  }
  implicit class ObservableJoinOps[A](o: Observable[A]) {
    def p: Pattern[A] = Pattern(o)
  }
  /* The macros implementing the transform */

  def async[A](body: =>Any): Observable[A] = macro asyncImpl[A]

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

    // Tree -> List[List[Tree]]
    val treePattern = rawPatternTrees.map { p => getPatternObjects(p) }

    // If we knew that flatten is deterministic we could be less verbose here...

    // List[List[Tree]] -> List[List[(Symbol, Tree)]]
    val symbolTreePattern = treePattern.map { ts => ts.map { t => (t.symbol, t) } }

    // List[List[(Symbol, Tree)] -> List[(Symbol, Tree)] -> Map[Symbol, Tree]
    val symbolsToTrees = symbolTreePattern.flatten.toMap

    // Map[Symbol, Tree] -> Map[(Symbol, Tree), Int] -> Map[Symbol, Int]
    val symbolsToIds = symbolsToTrees.zipWithIndex
                         .map { case ((s, _), i) => (s, 1 << i) }

    // List[List[(Symbol, Tree)]] -> List[List[Symbol]]
    val symbolPattern = symbolTreePattern.map { ps => ps.map { case (s, t) => s } }

    // List[List[Symbol]] -> List[(List[Int], List[Symbol])] -> List[(Int, List[Symbol])]
    val patterns = symbolPattern
                       .map { ss => (ss.map {s => symbolsToIds.get(s).get}, ss) }
                       .map { case (ids, ss) => ( ids.foldLeft(0){ (acc, i) => acc | i}, ss)}
    
    // val tpe = weakTypeOf[A]
    // import rx.functions.Action1


    /*
     observables.map { ? =>
       q"""
        ${obs.tree}.subscribe { next => 
            $stateLockVal.aquire()
            val $possibleStateVal = $stateVar | ${obs.index}  

            // for every pattern in which obs appears
            {$patterns.filter(foreach{ pattern =>
                if ((~possibleState & pattern.Id) == 0)
            }}

        }
       """
    */
    val stateVar = TermName(c.freshName("state"))
    val stateLockVal = TermName(c.freshName("stateLock"))
    // TODO: Hygiene
    val out = q"""
      var $stateVar = 0
      val $stateLockVal = new scala.concurrent.Lock()
      0
    """
    // println(out)
    out
  }
}

