package scala.async.internal

trait Transform {
  self: JoinMacro =>
  def joinTransform[T: c.WeakTypeTag](pf: c.Tree): c.Tree
}

trait LockTransform extends Transform { 
  self: JoinMacro with Parse with SubscribeService with Util =>
  import c.universe._

  object names { 
    val stateVar = fresh("state")
    val stateLockVal = fresh("stateLock")
  }

  def generatePatternCheck(patternId: Long, state: TermName) = (body: c.Tree, continuation: c.Tree) => q"""
    if ((~$state & $patternId) == 0) {
        ..$body
        ${names.stateLockVal}.release()
        ..$continuation
        break
    }
   """
  override def joinTransform[T: c.WeakTypeTag](pf: c.Tree): c.Tree = {
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
          val checkExpression = generatePatternCheck(patternsToIds.get(myPattern).get, possibleStateVal)
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
          val patternBody = replaceSymbolsWithTrees(symbolsToReplace, ids, myPattern.bodyTree)
          checkExpression(
           q"""..${dequeueStatements.map({ case (stats, _) => stats })}
               ..${dequeueStatements.map({ case (_, stats) => stats })}""", 
            patternBody
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
        ${names.stateLockVal}.acquire()
        val $possibleStateVal = ${names.stateVar} | ${eventsToIds.get(occuredEvent).get}
        breakable {
          ..$patternChecks
          // Reaching this line means that no pattern has matched, and we need to buffer the message
          $bufferStatement
          ${names.stateVar} = $possibleStateVal
          ${names.stateLockVal}.release()
          }
      """
    }))
    // Generate subscriptions for the underlaying reactive system.
    val observablesToEvents = eventCallbacks.groupBy({ case (event, _) => event.source }) 
    
    val subscriptions = observablesToEvents.map({ case (obsSym, events) => 
      val next = events.find(event => event._1.isInstanceOf[Next]).map(_._2)
      val error = events.find(event => event._1.isInstanceOf[Error]).map(_._2)
      val done = events.find(event => event._1.isInstanceOf[Done]).map(_._2)
      generateSubscription(obsSym, next, error, done)
    })
    
    q"""
    import _root_.scala.util.control.Breaks._
    import _root_.scala.collection.mutable

    var ${names.stateVar} = 0L
    val ${names.stateLockVal} = new _root_.scala.concurrent.Lock()

    // Queue declarations for Next event messages
    ..${nextEventsToQueues.map({ case (event, queueName) =>
        val messageType = typeArgumentOf(event.source)
        q"val $queueName = mutable.Queue[$messageType]()"
      })}

    // Variable declarations to store Error event messages (throwables)
    ..${errorEventsToVars.map({ case (event, varName) => 
        q"var $varName: Throwable = null"
      })}

    ..${subscriptions.map(s => s(None))}
    
    0
    """
  }
}