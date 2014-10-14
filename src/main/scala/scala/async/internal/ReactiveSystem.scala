package scala.async.internal 

trait ReactiveSystem {
  self: JoinMacro =>
  import c.universe._

  def createVariableStoringSubscriber: (TermName, Type) => Tree
  
  type EventCallback = Option[TermName] => Tree

  def createSubscriber(
    onNext: (Option[EventCallback], Type), 
    onError: Option[EventCallback], 
    onDone: Option[EventCallback],
    initialRequest: Option[Tree]): Tree 

  def subscribe(joinObservable: TermName, subscriber: TermName): Tree
  def unsubscribe(subscriber: TermName): Tree
  def isUnsubscribed(subscriber: TermName): Tree

  def requestMore(subscriber: TermName, value: Tree): Tree

  def createPublisher(onSubscribe: Tree, argument: TermName, tpe: Type): Tree

  def next(subscriber: TermName, value: Tree): Tree
  def error(subscriber: TermName, throwable: Tree): Tree
  def done(subscriber: TermName): Tree
}

trait RxJavaSystem extends ReactiveSystem {
  self: JoinMacro =>
  import c.universe._

  def createVariableStoringSubscriber: (TermName, Type) => Tree = 
    (name: TermName, tpe: Type) => q"var $name: _root_.scala.async.internal.imports.SubscriberAdapter[$tpe] = null"
  
  def createSubscriber(
    onNext: (Option[EventCallback], Type), 
    onError: Option[EventCallback], 
    onDone: Option[EventCallback],
    initialRequest: Option[Tree]): Tree = {
    val nextMessage = fresh("nextMessage")
    val errorMessage = fresh("errorMessage")
    // onNext
    val (onNextCallback, onNextTpe) = onNext
    val next = onNextCallback match {
      case Some(callback) => 
        q"${callback(Some(nextMessage))}"
      case None => q"()"
    }
    val overrideNext = q"override def onNext($nextMessage: $onNextTpe): Unit = $next"
    // onError
    val error = onError match {
      case Some(callback) => q"${callback(Some(errorMessage))}"
      case None =>  q"()"
    }
    val overrideError = q"override def onError($errorMessage: Throwable): Unit = $error"
    // onDone
    val done = onDone match {
      case Some(callback) =>  q"${callback(None)}"
      case None => q"()"
    }
    val overrideDone = q"override def onCompleted(): Unit = $done"
    // onStart
    val start = initialRequest match {
      case Some(init) => q"request($init)"
      case None => q"()"
    }
    val overrideStart = q"override def onStart(): Unit = $start"
    q"""
    new _root_.scala.async.internal.imports.SubscriberAdapter[$onNextTpe] {
      $overrideStart
      $overrideNext
      $overrideError
      $overrideDone
      def requestMore(n: Long) = request(n)
    }
    """
  }

  def subscribe(joinObservable: TermName, subscriber: TermName): Tree = q"$joinObservable.observable.subscribe($subscriber)"
  def unsubscribe(subscriber: TermName): Tree = q"$subscriber.unsubscribe()"
  def isUnsubscribed(subscriber: TermName): Tree = q"$subscriber.isUnsubscribed"

  def requestMore(subscriber: TermName, value: Tree): Tree = q"$subscriber.asInstanceOf[_root_.scala.async.internal.imports.SubscriberAdapter[_]].requestMore($value)"

  def createPublisher(onSubscribe: Tree, argument: TermName, tpe: Type): Tree = 
    q"_root_.rx.lang.scala.Observable(($argument: _root_.rx.lang.scala.Subscriber[$tpe]) => { $onSubscribe })"

  def next(subscriber: TermName, value: Tree): Tree = q"$subscriber.onNext($value)"
  def error(subscriber: TermName, throwable: Tree): Tree = q"$subscriber.onError($throwable)"
  def done(subscriber: TermName): Tree = q"$subscriber.onCompleted()"
}


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