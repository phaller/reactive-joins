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
  self: JoinMacro with Util with Parse =>
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