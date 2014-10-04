package scala.async.internal 

trait ReactiveSystem {

  def createVariableStoringSubscriber: (TermName) => Tree

  type EventCallback = Option[TermName] => Tree

  def createSubscriber(
    onNext: (Option[EventCallback], Type), 
    onError: Option[EventCallback], 
    onDone: Option[EventCallback],
    initialRequest: Option[Tree]): Tree

  def createPublisher(onSubscribe: TermName => Tree): Tree

  def subscribe(
    publisher: Tree, 
    subscriber: Tree, 
    initalRequest: Option[Tree]): Tree
  
  def unsubscribe(subscriber: Tree): Tree
  def requestMore(subscriber: Tree, count: Tree): Tree

  def next(subscriber: Tree, value: Tree): Tree
  def error(subscriber: Tree, throwable: Tree): Tree
  def done(subscriber: Tree): Tree
}

trait RxJavaSystem extends ReactiveSystem {
  self: JoinMacro =>

  import c.universe._

  def createVariableStoringSubscriber: (TermName) => Tree = 
    (name: TermName) => q"var $name: _root_.rx.lang.scala.Subscriber[$onNextTpe] with _root_.rx.lang.scala.SubscriberAdapter[$onNextTpe] = null"
  
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
    new _root_.rx.lang.scala.Subscriber[$onNextTpe] with _root_.rx.lang.scala.SubscriberAdapter[$onNextTpe] {
      $overrideStart
      $overrideNext
      $overrideError
      $overrideDone
      def requestMore(n: Long) = request(n)
    }
    """
  }
  def subscribe(publisher: Tree, subscriber: Tree): Tree = q"($publisher).subscribe($subscriber)"
  def unsubscribe(subscriber: Tree): Tree = q"($subscriber).unsubscribe()"
  def requestMore(subscriber: Tree, value: Tree) = q"($subscriber).requestMore($value)"

  def createPublisher(onSubscribe: TermName => Tree): Tree = q"""
    _root_.rx.lang.scala.Observable.create(${onSubscribe(fresh("subscriber"))})
  """

  def next(subscriber: Tree, value: Tree): Tree = q"$subscriber.onNext($value)"
  def error(subscriber: Tree, throwable: Tree): Tree = q"$subscriber.onError($throwable)"
  def done(subscriber: Tree): Tree = q"$subscriber.onCompleted()"
}