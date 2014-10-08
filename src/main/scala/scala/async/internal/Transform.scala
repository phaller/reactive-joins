package scala.async.internal

trait Transform {
  self: JoinMacro =>
  def joinTransform[A: c.WeakTypeTag](pf: c.Tree): c.Tree
}

trait LockFreeTransform extends Transform {
  self: JoinMacro with Parse with ReactiveSystem => 
  import c.universe._

  override def joinTransform[A: c.WeakTypeTag](pf: c.Tree): c.Tree = {
    val patterns: Set[Pattern] = parse(pf)
    val events: Set[Event] = uniqueEvents(patterns)
    val nextEventsToQueues = 
      freshNames(events.nexts, "queue")
    val errorEventsToVars = 
      freshNames(events.errors, "error")
    val doneEventsToVars = 
      freshNames(events.dones, "done")
    // Queue declarations for buffering Next event messages
    val queueDeclarations = nextEventsToQueues.map({ case (event, queueName) =>
        val messageType = typeArgumentOf(event.source)
        q"val $queueName = _root_.java.util.concurrent.ConcurrentLinkedQueue[$messageType]()"
    })
    // Variable declarations for storing Error event messages (throwables)
    val errorVarDeclarations = errorEventsToVars.map({ case (_, varName) => 
        q"@volatile var $varName: _root_.java.lang.Throwable = null"
    })
    // Variable declarations for storing the occurence of onDone
    val doneVarDeclarations = doneEventsToVars.map({ case (_, varName) =>
      q"@volatile var $varName: Boolean = false"
    })
    // val eventCallbacks = events.toList.map(occuredEvent => occuredEvent -> ((nextMessage: Option[TermName]) => {
    //   val messageToResolve = fresh("messageToResolve")
    //   val retry = fresh("retry")
    //   val resolve = q"""
    //     def resolve[A]($messageToResolve: Option[_root_scala.async.Join.Message[A]]) = {
    //       var $retry: Boolean = false
    //       check_pattern_1($messageToResolve) match {
    //         case Resolved => return
    //         case Retry => { retry = true }
    //       }
    //       check_pattern_2($messageToResolve) match {
    //         case Resolved => return
    //         case Retry => { retry = true }
    //       }
    //       if ($retry) {
    //         $backoff.once()
    //         resolve()
    //       }
    //     }
    //   """

      // val storeEventStatement = occurendEvent match {
      //   case next: Next =>
      //     val myQueue = nextEventsToQueues.get(next).get
      //     val myMessage = fresh("message")
      //     q"""
      //     val $myMessage = _root_scala.async.Join.Message(${nextEvent.get}, $myQueue)
      //     addToAndClaimQueue($myQueue, $myMessage)
      //     resolve(Some($myMessage))
      //     $myQueue.unclaim()
      //     """
      //   case error: Error =>
      //     val myErrorVar = errorEventsToVars.get(error).get
      //     q"""
      //       $myErrorVar = ${nextMessage.get}
      //       resolve(None) 
      //     """
      //   case done: Done =>
      //     val myDoneVar = doneEventsToVars.get(done).get
      //     q"""
      //       $myDoneVar = true
      //       resolve(None) 
      //     """
      // }
    // })
    EmptyTree
  }
}

trait LockTransform extends Transform { 
  self: JoinMacro with Parse with ReactiveSystem with BackPressure with Util =>
  import c.universe._
  import scala.async.Join.{JoinReturn, Next => ReturnNext, Done => ReturnDone, Pass => ReturnPass, Last => ReturnLast}

  // names represents a "global" namespace where TermNames can be stored to be used across the functions involved in the transform
  object names {
    val outSubscriber = fresh("outSubscriber")
    val stateVar = fresh("state")
    val stateLockVal = fresh("stateLock")
  }

  def generatePatternCheck(patternId: Long, state: TermName, patternGuard: Tree) = (body: c.Tree, continuation: c.Tree) => {
    val checkPatternTree = q"(~$state & $patternId) == 0"
    val matchExpression = patternGuard match {
      case EmptyTree => checkPatternTree
      case guardTree => q"(($checkPatternTree) && $guardTree)"
    }
    q"""
    if ($matchExpression) {
        ..$body
        ${names.stateLockVal}.unlock()
        ..$continuation
        break
    }
   """
  }

  def generateReturnExpression(patternBody: Tree, afterDone: Option[Tree] = None): Tree = parsePatternBody(patternBody) match {
    case (ReturnNext(returnExpr), block) =>
      val exceptionName = fresh("ex")
      q"""
      ..$block
      try {
        ${next(names.outSubscriber, returnExpr)}
      } catch {
        case $exceptionName: Throwable => 
        ${error(names.outSubscriber, q"$exceptionName")}
      }"""
    case (ReturnLast(returnExpr), block) => q"""
      ..$block
      ${next(names.outSubscriber, returnExpr)}
      ${done(names.outSubscriber)}
      ${afterDone.getOrElse(EmptyTree)}
    """
    case (ReturnDone, block) => q"""
      ..$block
      ${done(names.outSubscriber)}
      ${afterDone.getOrElse(EmptyTree)}
      """
    case (ReturnPass, block) => q"""
      ..$block
      """
  }

  override def joinTransform[A: c.WeakTypeTag](pf: Tree): Tree = {
    // Use the constructs defined the Parse trait as representations of Join-Patterns.
    val patterns: Set[Pattern] = parse(pf)  
    // Collect unique events from patterns 
    val events: Set[Event] = uniqueEvents(patterns)
    // Assign to each event a unique id (has to be a power of 2).
    val eventsToIds: Map[Event, Long] = 
      events.zipWithIndex.map({ case (event, index) => (event, 1L << index) }).toMap
    // Pattern ids are defined as the binary-sum of the ids of their events.
    def accumulateEventId(acc: Long, event: Event) = acc | eventsToIds.get(event).get
    val patternsToIds: Map[Pattern, Long] =
      patterns.map(p => p -> p.events.foldLeft(0L)(accumulateEventId)).toMap
    // We keep for every Next event a mutable Queue. We'll create names now, and declare the queues later.
    val nextEventsToQueues = 
      freshNames(events.nexts, "queue")
    // We keep for every Error event a variable to store the throwable it carries.
    // No larger buffer is needed as an Exeception can be thrown exactly once per source.
    // Notice: we do not need to buffer Done events since they do not carry a message,
    // and they happen only once per source. Therefore no additional information needs to 
    // be stored other than their presence (which is already done so in the global state).
    val errorEventsToVars = 
      freshNames(events.errors, "error")
    // Every observable is subscribered to with a subscriber. It can later be used as a handle
    // to unsubscribe, and request more items. We therefore need to store subscribers.
    val observables = events.groupBy(e => e.source).map(_._1)
    val observablesToSubscribers =
      freshNames(observables, "subscribers")
    // This block will be called once the joined observable has completed
    val unsubscribeAllBlock = q"..${observablesToSubscribers.map({ case (_, subscriber) => unsubscribe(subscriber) })}"
    // We generate a callback for every event of type Next/Error/Done. 
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
            freshNames(otherEvents.nexts, "dequeuedMessage")
            .toList
          // Generate the deqeue statements. Further, update the state to reflect the removal of
          // the buffered events (set the event bit to 0 in case there are no more buffered messages).
          // We return a pair because of a problem of the creation of a Block by the quasiquotation if 
          // we put the statements into a single quasiquote.
          val dequeueStatements = dequeuedMessageVals.map({ case (event, name) =>
            val queue = nextEventsToQueues.get(event).get
            val requestMoreStats = if (unboundBuffer) EmptyTree else {
              val subscriber = observablesToSubscribers.get(event.source).get
              requestMore(subscriber, bufferSizeTree)
            }
            (q"val $name = $queue.dequeue()",
             q"""if ($queue.isEmpty) {
               ${names.stateVar} = ${names.stateVar} & ~${eventsToIds.get(event).get}
               ..$requestMoreStats
              }""")
          })
          // Retrieve the names of the vars storing the Throwables of possibly involved Error events
          val errorVars = otherEvents.errors
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
          // Decide what to do (Next, Done, or Pass), by inspection of the return expression of the pattern-body
          val patternBody = generateReturnExpression(rawPatternBody, afterDone = Some(unsubscribeAllBlock))
          val requestMoreFromOccured = occuredEvent match {
            case event @ Next(source) if !unboundBuffer => 
              val occuredQueue = nextEventsToQueues.get(event).get
              val occurredSubscriber = observablesToSubscribers.get(source).get
              val requestStatement = requestMore(occurredSubscriber, bufferSizeTree)
              q"""
              if ($occuredQueue.isEmpty) {
                $requestStatement
              }
              """
            case _ => EmptyTree
          }
          checkExpression(
           q"""..${dequeueStatements.map({ case (stats, _) => stats })}
               ..${dequeueStatements.map({ case (_, stats) => stats })}
               ..$patternBody""", 
            requestMoreFromOccured
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
        if(${isUnsubscribed(names.outSubscriber)}) return
        ${names.stateLockVal}.lock()
        val $possibleStateVal = ${names.stateVar} | ${eventsToIds.get(occuredEvent).get}
        breakable {
          ..$patternChecks
          // Reaching the following line means that no pattern has matched, and we need to buffer the message
          $bufferStatement
          ${names.stateVar} = $possibleStateVal
          ${names.stateLockVal}.unlock()
        }
      """
    }))
    // TODO: Explain why we need to createVariableStoringSubscriber mechanism.
    val subscriberVarDeclarations = observablesToSubscribers.map({ case (observable, subscriber) =>
      val obsTpe = typeArgumentOf(observable)
      createVariableStoringSubscriber(subscriber, obsTpe)
    })
    // Group the event call backs we created by their source observable
     val observablesToEventCallbacks = 
      eventCallbacks.groupBy({ case (event, _) => event.source })
    // Create a Subscriber for every observable
    val subscriberDeclarations = observablesToEventCallbacks.map({ case (obsSym, events) => 
      val next = events.find(event => event._1.isInstanceOf[Next]).map(_._2)
      val error = events.find(event => event._1.isInstanceOf[Error]).map(_._2)
      val done = events.find(event => event._1.isInstanceOf[Done]).map(_._2)
      val nextMessageType = typeArgumentOf(obsSym)
      val subscriberDecl = createSubscriber((next, nextMessageType), error, done, Some(bufferSizeTree))
      val subscriberVar = observablesToSubscribers.get(obsSym).get
      q"$subscriberVar = $subscriberDecl"
    })
    // We generate code which subscribes the created subscribers to their observable
    val subscriptions = observablesToSubscribers.map({ case (observable, subscriber) =>
      subscribe(observable.asTerm.name, subscriber)
    })
    // Queue declarations for buffering Next event messages
    val queueDeclarations = nextEventsToQueues.map({ case (event, queueName) =>
        val messageType = typeArgumentOf(event.source)
        q"val $queueName = _root_.scala.collection.mutable.Queue[$messageType]()"
    })
    // Variable declarations for storing Error event messages (throwables)
    val errorVarDeclarations = errorEventsToVars.map({ case (_, varName) => 
        q"var $varName: _root_.java.lang.Throwable = null"
    })
    // TODO: How to unsubscribe...
    // OnSubscribe will be called for every new subscriber to our joined Observable
    val onSubscribe = q"""
      import _root_.scala.util.control.Breaks._
      val ${names.stateLockVal} = new _root_.java.util.concurrent.locks.ReentrantLock()
      var ${names.stateVar} = 0L
      ..$queueDeclarations
      ..$errorVarDeclarations
      ..$subscriberVarDeclarations
      ..$subscriberDeclarations
      ..$subscriptions
    """
    // Find the type of elements the generated observable will emit
    val resultType = implicitly[WeakTypeTag[A]].tpe
    createPublisher(onSubscribe, names.outSubscriber, resultType)
  }
}