package scala.async.internal

trait Transform {
  self: JoinMacro =>
  def joinTransform[A: c.WeakTypeTag](pf: c.Tree): c.Tree
}

trait RxJoinTransform extends Transform {
  self: JoinMacro with Parse with RxJavaSubscribeService => 
  import c.universe._

  override def joinTransform[A: c.WeakTypeTag](pf: c.Tree): c.Tree = {
    val patterns: Set[Pattern] = parse(pf)
    EmptyTree
  }
}

trait LockTransform extends Transform { 
  self: JoinMacro with Parse with RxJavaSubscribeService with Util =>
  import c.universe._

  // names represents a global namespace 
  object names { 
    val stateVar = fresh("state")
    val stateLockVal = fresh("stateLock")
    val subjectVal = fresh("subject")
    val stop = fresh("stop")
  }

  def generatePatternCheck(patternId: Long, state: TermName, patternGuard: c.Tree) = (body: c.Tree, continuation: c.Tree) => {
    val checkPatternTree = q"(~$state & $patternId) == 0"
    val matchExpression = patternGuard match {
      case EmptyTree => checkPatternTree
      case guardTree => q"(($checkPatternTree) && $guardTree)"
    }
    q"""
    ${insertIfTracing(q"""
      debug("Checking pattern: " + $patternId)
      """)}
    if ($matchExpression) {
        ${insertIfTracing(q"""debug("Pattern matched!")""")}
        ..$body
        ${insertIfTracing(q"""debug("Releasing lock")""")}
        ${names.stateLockVal}.release()
        ..$continuation
        break
    }
   """
 }

  def generateReturnExpression(patternBody: c.Tree): c.Tree = patternBody match {
    case Apply(TypeApply(Select(Select(_, TermName("Next")), TermName("apply")), _), nextExpr) => q"""
      ${insertIfTracing(q"printQueues()")}
      try {
        ${names.subjectVal}.onNext(${nextExpr.head})
      } catch {
        case e: Throwable => ${names.subjectVal}.onError(e)
      }"""
    case Select(_, TermName("Done")) => q"""
      ${names.stop} = true
      ${names.subjectVal}.onCompleted()
      unsubscribe()
      """
    case Select(_, TermName("Pass")) => EmptyTree 
    case Apply(Select(_, TermName("unitToPass")), stats) => q"..$stats"
    // ^ matches the implicit conversion from Unit to Pass
    case other =>  
      c.error(c.enclosingPosition, s"Join pattern has to return a value of type JoinReturn: Next(...), Done, or Pass. Got: $other")
      EmptyTree
  }

  override def joinTransform[A: c.WeakTypeTag](pf: c.Tree): c.Tree = {
    // Use the constructs defined the Parse trait as representations of Join-Patterns.
    val patterns: Set[Pattern] = parse(pf)  
    // Collect events across all pattern, and remove duplicates.
    val events: Set[Event] = 
      patterns.flatMap({ case Pattern(events, _, _, _) => events }).toSet
    // Assign to each event a unique id (has to be a power of 2).
    val eventsToIds: Map[Event, Long] = 
      events.zipWithIndex.map({ case (event, index) => (event, 1L << index) }).toMap
    // Pattern ids are defined as the binary-sum of the ids of their events.
    def accumulateEventId(acc: Long, event: Event) = acc | eventsToIds.get(event).get
    val patternsToIds: Map[Pattern, Long] =
      patterns.map(p => p -> p.events.foldLeft(0L)(accumulateEventId)).toMap
    // We keep for every Next event a mutable Queue. We'll create names now, and declare the queues later.
    val nextEventsToQueues =
      events.collect({ case event: Next => event })
      .map(event => (event, fresh("queue")))
      .toMap
    // We keep for every Error event a variable to store the throwable it carries.
    // No larger buffer is needed as an Exeception can be thrown exactly once per source.
    // Notice: we do not need to buffer Done events since they do not carry a message,
    // and they happen only once per souce. Therefore no additional information needs to 
    // be stored other than their presence (which is already done so in the global state).
    val errorEventsToVars = 
      events.collect({ case event: Error => event })
      .map(event => (event, fresh("error")))
      .toMap

    val observablesToSubscriptions =
      events.groupBy(e => e.source)
        .map({case (observable ,_) => observable -> fresh("subscription")})
        .toMap
    // We generate a callback for every event of type Next/Error/Done. (NextFilter
    // are a special case of Next, and handled within the callbacks of the Next event
    // with the same source-symbol (i.e. the same Observable)).
    val eventCallbacks = events.toList.map(occuredEvent => occuredEvent -> ((nextMessage: Option[TermName]) => {
      val possibleStateVal = fresh("possibleState")
      val myPatterns = patterns.filter(pattern => pattern.events.contains(occuredEvent))
      val patternChecks = 
        myPatterns.toList.map(myPattern => {
          // Generate the if-expression which checks whether a pattern has matched. We have to later provide 
          // the "checkExpression" with a body, and a continuation.
          val checkExpression = generatePatternCheck(patternsToIds.get(myPattern).get, possibleStateVal, myPattern.guardTree)
          // In case of a successful pattern-match, we would like to execute the pattern-body. For this to
          // succeed we need to replace the occurences of pattern-bound variables in the pattern-body with
          // the actual message content. For example if the join-pattern looks like this: "case O1(x) => x + 1", 
          // we need to replace the "x" with the message identifier that we received from the O1 source. This 
          // means that we need to dequeue a message from all other Next events which are involved in the successful 
          // pattern-match, and also retrieve the contents of the variables which store throwables carried by
          // Error events. Done events do not carry additional bindings. We employ an optimization called "lazy"-
          // queuing which does not queue the occuredEvent unless necessary. Therefore, if the occuredEvent is
          // not None (it's a Next or an Error), we also need to replace it in the pattern-body.
          val otherEvents = myPattern.events.toList.filter(otherEvent => otherEvent != occuredEvent)
          // We need vals to store the messages we dequeue from previous Next events
          val dequeuedMessageVals = 
            otherEvents.collect({ case event: Next => event })
            .map(event => event -> fresh("dequeuedMessage"))
          // Generate the deqeue statements. Further, update the state to reflect the removal of
          // the buffered events (set the event bit to 0 in case there are no more buffered messages).
          // We return a pair because of a problem of the creation of a Block by the quasiquotation if 
          // we put the statements into a single quasiquote.
          val dequeueStatements = dequeuedMessageVals.map({ case (event, name) =>
            val queue = nextEventsToQueues.get(event).get
            (q"val $name = $queue.dequeue()",
             q"""if ($queue.isEmpty) {
               ${names.stateVar} = ${names.stateVar} & ~${eventsToIds.get(event).get}
              }""")
          })
          // Retrieve the names of the vars storting the throwables of possibly involved Error events
          val errorVars = otherEvents.collect({ case event: Error => event })
            .map(event => event -> errorEventsToVars.get(event).get)
          // Replace the occurences of Next, and Error event binding variables in the pattern-body
          val combinedEvents = dequeuedMessageVals ++ errorVars
          var symbolsToReplace = combinedEvents.map({ case (event, _) => myPattern.bindings.get(event).get }) 
          var ids = combinedEvents.map({ case (_, name) => Ident(name) })
          // If the occurred event includes a message, we also need to replace it in the body!
          if (nextMessage.nonEmpty) {
            symbolsToReplace = myPattern.bindings.get(occuredEvent).get :: symbolsToReplace
            ids = Ident(nextMessage.get) :: ids
          }
          val rawPatternBody = replaceSymbolsWithTrees(symbolsToReplace, ids, myPattern.bodyTree)

          val patternBody = rawPatternBody match {
            case Block(stats, lastExpr) => q"..$stats; ..${generateReturnExpression(lastExpr)}"
            case _ => generateReturnExpression(rawPatternBody)
          }

          checkExpression(
           q"""..${dequeueStatements.map({ case (stats, _) => stats })}
               ..${dequeueStatements.map({ case (_, stats) => stats })}
               ..$patternBody""", 
            EmptyTree
          )
      })
      // In case a message has not lead to a pattern-match we need to store it. We do not need
      // to store Done events as their presence is already stored with the set bit in the state
      // bitfield.
      val bufferStatement = occuredEvent match {
        case next @ Next(_) => q"${nextEventsToQueues.get(next).get}.enqueue(${nextMessage.get})"
        case error @ Error(_) => q"${errorEventsToVars.get(error).get} = ${nextMessage.get}"
        case _ => EmptyTree
      }
      q"""
        ${insertIfTracing(q"""debug("Waiting for lock")""")}
        ${names.stateLockVal}.acquire()
        ${insertIfTracing(q"""debug("Got lock")""")}
        if (${names.stop}) {
          ${insertIfTracing(q"""debug("Aborting execution")""")}
          ${insertIfTracing(q"""debug("Releasing Lock")""")}
          ${names.stateLockVal}.release()
        } else {
          val $possibleStateVal = ${names.stateVar} | ${eventsToIds.get(occuredEvent).get}
          breakable {
            ${insertIfTracing(q"""debug("Checking Patterns")""")}
            ..$patternChecks
            ${insertIfTracing(q"""debug("No pattern matched. Buffering.")""")}
            // Reaching this line means that no pattern has matched, and we need to buffer the message
            $bufferStatement
            ${names.stateVar} = $possibleStateVal
            ${insertIfTracing(q"""debug("Releasing Lock")""")}
            ${insertIfTracing(q"printQueues()")}
            ${names.stateLockVal}.release()
          }
        }
      """
    }))
    // Generate subscriptions for RxJava
    val observablesToEvents = eventCallbacks.groupBy({ case (event, _) => event.source }) 
    val subscriptions = observablesToEvents.map({ case (obsSym, events) => 
      val next = events.find(event => event._1.isInstanceOf[Next]).map(_._2)
      val error = events.find(event => event._1.isInstanceOf[Error]).map(_._2)
      val done = events.find(event => event._1.isInstanceOf[Done]).map(_._2)
      val subscription = observablesToSubscriptions.get(obsSym).get
      q"$subscription = ${generateSubscription(obsSym, next, error, done)}"
    })

    val subscriptionVarDefs = observablesToEvents.map({ case (obsSym, _) => 
      val subscription = observablesToSubscriptions.get(obsSym).get
      q"var $subscription: _root_.rx.lang.scala.Subscription = null"
    })

    val resultType = implicitly[WeakTypeTag[A]].tpe
    // Assemble all parts into the full transform

    q"""
    import _root_.scala.util.control.Breaks._
    import _root_.scala.collection.mutable

    var ${names.stateVar} = 0L
    val ${names.stateLockVal} = new _root_.scala.concurrent.Lock()
    val ${names.subjectVal} = _root_.rx.lang.scala.subjects.ReplaySubject[$resultType]()
    var ${names.stop} = false

    // Subscription declarations
    ..$subscriptionVarDefs

    // Queue declarations for Next event messages
    ..${nextEventsToQueues.map({ case (event, queueName) =>
        val messageType = typeArgumentOf(event.source)
        q"val $queueName = mutable.Queue[$messageType]()"
      })}

    // Variable declarations to store Error event messages (throwables)
    ..${errorEventsToVars.map({ case (event, varName) => 
        q"var $varName: Throwable = null"
    })}

    ..$subscriptions

    ${insertIfTracing(q"""
    def debug(s: String) = println("[join] Thread" + Thread.currentThread.getId + ": " + s)
    """)}
    
    ${insertIfTracing(q"""
      def printQueues() = {
        ..${nextEventsToQueues.map({ case (_, queueName) =>
          q"""println("[join] " + $queueName)"""
      })}
    }""")}

    def unsubscribe() = {..${observablesToSubscriptions.map({ case (_, s) => q"""
        ${insertIfTracing(q"""debug("Unsubscribing")""")}
        $s.unsubscribe()
    """})}}

    ${names.subjectVal}
    """
  }
}