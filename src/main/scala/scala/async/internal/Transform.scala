package scala.async.internal

trait Transform {
  self: JoinMacro =>
  def joinTransform[A: c.WeakTypeTag](pf: c.Tree): c.Tree
}

trait LockFreeTransform extends Transform {
  self: JoinMacro with Parse with ReactiveSystem with ReactiveSystemHelper with Backpressure => 
  import c.universe._

  object names {
    val outSubscriber = fresh("outSubscriber")
  }

  override def joinTransform[A: c.WeakTypeTag](pf: c.Tree): c.Tree = {
    val patterns: Set[Pattern] = parse(pf)
    val events: Set[Event] = uniqueEvents(patterns)
    val nextEventsToQueues = 
      freshNames(events.nexts, "queue")
    val errorEventsToVars = 
      freshNames(events.errors, "error")
    val doneEventsToVars = 
      freshNames(events.dones, "done")
    val observables = events.groupBy(e => e.source).map(_._1)
    val observablesToSubscribers =
      freshNames(observables, "subscribers")
     val queuesToLocks = 
      freshNames(nextEventsToQueues.map(_._2), "queueLock")
    // We give every event an id so that we can use a single matching function for every pattern.
    val eventsToIds: Map[Event, Long] = 
      events.zipWithIndex.map({ case (event, index) => (event, 1L << index) }).toMap
    // This block will be called once the joined observable has completed
    val unsubscribeAllBlock = q"..${observablesToSubscribers.map({ case (_, subscriber) => unsubscribe(subscriber) })}"
    // Queue declarations for buffering Next event messages
    val queueDeclarations = nextEventsToQueues.map({ case (event, queueName) =>
        val messageType = typeArgumentOf(event.source)
        q"val $queueName = new _root_.java.util.concurrent.ConcurrentLinkedQueue[_root_.scala.async.internal.imports.nondeterministic.Message[$messageType]]()"
    })
    // Variable declarations for storing Error event messages (throwables)
    val errorVarDeclarations = errorEventsToVars.map({ case (_, varName) => 
        q"@volatile var $varName: _root_.scala.async.internal.imports.nondeterministic.Message[Throwable] = null"
    })
    // Variable declarations for storing the occurence of onDone
    val doneVarDeclarations = doneEventsToVars.map({ case (_, varName) =>
      q"@volatile var $varName: _root_.scala.async.internal.imports.nondeterministic.Message[Unit] = null"
    })
    // Every pattern has its own handler function which can be called by any event-handler involved in a pattern
    val patternMatchHandler = freshNames(patterns.toList, "checkPattern").map({ case (pattern, name) => pattern -> {
      val resolveMessage = fresh("resolveMessage")
      val messagesToClaim = fresh("messagesToClaim")
      val errors = pattern.events.errors.toList
      val dones = pattern.events.dones.toList
      val nexts = pattern.events.nexts.toList
      // Messages of type done, and error are handled in the same way
      val errorOrdoneHandler = (eventVar: TermName, eventId: Long) => q"""
          if ($eventVar == null) {
            return if ($resolveMessage.source == $eventId) {
              _root_.scala.async.internal.imports.nondeterministic.Resolved
            } else {
              _root_.scala.async.internal.imports.nondeterministic.NoMatch
            }
          } else if ($eventVar.status() == _root_.scala.async.internal.imports.nondeterministic.Claimed) {
            return _root_.scala.async.internal.imports.nondeterministic.Retry
          } else { 
            $messagesToClaim = $eventVar :: $messagesToClaim
          }
        """
      val errorHandler = errors.map(event => errorOrdoneHandler(errorEventsToVars.get(event).get, eventsToIds.get(event).get))
      val doneHandler = dones.map(event => errorOrdoneHandler(doneEventsToVars.get(event).get, eventsToIds.get(event).get))
      // Message of type onNext are handled very similarly to onDone, onError but include
      val nextHandler = nexts.map(event => {
        val head = fresh("head")
        val myQueue = nextEventsToQueues.get(event).get
        val eventId = eventsToIds.get(event).get
        q"""
          val $head = $myQueue.peek()
          if ($head == null) {
            return if ($resolveMessage.source == $eventId) {
              debug("Decided Resolved")
              _root_.scala.async.internal.imports.nondeterministic.Resolved
            }
            else {
              debug("Decided no match")
              _root_.scala.async.internal.imports.nondeterministic.NoMatch
            }
          } else if ($head.status() == _root_.scala.async.internal.imports.nondeterministic.Claimed) {
            debug("Decided Retry")
            return _root_.scala.async.internal.imports.nondeterministic.Retry
          } else {
            debug("Adding to claim messages")
            if ($resolveMessage.source == $eventId && !($head eq $resolveMessage)) return _root_.scala.async.internal.imports.nondeterministic.Resolved
            $messagesToClaim = $head :: $messagesToClaim
          }
        """
      })
      // After we collected enough messages to claim, we try to do so. If we fail this means we lost a race against another resolving thread
      val claimedMessages = fresh("claimedMessages")
      // But before we do that, we need to prepated for the case when we can match
      val dequeuedMessageVals = freshNames(pattern.events.nexts, "dequeuedMessage").toList
      val dequeueStatements = dequeuedMessageVals.map({ case (event, name) =>
        val queue = nextEventsToQueues.get(event).get
        val requestMoreStats = if (unboundBuffer) EmptyTree else {
          val subscriber = observablesToSubscribers.get(event.source).get
          q"""
          if ($queue.isEmpty) {
            ..${requestMore(subscriber, bufferSizeTree)}
          }"""
        }
        (q"val $name = $queue.poll().content",
        requestMoreStats)
      })
      // Consume the error vars by storing them in a different variable, and setting the errorVar to null
      val retrievedErrorVals = freshNames(pattern.events.errors, "unwrapedError").toList
      val retrieveErrorStatements = retrievedErrorVals.map({ case (event, name) => 
        val errorVar = errorEventsToVars.get(event).get
        (q"val $name = $errorVar.content",
         q"$errorVar = null")
      })
      // Consume the done messages
      val retrieveDoneStatements = dones.map(event => {
        val name = doneEventsToVars.get(event).get
        q"$name = null" 
      })
      // Replace the occurences of Next, and Error event binding variables in the pattern-body
      val combinedEvents = dequeuedMessageVals ++ retrievedErrorVals
      var symbolsToReplace = combinedEvents.map({ case (event, _) => pattern.bindings.get(event).get }) 
      var ids = combinedEvents.map({ case (_, name) => Ident(name) })
      val rawPatternBody = replaceSymbolsWithTrees(symbolsToReplace, ids, pattern.bodyTree)
      // Decide what to do (Next, Done, or Pass), by inspection of the return expression of the pattern-body
      val patternBody = generateReturnExpression(parsePatternBody(rawPatternBody), names.outSubscriber, afterDone = Some(unsubscribeAllBlock))
      val claimMessages = q"""
        val $claimedMessages = $messagesToClaim.map(message => if (!message.tryClaim()) message)
        if ($claimedMessages.size != $messagesToClaim.size) {
          $claimedMessages.foreach(m => m.asInstanceOf[_root_.scala.async.internal.imports.nondeterministic.Message[Any]].unclaim())
          debug("Lost race. Retry.")
          return _root_.scala.async.internal.imports.nondeterministic.Retry
        } else {
          ..${dequeueStatements.map({ case (stats, _) => stats })}
          ..${dequeueStatements.map({ case (_, stats) => stats })}
          ..${retrieveErrorStatements.map({ case (stats, _) => stats })}
          ..${retrieveErrorStatements.map({ case (_, stats) => stats })}
          ..$retrieveDoneStatements
          ..$patternBody
          debug("Matched pattern! Resolved.")
          return _root_.scala.async.internal.imports.nondeterministic.Resolved
      }
      """
      // The full checking of a pattern involves finding error, done, and next messages to claim,
      // trying to claim them, and if successful: execute pattern matching!
      name -> q"""def $name[A]($resolveMessage: _root_.scala.async.internal.imports.nondeterministic.Message[A]): _root_.scala.async.internal.imports.nondeterministic.MatchResult = {
          debug("Checking pattern " + $resolveMessage)
          var $messagesToClaim = _root_.scala.collection.immutable.List.empty[_root_.scala.async.internal.imports.nondeterministic.Message[Any]]
          ..$errorHandler
          ..$doneHandler
          ..$nextHandler
          $claimMessages
        }
      """
    }})
    // We create for each event call back a resolve function calling the corresponding pattern-match handler
    val eventCallbacks = events.toList.map(occuredEvent => occuredEvent -> ((nextMessage: Option[TermName]) => {
      val messageToResolve = fresh("messageToResolve")
      val backoff = fresh("backoff")
      val retry = fresh("retry")
      val myPatterns = patterns.filter(pattern => pattern.events.contains(occuredEvent)).toList
      val callPatternChecks = myPatterns.map(p => { 
        val patternCheck = patternMatchHandler.get(p).get._1 
        q"$patternCheck($messageToResolve)"
      })
      val patternChecks = callPatternChecks.map(call => q"""
        $call match {
          case _root_.scala.async.internal.imports.nondeterministic.Resolved => return
          case _root_.scala.async.internal.imports.nondeterministic.Retry => { $retry = true }
        }
        """)
      val mySubscriber = observablesToSubscribers.get(occuredEvent.source).get
      val resolve = q"""
        @_root_.scala.annotation.tailrec
        def resolve[A]($messageToResolve: _root_.scala.async.internal.imports.nondeterministic.Message[A]): Unit = {
          // TODO: Remove
          debug("Resolving: " + $messageToResolve)
          var $retry: Boolean = false
          val $backoff = _root_.scala.async.internal.imports.nondeterministic.Backoff()
          ..$patternChecks
          if ($retry && !${isUnsubscribed(mySubscriber)}) {
            $backoff.once()
            resolve($messageToResolve)
          }
        }
      """
      val eventId = eventsToIds.get(occuredEvent).get
      val storeEventStatement = occuredEvent match {
        case next: Next =>
          val myQueue = nextEventsToQueues.get(next).get
          val myMessage = fresh("message")
          val myQueueLock = queuesToLocks.get(nextEventsToQueues.get(next).get).get
          val myBackoff = fresh("backoff")
          val addToAndClaimQueue = q"""
            @_root_.scala.annotation.tailrec
            def addToAndClaimQueue(): Unit = {
              val $myBackoff = _root_.scala.async.internal.imports.nondeterministic.Backoff()
              if (!$myQueueLock.tryClaim()) {
                $myBackoff.once()
                addToAndClaimQueue()
              } else {
                $myQueue.add($myMessage)
              } 
            }"""
          // Putting the store event statements for next event toghether
          q"""
            val $myMessage = _root_.scala.async.internal.imports.nondeterministic.Message(${nextMessage.get}, $eventId)
            $addToAndClaimQueue
            addToAndClaimQueue()
            resolve($myMessage)
            $myQueueLock.unclaim()
          """
        case error: Error =>
          q"resolve(_root_.scala.async.internal.imports.nondeterministic.Message(${nextMessage.get}, $eventId))"
        case done: Done =>
          q"resolve(_root_.scala.async.internal.imports.nondeterministic.Message((), $eventId))"
        case _ => 
          c.error(c.enclosingPosition, s"Filters on next events are not yet supported.")
          EmptyTree
      }
      q"""
      $resolve
      ..$storeEventStatement
      """
    }))
    // TODO: Explain why we need to createVariableStoringSubscriber mechanism.
    val subscriberVarDeclarations = generateSubscribervarDeclarations(observablesToSubscribers)
    // Create a Subscriber for every observable
    val subscriberDeclarations = generateSubscriberDeclarations(observablesToSubscribers, eventCallbacks, Some(bufferSizeTree))
    // We generate code which subscribes the created subscribers to their observable
    val subscriptions = generateObservableSubscriptions(observablesToSubscribers)
    // Create statements for the queue-locks
    val queueLocks = queuesToLocks.map({ case (_, lockName) => q"val $lockName = _root_.scala.async.internal.imports.nondeterministic.QueueLock()"})
    // Putting it all together
    val onSubscribe = q"""
      def debug(s: String) = println("[join] Thread" + Thread.currentThread.getId + ": " + s)
     ..$queueLocks
     ..$queueDeclarations
     ..$errorVarDeclarations
     ..$doneVarDeclarations
     ..$subscriberVarDeclarations
     ..${patternMatchHandler.map(_._2._2)}
     ..$subscriberDeclarations
     ..$subscriptions
    """
    // Find the type of elements the generated observable will emit
    val resultType = implicitly[WeakTypeTag[A]].tpe
    createPublisher(onSubscribe, names.outSubscriber, resultType)
  }
}
