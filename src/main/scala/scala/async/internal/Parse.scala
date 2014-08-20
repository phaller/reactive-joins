package scala.async.internal

trait Parse {
  self: JoinMacro =>
  import c.universe._

  // Abstract Syntax Trees for the partifal-function join-syntax
  sealed trait PatternTree
  sealed trait BinaryOperator extends PatternTree
  case class And(left: PatternTree, right: PatternTree) extends BinaryOperator
  case class Or(left: PatternTree, right: PatternTree) extends BinaryOperator
  sealed trait Event extends PatternTree {
    def source: c.Symbol
  }
  case class Next(source: c.Symbol) extends Event
  case class NextFilter(source: c.Symbol, filter: c.universe.Constant) extends Event
  case class Error(source: c.Symbol) extends Event
  case class Done(source: c.Symbol) extends Event

  // Convenient representation of a single pattern. Do not subclass without adapting 
  // "equals" accordingly. Decorating the method with "final" causes outer-class 
  // typecheck warnings, and therefore was omitted.
  case class Pattern(events: Set[Event], bindings: Map[Event, c.Symbol], bodyTree: c.Tree, guardTree: c.Tree) {
    override def equals(other: Any): Boolean = other match {
      case that: Pattern => 
        that.events == this.events && that.guardTree.equalsStructure(this.guardTree)
      case _ => false
    }
    override def hashCode: Int = 41 * (41 + events.hashCode) + guardTree.hashCode
  }

  // Transforms a CaseDef-Tree into a PatternTree, and additionaly returns 
  // a mapping from Events to the Symbol of their variable bindings (e.g. 
  // the "x" in "case Obs(x) => ...").
  def transformToPatternTree(caseDefTree: Tree): (PatternTree, Map[Event, Symbol]) = {
    caseDefTree match {
      case pq"$ref(..$pats)" if pats.size == 2 => 
        val (left, leftBindings) = transformToPatternTree(pats(0))
        val (right, rightBindings) = transformToPatternTree(pats(1))
        val combinedBindings = leftBindings ++ rightBindings
        // TODO: Find better way of distinguishing && and ||
        ref.symbol.typeSignature.toString match {
          case tpe if tpe.contains("&&.type") => (And(left, right), combinedBindings)
          case tpe if tpe.contains("||.type") => (Or(left, right), combinedBindings)
        }
      case pq"$ref(..$pats)" if pats.size == 1 => pats.head match {
        case patternVar @ Bind(_, _) => ref match {
          case Select(obs @ _, TermName("error")) => 
            val error  = Error(obs.symbol)
            (error, Map(error -> patternVar.symbol))
          case _ => 
            val next = Next(ref.symbol)
            (next, Map(next -> patternVar.symbol))
        }
        case Literal(const @ Constant(_)) => (NextFilter(ref.symbol, const), Map[Event, Symbol]())
      }
      case pq"$ref" => ref match {
        case Select(obs @ _, TermName("done")) => (Done(obs.symbol), Map[Event, Symbol]())
      }
    }
  }

  def extractEvents(patternTree: PatternTree): Set[Event] = patternTree match {
    case And(left, right) => extractEvents(left) ++ extractEvents(right)
    case Or(left, right) => extractEvents(left) ++ extractEvents(right)
    case event: Event => Set(event)
  }

  def parse[A](pf: c.Tree): Set[Pattern] = {
    val q"{ case ..$cases }" = pf

    val definedPatterns: List[Pattern] = cases.map(caze => { 
      val (patternTree, bindings) = transformToPatternTree(caze.pat)
      val events = extractEvents(patternTree)
      Pattern(events, bindings, caze.body, caze.guard)
    })

    val patterns: Set[Pattern] = definedPatterns.toSet

    if (definedPatterns.size != patterns.size) {
      val doublicates = definedPatterns.groupBy(identity).filter(p => p._2.size > 1).keys
      c.warning(c.enclosingPosition, s"Found doublicate patterns: ${doublicates.mkString(",")}")
    }
    patterns
  }
}