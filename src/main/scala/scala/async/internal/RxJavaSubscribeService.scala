package scala.async.internal

trait RxJavaSubscribeService {
  self: JoinMacro with Util =>
  import c.universe._

  type EventCallback = Option[TermName] => c.Tree

  def generateSubscriptions(eventCallbacks: Map[Event, EventCallback],
                            observablesToUnsubscribables: Map[Symbol, TermName],
                            observablesToRequestables: Map[Symbol, TermName],
                            bufferSizeTree: Tree) = {
    val observablesToEventCallbacks = 
      eventCallbacks.groupBy({ case (event, _) => event.source })
    // We need to separate the different event call-back types, and
    val subscriptions = observablesToEventCallbacks.map({ case (obsSym, events) => 
      val next = events.find(event => event._1.isInstanceOf[Next]).map(_._2)
      val error = events.find(event => event._1.isInstanceOf[Error]).map(_._2)
      val done = events.find(event => event._1.isInstanceOf[Done]).map(_._2)
      val subscription = observablesToUnsubscribables.get(obsSym).get
      val requestable = observablesToRequestables.get(obsSym).get
      q"$subscription = ${generateSubscription(obsSym, requestable, next, error, done, bufferSizeTree)}"
    })
    val subscriptionVarDefs = observablesToUnsubscribables.map({ case (_, subscription) => 
      q"var $subscription: _root_.scala.async.Join.Unsubscribable = null"
    })
    val requestableVarDefs = observablesToRequestables.map({ case (_, requestable) =>
      q"var $requestable: _root_.scala.async.Join.Requestable = null"
    })
    q"""
      ..$subscriptionVarDefs
      ..$requestableVarDefs
      ..$subscriptions
    """
  }

  private def generateSubscription(joinObservable: Symbol, requestable: TermName, onNext: Option[EventCallback], onError: Option[EventCallback], onDone: Option[EventCallback], initialRequest: c.Tree): c.Tree = {
    val obsTpe = typeArgumentOf(joinObservable)
    val nextMessage = fresh("nextMessage")
    val errorMessage = fresh("errorMessage")
    // onNext
    val next = onNext match {
      case Some(callback) => 
        q"${callback(Some(nextMessage))}"
      case None => q"()"
    }
    val overrideNext = q"override def onNext($nextMessage: $obsTpe): Unit = $next"
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
    q"""
    $requestable = new _root_.rx.lang.scala.Subscriber[$obsTpe] with _root_.rx.lang.scala.SubscriberAdapter[$obsTpe] {
        override def onStart(): Unit = request($initialRequest)
        $overrideNext
        $overrideError
        $overrideDone
        def requestMore(n: Long) = request(n)
    }
    """ 
  }
}