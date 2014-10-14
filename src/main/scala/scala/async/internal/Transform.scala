package scala.async.internal

trait Transform {
  self: JoinMacro =>
  def joinTransform[A: c.WeakTypeTag](pf: c.Tree): c.Tree
}

trait LockFreeTransform extends Transform {
  self: JoinMacro with Parse with ReactiveSystem with ReactiveSystemHelper => 
  import c.universe._

  override def joinTransform[A: c.WeakTypeTag](pf: c.Tree): c.Tree = {
  //   val patterns: Set[Pattern] = parse(pf)
  //   val events: Set[Event] = uniqueEvents(patterns)
  //   val nextEventsToQueues = 
  //     freshNames(events.nexts, "queue")
  //   val errorEventsToVars = 
  //     freshNames(events.errors, "error")
  //   val doneEventsToVars = 
  //     freshNames(events.dones, "done")
  //   val observables = events.groupBy(e => e.source).map(_._1)
  //   val observablesToSubscribers =
  //     freshNames(observables, "subscribers")
  //   // This block will be called once the joined observable has completed
  //   val unsubscribeAllBlock = q"..${observablesToSubscribers.map({ case (_, subscriber) => unsubscribe(subscriber) })}"
  //   // Queue declarations for buffering Next event messages
  //   val queueDeclarations = nextEventsToQueues.map({ case (event, queueName) =>
  //       val messageType = typeArgumentOf(event.source)
  //       q"val $queueName = _root_.java.util.concurrent.ConcurrentLinkedQueue[$messageType]()"
  //   })
  //   // Variable declarations for storing Error event messages (throwables)
  //   val errorVarDeclarations = errorEventsToVars.map({ case (_, varName) => 
  //       q"@volatile var $varName: _root_scala.async.internal.imports.nondeterministic.Message[Throwable] = null"
  //   })
  //   // Variable declarations for storing the occurence of onDone
  //   val doneVarDeclarations = doneEventsToVars.map({ case (_, varName) =>
  //     q"@volatile var $varName: _root_scala.async.internal.imports.nondeterministic.Message[Unit] = null"
  //   })
  //   // Every pattern has its own handler function which can be called by any event-handler involved in a pattern
  //   val patternMatchHandler = patterns.toList.freshNames("check_pattern").map({ case (pattern, name) => {
  //     val resolveMessage = fresh("resolveMessage")
  //     val messagesToClaim = fresh("messagesToClaim")
  //     val errors = pattern.events.errors
  //     val dones = pattern.events.dones
  //     val nexts = pattern.events.nexts
  //     // Messages of type done, and error are handled in the same way
  //     val errorOrdoneHandler = (eventVar: TermName) => q"""
  //       if ($eventVar == null) {
  //           return if ($resolveMessage.source == $event) {
  //             _root_scala.async.internal.imports.nondeterministic.Resolved
  //           } else {
  //             _root_scala.async.internal.imports.nondeterministic.NoMatch
  //           }
  //         } else if ($eventVar.Status() == _root_scala.async.internal.imports.nondeterministic.Status.Claimed) {
  //           return _root_scala.async.internal.imports.nondeterministic.Retry
  //         } else { 
  //           $messagesToClaim = $eventVar :: $messagesToClaim
  //         }
  //       """
  //     val errorHandler = errors.map(event => errorOrdoneHandler(errorEventsToVars.get(event).get))
  //     val doneHandler =  dones.map(event => errorOrdoneHandler(doneEventsToVars.get(event).get))
  //     // Message of type onNExt are handled very similarly to onDone, onError but include
  //     val nextHandler = nexts.map(event => {
  //       val head = fresh("head")
  //       val myQueue = nextEventsToQueues.get(event).get
  //       q"""
  //         val $head = $myQueue.peek()
  //         if ($head == null) {
  //           return if ($resolveMessage.source == $event)
  //             _root_scala.async.internal.imports.nondeterministic.Resolved
  //           else 
  //             _root_scala.async.internal.imports.nondeterministic.NoMatch
  //         } else if ($head.Status() == _root_scala.async.internal.imports.nondeterministic.Status.Claimed) {
  //           return _root_scala.async.internal.imports.nondeterministic.Retry
  //         } else {
  //           if ($resolveMessage.source == $event && $head neq $resolveMessage) return _root_scala.async.internal.imports.nondeterministic.Resolved
  //           $messagesToClaim = $head :: $messagesToClaim
  //         }
  //       """
  //     })
  //     // After we collected enough messages to claim, we try to do so. If we fail this means we lost a race against another resolving thread
  //     val claimedMessages = fresh("claimedMessages")
  //     val message = fresh("message")
  //     // But before we do that, we need to prepated for the case when we can match
  //     val dequeuedMessageVals = freshNames(pattern.events.nexts, "dequeuedMessage").toList
  //     val dequeueStatements = dequeuedMessageVals.map({ case (event, name) =>
  //       val queue = nextEventsToQueues.get(event).get
  //       val requestMoreStats = generateRequestMoreStatement(observablesToSubscribers.get(event.source).get)
  //       (q"val $name = $queue.poll().content",
  //        q"""
  //         if ($queue.isEmpty) {
  //           ..$requestMoreStats
  //         }""")
  //     })
  //     // Consume the error vars by storing them in a different variable, and setting the errorVar to null
  //     val retrievedErrorVals = freshNames(pattern.events.errors, "unwrapedError").toList
  //     val retrieveErrorStatements = retrievedErrorVals.map({ case (event, name) => 
  //       val errorVar = errorEventsToVars.get(event).get
  //       (q"val $name = $errorVar.content",
  //        q"$errorVar = null")
  //     })
  //     // Replace the occurences of Next, and Error event binding variables in the pattern-body
  //     val combinedEvents = dequeuedMessageVals ++ retrievedErrorVals
  //     var symbolsToReplace = combinedEvents.map({ case (event, _) => pattern.bindings.get(event).get }) 
  //     var ids = combinedEvents.map({ case (_, name) => Ident(name) })
  //     val rawPatternBody = replaceSymbolsWithTrees(symbolsToReplace, ids, pattern.bodyTree)
  //     // Decide what to do (Next, Done, or Pass), by inspection of the return expression of the pattern-body
  //     val patternBody = generateReturnExpression(rawPatternBody, afterDone = Some(unsubscribeAllBlock))
  //     val claimMessages = q"""
  //       val $claimedMessages = $messagesToClaim.map($message => if (!$message.tryClaim()) $message))
  //       if ($claimedMessages.size != $messagesToClaim.size) {
  //         $claimedMessages.foreach(_.unclaim())
  //         return _root_scala.async.internal.imports.nondeterministic.Retry
  //       } else {
  //         ..${dequeueStatements.map({ case (stats, _) => stats })}
  //         ..${dequeueStatements.map({ case (_, stats) => stats })}
  //         ..${retrieveErrorStatements.map({ case (stats, _) => stats })}
  //         ..${retrieveErrorStatements.map({ case (_, stats) => stats })}
  //         ..$patternBody
  //         return _root_scala.async.internal.imports.nondeterministic.Matched
  //     }
  //     """
  //     // The full checking of a pattern involves finding error, done, and next messages to claim,
  //     // trying to claim them, and if successful: execute pattern matching!
  //     q"""
  //       def $name[A]($resolveMessage: _root_scala.async.internal.imports.nondeterministic.Message[A]): _root_scala.async.internal.imports.nondeterministic.MatchResult = {
  //         var $messagesToClaim = _root_.scala.collection.Seq[_root_scala.async.internal.imports.nondeterministic.Message[Any]].empty
  //         ..$errorHandler
  //         ..$doneHandler
  //         ..$nextHandler
  //         $claimMessages
  //       }
  //     """
  //   }})
  //   // We create for each event call back a resolve function calling the corresponding pattern-match handler
  //   val eventCallbacks = events.toList.map(occuredEvent => occuredEvent -> ((nextMessage: Option[TermName]) => {
  //     val messageToResolve = fresh("messageToResolve")
  //     val retry = fresh("retry")
  //     val myPatterns = patterns.filter(pattern => pattern.events.contains(occuredEvent))
  //     val callPatternChecks = myPatterns.map(p => { 
  //       val patternCheck = patternMatchHandler.get(p).get 
  //       q"$patternCheck($messageToResolve)"
  //     })
  //     val patternChecks = callPatternChecks.map(call => q"""
  //       $call match {
  //         case _root_scala.async.internal.imports.nondeterministic.Resolved => return
  //         case _root_scala.async.internal.imports.nondeterministic.Retry => { $retry = true }
  //       }
  //       """)
  //     val resolve = q"""
  //       def resolve[A]($messageToResolve: _root_scala.async.internal.imports.nondeterministic.Message[A]) = {
  //         var $retry: Boolean = false
  //         $patternChecks
  //         if ($retry) {
  //           $backoff.once()
  //           resolve()
  //         }
  //       }
  //     """
  //     val storeEventStatement = occurendEvent match {
  //       case next: Next =>
  //         val myQueue = nextEventsToQueues.get(next).get
  //         val myMessage = fresh("message")
  //         q"""
  //           val $myMessage = _root_scala.async.internal.imports.nondeterministic.Message(${nextEvent.get}, $next)
  //           addToAndClaimQueue($myQueue, $myMessage)
  //           resolve($myMessage)
  //           $myQueue.unclaim()
  //         """
  //       case error: Error =>
  //         q"resolve(_root_scala.async.internal.imports.nondeterministic.Message(${nextEvent.get}, $error))"
  //       case done: Done =>
  //         q"resolve(_root_scala.async.internal.imports.nondeterministic.Message(()), $done))"
  //     }
  //     q"""
  //     $resolve
  //     ..$storeEventStatement
  //     """
  //   })
  //   // Putting it all together
  //   q"""
  //    ..$queueDeclarations
  //    ..$errorVarDeclarations
  //    ..$doneVarDeclarations
  //    ..$patternMatchHandler
  //   """
    EmptyTree
  }
}

trait LockTransform extends Transform { 
  self: JoinMacro with Parse with ReactiveSystem with ReactiveSystemHelper with Util =>
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
            val requestMoreStats = generateRequestMoreStatement(observablesToSubscribers.get(event.source).get)
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