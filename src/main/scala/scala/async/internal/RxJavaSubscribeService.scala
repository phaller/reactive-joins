package scala.async.internal

trait RxJavaSubscribeService {
  self: JoinMacro with Util =>
  import c.universe._

  type EventCallback = Option[Option[TermName] => c.Tree]

  def generateSubscription(joinObservable: Symbol, subscriber: TermName, onNext: EventCallback, onError: EventCallback, onDone: EventCallback, initialRequest: c.Tree): c.Tree = {
    val obsTpe = typeArgumentOf(joinObservable)
    val nextMessage = fresh("nextMessage")
    val errorMessage = fresh("errorMessage")
    // onNext
    val next = onNext match {
      case Some(callback) => 
        q"""${insertIfTracing(q"""debug("Received: " + $nextMessage.toString())""")}
            ${callback(Some(nextMessage))}"""
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
    // In case we only have subscriptions do onError, or onDone then we can
    // request a maximum number of events since they both only occur once.
    // If we do not do this than a join wainting only for onDone, or onError would be
    // stuck unless the onDone, or onError event is the only, and first event to 
    // be emitted.
    q"""
    $subscriber = new _root_.rx.lang.scala.Subscriber[$obsTpe] with Requestable {
        override def onStart(): Unit = request($initialRequest)
        $overrideNext
        $overrideError
        $overrideDone
        def requestMore(n: Long) = request(n)
    }
    $joinObservable.observable.subscribe($subscriber)
    """ 
  }
}