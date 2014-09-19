package scala.async.internal

trait Parse {
  self: JoinMacro =>
  import c.universe._
  import scala.async.Join.{JoinReturn, Next => ReturnNext, Done => ReturnDone, Pass => ReturnPass}

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
  private def transformToPatternTree(caseDefTree: Tree): (PatternTree, Map[Event, Symbol]) = {
    caseDefTree match {
      // Binary operators: &&, and ||
      case pq"$ref(..$pats)" if pats.size == 2 => 
        val (left, leftBindings) = transformToPatternTree(pats(0))
        val (right, rightBindings) = transformToPatternTree(pats(1))
        val combinedBindings = leftBindings ++ rightBindings
        // TODO: Find better way of distinguishing && and ||
        ref.symbol.typeSignature.toString match {
          case tpe if tpe.contains("&&.type") => (And(left, right), combinedBindings)
          case tpe if tpe.contains("||.type") => (Or(left, right), combinedBindings)
        }
      // Unary operators: Next, Error, and NextFilter (e.g. case Obs(1))
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
      // Done
      case pq"$ref" => ref match {
        case Select(obs @ _, TermName("done")) => (Done(obs.symbol), Map[Event, Symbol]())
      }
    }
  }

  // Extracts events from PatternTrees
  private def extractEvents(patternTree: PatternTree): Set[Event] = patternTree match {
    case And(left, right) => extractEvents(left) ++ extractEvents(right)
    case Or(left, right) => extractEvents(left) ++ extractEvents(right)
    case event: Event => Set(event)
  }

  // Collects unique events across all patterns. (The same event might be used in multiple patterns.)
  def uniqueEvents(patterns: Set[Pattern]): Set[Event] = patterns.flatMap({ case Pattern(events, _, _, _) => events }).toSet

  def parse(pf: c.Tree): Set[Pattern] = {
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

 // Parse a pattern-body for the action the user wants to perform, also returns the other statement to be executed
 // before the JoinReturn action, which is only Next, Done, or Pass
 def parsePatternBody(patternBody: c.Tree): (JoinReturn[c.Tree], List[c.Tree]) = patternBody match {
  case Block(stats, lastExpr) => (parseReturnStatement(lastExpr), stats)
  case Apply(Select(_, TermName("unitToPass")), stats) => (ReturnPass, stats)
  // ^ matches the implicit conversion from Unit to Pass
  case _ => (parseReturnStatement(patternBody), List(EmptyTree))
 }

  // Returns a representation of what the user wanted us to do in a pattern body. If it's a next, then the expression
  // of what a user wanted to send (e.g. the x in Next(x)) is returned as JoinReturn[c.Tree].
  private def parseReturnStatement(statement: c.Tree): JoinReturn[c.Tree] = statement match {
    case Apply(TypeApply(Select(Select(_, TermName("Next")), TermName("apply")), _), stats) => ReturnNext(stats.head)
    case Select(_, TermName("Done")) => ReturnDone
    case Select(_, TermName("Pass")) => ReturnPass
    case other =>  
      c.error(c.enclosingPosition, s"Join pattern has to return a value of type JoinReturn: Next(...), Done, or Pass. Got: $other")
      ReturnPass
 }

}