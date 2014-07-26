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
          case Select(obs @ Select(_, _), TermName("done")) => (Done(obs.symbol), Map[Event, Symbol]())
        }
      }
    }

    val q"{ case ..$cases }" = pf
    
    final case class Pattern(events: Set[Event], bindings: Map[Event, Symbol], bodyTree: Tree, guardTree: Tree) {
        override def equals(other: Any): Boolean = other match {
          case that: Pattern => 
            that.events == this.events && that.guardTree.equalsStructure(this.guardTree)
          case _ => false
        }
        override def hashCode: Int = 41 * (41 + events.hashCode) + guardTree.hashCode
    }

    // TODO: How to report errors? Expects patternTree to contain no or nodes
    // case Or(_, _) => // TODO: Error-state
    def extractEvents(patternTree: PatternTree): Set[Event] = patternTree match {
      case And(left , right) => extractEvents(left) ++ extractEvents(right)
      case other: Event => Set(other)
    }

    val definedPatterns: List[Pattern] = cases.map(caze => { 
      val (patternTree, bindings) = transformToPatternTree(caze.pat)
      val events = extractEvents(patternTree)
      Pattern(events, bindings, caze.body, caze.guard)
    })

    val patterns: Set[Pattern] = definedPatterns.toSet

    if (definedPatterns.size != patterns.size) {
      // TODO: Warning, doublicate patterns!
    }

    // Collect events across all patterns, remove duplicates, and assign to each a unique id
    val events: Set[Event] = patterns.flatMap({ case Pattern(events, _, _, _) => events })
                                     .toSet
    val eventsToIds: Map[Event, Long] = events.zipWithIndex
                                              .map({ case (event, index) => (event, 1L << index) })
                                              .toMap
    // Calculate pattern ids by "or"-ing the ids of their events
    def accumulateEventId(acc: Long, event: Event) = acc | eventsToIds.get(event).get
    val patternsToIds: Map[Pattern, Long] = 
      patterns.map(p => p -> p.events.foldLeft(0L)(accumulateEventId)).toMap

    def getFirstTypeArgument(sym: Symbol) = {
      val NullaryMethodType(tpe) = sym.typeSignature
      val TypeRef(_, _, obsTpe :: Nil) = tpe
      obsTpe
    }

    case class NewDecl(name: TermName, declaration: Tree)

    val nextEventsToQueues = 
      events.collect({ case event: Next => event })
            .map(event => {
                  val obsTpe = getFirstTypeArgument(event.source)
                  val queueName = TermName(c.freshName("eventQueue"))
                  val queueDeclaration = q"val $queueName = mutable.Queue[$obsTpe]()"
                  event -> NewDecl(queueName, queueDeclaration)})
            .toMap

    val errorEventsToVars = events.collect({ case event: Error => event })
                                  .map(event => {
                                    val throwableVar = TermName(c.freshName("error"))
                                    val declaration = q"var $throwableVar: Throwable = null" 
                                    event -> NewDecl(throwableVar, declaration)})
                                  .toMap

    val stateVar = TermName(c.freshName("state"))
    val stateLockVal = TermName(c.freshName("stateLock"))

    val subscriptions = events.toList.map(subscribeEvent => subscribeEvent -> ((nextMessage: Option[TermName]) => { 
          val possibleStateVal = TermName(c.freshName("possibleState"))
          val myPatterns = patterns.filter(pattern => pattern.events.contains(subscribeEvent))
          // Generate the if-expressions which check whether a pattern has matched
          val patternChecks = myPatterns.toList.map(myPattern => {
            val otherEvents = myPattern.events.toList.filter(event => event != subscribeEvent)
            val dequeueBuffers = otherEvents.collect({ case event: Next => event })
                                            .map(event => event -> TermName(c.freshName("dequeuedMessage")))
            val dequeueStats = dequeueBuffers.map({ case (event, name) => 
              val queue = nextEventsToQueues.get(event).get.name
              (q"val $name = ${queue}.dequeue()",
              q""" 
              if ($queue.isEmpty) {
                $stateVar = $stateVar & ~${eventsToIds.get(event).get}
              }""")
            })
            val errorVars = otherEvents.collect({ case event: Error => event})
                                       .map(event => event -> errorEventsToVars.get(event).get.name)
            // The following code uses the dequeued items to execute the pattern-body: we replace
            // all occurences of the pattern "bind variables" (like the "x" in "O(x)) with the symbol
            // of the value holding the dequeued messages. We use the compiler internal symbol table,
            // and thus have to cast all trees, and symbols in the internal types
            val symtable = c.universe.asInstanceOf[scala.reflect.internal.SymbolTable]
            val combinedEvents = dequeueBuffers ++ errorVars
            var symsToReplace = combinedEvents.map({ case (event, _) => myPattern.bindings.get(event).get.asInstanceOf[symtable.Symbol]}) 
            var ids = combinedEvents.map({ case (_, name) => Ident(name).asInstanceOf[symtable.Tree]})
            // If the occurred event includes a message, we also need to replace it in the body!
            if (nextMessage.nonEmpty) {
              symsToReplace = myPattern.bindings.get(subscribeEvent).get.asInstanceOf[symtable.Symbol] :: symsToReplace
              ids = Ident(nextMessage.get).asInstanceOf[symtable.Tree] :: ids
            }
            val substituter = new symtable.TreeSubstituter(symsToReplace, ids)
            val transformedBody = substituter.transform(myPattern.bodyTree.asInstanceOf[symtable.Tree])
            val checkedTransformedBody = c.untypecheck(transformedBody.asInstanceOf[c.universe.Tree]) 
            q"""
            if ((~$possibleStateVal & ${patternsToIds.get(myPattern).get}) == 0) {
              ..${dequeueStats.map({ case (dequeueStats, _) => dequeueStats })}
              ..${dequeueStats.map({ case (_, statusStats) => statusStats })}
              $stateLockVal.release()
              println("calculated result: " + $checkedTransformedBody)
              break
            }
            """
          })
          val bufferStat = subscribeEvent match {
            case next @ Next(_) => q"${nextEventsToQueues.get(next).get.name}.enqueue(${nextMessage.get})"
            case error @ Error(_) => q"${errorEventsToVars.get(error).get.name} = ${nextMessage.get}"
            case _ => q""
          }
          q"""
            $stateLockVal.acquire()
            val $possibleStateVal = $stateVar | ${eventsToIds.get(subscribeEvent).get}
            breakable {
              ..$patternChecks
              // reaching here means no pattern has matched:
              $bufferStat
              $stateVar = $possibleStateVal
              $stateLockVal.release()
            }
          """
    }))

    val observalbesToEvents = subscriptions.groupBy({ case (subscribeEvent, _) => subscribeEvent.source }) 

    val subscribeActions = observalbesToEvents.map({ case (obsSym, events) => 
      val obsTpe = getFirstTypeArgument(obsSym)
      val nextSubscription = 
        events.filter(event => event._1.isInstanceOf[Next]).map({ case (next, patternChecks) => 
          val nextMessage = TermName(c.freshName("nextMessage"))
          val obsTpe = getFirstTypeArgument(obsSym)
          q"""
          new _root_.rx.functions.Action1[$obsTpe] {
            def call($nextMessage: $obsTpe) = {
            ..${patternChecks(Some(nextMessage))} 
            }
          }
          """
        })
      val errorSubscription = 
        events.filter(event => event._1.isInstanceOf[Error]).map({ case (next, patternChecks) => 
          val nextMessage = TermName(c.freshName("nextMessage"))
          q"""
          new _root_.rx.functions.Action1[Throwable] {
            def call($nextMessage: Throwable) = {
            ..${patternChecks(Some(nextMessage))} 
            }
          }
          """
      })
      val doneSubscription = 
        events.filter(event => event._1.isInstanceOf[Done]).map({ case (next, patternChecks) => 
        q"""
          new _root_.rx.functions.Action0 {
            def call() = {
            ..${patternChecks(None)} 
            }
          }
          """
        })
      val emptyAction1 = q"new _root_.rx.functions.Action1[Any] { def call(x: Any) = {} }"
      val emptyThrowableAction1 = q"new _root_.rx.functions.Action1[Throwable] { def call(x: Throwable) = {} }"
      val emptyAction0 = q"new _root_.rx.functions.Action0 { def call() = {} }"
      (nextSubscription.size, errorSubscription.size, doneSubscription.size) match {
        case (0, 0, 1) => q"$obsSym.observable.subscribe($emptyAction1, $emptyThrowableAction1, ${doneSubscription.head})"
        case (0, 1, 0) => q"$obsSym.observable.subscribe($emptyAction1, ${errorSubscription.head}, $emptyAction0)"
        case (0, 1, 1) => q"$obsSym.observable.subscribe($emptyAction1, ${errorSubscription.head}, ${doneSubscription.head})"
        case (1, 0, 0) => q"$obsSym.observable.subscribe(${nextSubscription.head}, $emptyThrowableAction1, $emptyAction0)"
        case (1, 0, 1) => q"$obsSym.observable.subscribe(${nextSubscription.head}, $emptyThrowableAction1, ${doneSubscription.head})"
        case (1, 1, 0) => q"$obsSym.observable.subscribe(${nextSubscription.head}, ${errorSubscription.head},$emptyAction0)"
        case (1, 1, 1) => q"$obsSym.observable.subscribe(${nextSubscription.head}, ${errorSubscription.head}, ${doneSubscription.head})"
      }
    })

    val out = q"""
      import _root_.scala.util.control.Breaks._
      import _root_.scala.collection.mutable
      // Required for state handling
      var $stateVar: Long = 0L
      val $stateLockVal = new _root_.scala.concurrent.Lock()
      // Queue declarations for Next events
      ..${nextEventsToQueues.map({ case (_, NewDecl(_, declaration)) => declaration })}
      // Variable declarations to store Error events
      ..${errorEventsToVars.map({ case (_, NewDecl(_, declaration)) => declaration })}
      ..$subscribeActions
      0
    """
    println(out)
    out
  }
}
