package scala.async.internal

trait Transform {
  self: JoinMacro =>
  def joinTransform[A: c.WeakTypeTag](pf: c.Tree): c.Tree
}

trait LockFreeTransform extends Transform {
  self: JoinMacro with Parse with RxJavaSubscribeService => 
  import c.universe._
  // names represents a global namespace 
  object names { 
    val subjectVal = fresh("subject")
  }

  override def joinTransform[A: c.WeakTypeTag](pf: c.Tree): c.Tree = {
    val patterns: Set[Pattern] = parse(pf)
    val events: Set[Event] = uniqueEvents(patterns)
    val observables = events.groupBy(e => e.source).map(_._1)
    val observablesToSubscriptions =
      freshNames(observables, "subscription")
    val observablesToRequestables = 
      freshNames(observables, "requestable")
    val nextEventsToQueues = 
      freshNames(events.nexts, "queue")
    val errorEventsToVars = 
      freshNames(events.errors, "error")
    val doneEventsToVars = 
      freshNames(events.dones, "done")
    val nextEventCallbacks = events.nexts.toList.map(
      occuredEvent => occuredEvent -> ((nextMessage: Option[TermName]) => {
        val myQueue = nextEventsToQueues.get(occuredEvent).get
        val myPatterns = patterns.filter(pattern => pattern.events.contains(occuredEvent))
        myPatterns.map(pattern => {
          def patternCheck(pattern: Pattern): c.Tree = {
            pattern.events.toSeq.map(event => {
              val head = fresh("head")
              val queue = nextEventsToQueues.get(event).get
              q"""
              val $head = $queue.peek()
              if ($head == null) {
                return Right(false)
              } else if ($head.Status() == Status.claimed) {
                return Right(true)
              } else if($head.Status() == Status.pending) {
                msgs = head :: msgs
              }
              """
            })
          }
         
        })
        val backoff = fresh("backoff")
        val retry = fresh("retry")
        q"""
        def checkPattern(p: Pattern): Either[Seq[Message[Any]], Boolean] = {
      
          // Find pending head messages in the required buffer
          def findPending: Either[Seq[Message[Any]], Boolean] = {
            var msgs = Seq[Message[Any]].Empty
            // repeat for each involved event!!
            val $head = $queue.peek()
             if ($head == null) {
              return Right(false)
            } else if ($head.Status() == Status.claimed) {
              return Right(true)
            } else if($head.Status() == Status.pending) {
              msgs = head :: msgs
            }
            Left(msgs)
          }

          // Try to claim them, roll-back if failed, return messages
          findPending match {
            case Right(x) => return Right(x)
            case Left(msgs) => {
              val claimed = msgs.map(msg => if (!msg.tryClaim()) msg))
              if (claimed.size != msgs.size) {
                claimed.foreach(_.unclaim())
                return Right(true)
              } else {
                // maybe, dequeue them? Or immediately match here?
                return Left(claimed)
              }
            }
          }
        }

        // YOU WERE HERE: YOU NEED TO MACROIFY THIS CODE + Done / Error
        val $backoff = _root_.scala.async.Join.internal.Backoff()
        $myQueue.add(_root_.scala.async.Join.Message(${nextMessage.get}))

        @tailrec
        def resolve() = {
          var $retry = false
          myPatterns.foreach(p => {
            checkPattern(p) match {
              case Left(match) => 
                // MATCH
                return
              case Right(true) => { retry = true }
            }
          })
          if ($retry) {
            $backoff.once()
            resolve()
          }
        }
        resolve()
        """
        EmptyTree
    }))

    val eventCallbacks = nextEventCallbacks

    val subscriptions = generateSubscriptions(eventCallbacks.toMap, 
                            observablesToSubscriptions,
                            observablesToRequestables,
                            bufferSizeTree)

    val resultType = implicitly[WeakTypeTag[A]].tpe
    q"""
    val ${names.subjectVal} = _root_.rx.lang.scala.subjects.ReplaySubject[$resultType]()
    
    ..${nextEventsToQueues.map({ case (event, queue) => 
      val messageType = typeArgumentOf(event.source)
      q"val $queue = new _root_.java.util.concurrent.ConcurrentLinkedQueue[$messageType]"
    })}

    ${names.subjectVal}
    """ 
  }
}

trait LockTransform extends Transform { 
  self: JoinMacro with Parse with RxJavaSubscribeService with BackPressure with Util =>
  import c.universe._
  import scala.async.Join.{JoinReturn, Next => ReturnNext, Done => ReturnDone, Pass => ReturnPass}

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
    if ($matchExpression) {
        ..$body
        ${names.stateLockVal}.unlock()
        ..$continuation
        break
    }
   """
  }

  def generateReturnExpression(patternBody: c.Tree): c.Tree = parsePatternBody(patternBody) match {
    case (ReturnNext(returnExpr), block) => q"""
      ..$block
      try {
        ${names.subjectVal}.onNext($returnExpr)
      } catch {
        case e: Throwable => ${names.subjectVal}.onError(e)
      }"""
    case (ReturnDone, block) => q"""
      ..$block
      ${names.stop} = true
      ${names.subjectVal}.onCompleted()
      unsubscribe()
      """
    case (ReturnPass, block) => q"..$block"
  }
 
  override def joinTransform[A: c.WeakTypeTag](pf: c.Tree): c.Tree = {
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
    // and they happen only once per souce. Therefore no additional information needs to 
    // be stored other than their presence (which is already done so in the global state).
    val errorEventsToVars = 
      freshNames(events.errors, "error")
    // Every observable is subscribered to once. This results in a subscription later used
    // to unsubscribe, and free resources. We therefore need to store that subscription.
    val observables = events.groupBy(e => e.source).map(_._1)
    val observablesToSubscriptions =
      freshNames(observables, "subscription")
    // We need to store the subscriber to every observable so that we can handle back-pressure
    val observablesToRequestables = 
      freshNames(observables, "requestable")
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
            freshNames(otherEvents.nexts, "dequeuedMessage")
            .toList
          // Generate the deqeue statements. Further, update the state to reflect the removal of
          // the buffered events (set the event bit to 0 in case there are no more buffered messages).
          // We return a pair because of a problem of the creation of a Block by the quasiquotation if 
          // we put the statements into a single quasiquote.
          val dequeueStatements = dequeuedMessageVals.map({ case (event, name) =>
            val queue = nextEventsToQueues.get(event).get
            val requestMoreStats = if (unboundBuffer) EmptyTree else {
              val requestable = observablesToRequestables.get(event.source).get
              q"""
               $requestable.requestMore($bufferSizeTree)
              """
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
          val patternBody = generateReturnExpression(rawPatternBody)
          val requestMoreFromOccured = occuredEvent match {
            case event @ Next(source) if !unboundBuffer => 
              val occuredQueue = nextEventsToQueues.get(event).get
              val occuredRequestable = observablesToRequestables.get(source).get
              q"""
              if ($occuredQueue.isEmpty) {
                $occuredRequestable.requestMore($bufferSizeTree)
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
        ${names.stateLockVal}.lock()
        if (${names.stop}) {
          ${names.stateLockVal}.unlock()
        } else {
          val $possibleStateVal = ${names.stateVar} | ${eventsToIds.get(occuredEvent).get}
          breakable {
            ..$patternChecks
            // Reaching this line means that no pattern has matched, and we need to buffer the message
            $bufferStatement
            ${names.stateVar} = $possibleStateVal
            ${names.stateLockVal}.unlock()
          }
        }
      """
    }))
    // Generate unsubscribable for RxJava
    val subscriptions = 
      generateSubscriptions(eventCallbacks.toMap, 
                            observablesToSubscriptions,
                            observablesToRequestables,
                            bufferSizeTree)
    // Resolve the type of elements the generated observable will emit
    val resultType = implicitly[WeakTypeTag[A]].tpe
    // Assemble all parts into the full transform
    q"""
    import _root_.scala.util.control.Breaks._

    var ${names.stateVar} = 0L
    val ${names.stateLockVal} = new _root_.java.util.concurrent.locks.ReentrantLock
    val ${names.subjectVal} = _root_.rx.lang.scala.subjects.ReplaySubject[$resultType]()
    var ${names.stop} = false

    // Queue declarations for Next event messages
    ..${nextEventsToQueues.map({ case (event, queueName) =>
        val messageType = typeArgumentOf(event.source)
        q"val $queueName = _root_.scala.collection.mutable.Queue[$messageType]()"
      })}

    // Variable declarations to store Error event messages (throwables)
    ..${errorEventsToVars.map({ case (_, varName) => 
        q"var $varName: Throwable = null"
    })}

    ..$subscriptions
  
    def unsubscribe() = {..${
      observablesToSubscriptions.map({ case (_, s) => q"""
        $s.unsubscribe()
    """})}}

    ${names.subjectVal}
    """
  }
}