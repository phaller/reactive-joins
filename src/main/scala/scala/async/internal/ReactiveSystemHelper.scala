package scala.async.internal 

trait ReactiveSystemHelper {
  self: JoinMacro with ReactiveSystem with Util => 
  import c.universe._
  import scala.async.Join.{JoinReturn, Next => ReturnNext, Done => ReturnDone, Pass => ReturnPass, Last => ReturnLast}

  def generateReturnExpression(parsedPatternBody: (JoinReturn[Tree], List[Tree]), outSubscriber: TermName, afterDone: Option[Tree] = None): Tree = parsedPatternBody match {
    case (ReturnNext(returnExpr), block) =>
      val exceptionName = fresh("ex")
      q"""
      ..$block
      try {
        ${next(outSubscriber, returnExpr)}
      } catch {
        case $exceptionName: Throwable => 
        ${error(outSubscriber, q"$exceptionName")}
      }"""
    case (ReturnLast(returnExpr), block) => q"""
      ..$block
      ${next(outSubscriber, returnExpr)}
      ${done(outSubscriber)}
      ${afterDone.getOrElse(EmptyTree)}
    """
    case (ReturnDone, block) => q"""
      ..$block
      ${done(outSubscriber)}
      ${afterDone.getOrElse(EmptyTree)}
      """
    case (ReturnPass, block) => q"""
      ..$block
      """
  }

  def generateSubscribervarDeclarations(observablesToSubscribers: Map[Symbol, TermName]) = observablesToSubscribers.map({ case (observable, subscriber) =>
    val obsTpe = typeArgumentOf(observable)
    createVariableStoringSubscriber(subscriber, obsTpe)
  })

  def generateSubscriberDeclarations(observablesToSubscribers: Map[Symbol, TermName], eventCallbacks: List[(Event, EventCallback)], bufferSizeTree: Option[Tree] = None) = {
    // Group the event call backs we created by their source observable
    val observablesToEventCallbacks = eventCallbacks.groupBy({ case (event, _) => event.source })
    // Create a Subscriber for every observable
    observablesToEventCallbacks.map({ case (obsSym, events) => 
      val next = events.find(event => event._1.isInstanceOf[Next]).map(_._2)
      val error = events.find(event => event._1.isInstanceOf[Error]).map(_._2)
      val done = events.find(event => event._1.isInstanceOf[Done]).map(_._2)
      val nextMessageType = typeArgumentOf(obsSym)
      val subscriberDecl = createSubscriber((next, nextMessageType), error, done, bufferSizeTree)
      val subscriberVar = observablesToSubscribers.get(obsSym).get
      q"$subscriberVar = $subscriberDecl"
    })
  }
    
  def generateObservableSubscriptions(observablesToSubscribers: Map[Symbol, TermName]) = {
    // We generate code which subscribes the created subscribers to their observable
    observablesToSubscribers.map({ case (observable, subscriber) =>
      subscribe(observable.asTerm.name, subscriber)
    })  
  }
}