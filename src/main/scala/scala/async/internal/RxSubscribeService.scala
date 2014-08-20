package scala.async.internal

trait RxJavaSubscribeService {
  self: JoinMacro with Util =>
  import c.universe._

  type EventCallback = Option[Option[TermName] => c.Tree]

  def generateSubscription(joinObservable: Symbol, onNext: EventCallback, onError: EventCallback, onDone: EventCallback): c.Tree = {
    val next = onNext match {
      case Some(callback) => 
        val obsTpe = typeArgumentOf(joinObservable)
        val nextMessage = fresh("nextMessage")
        q"($nextMessage: $obsTpe) => ${callback(Some(nextMessage))}"
      case None => q"(_: Any) => ()"
    }
    val error = onError match {
      case Some(callback) =>  
        val nextMessage = fresh("nextMessage")
        q"($nextMessage: Throwable) => ${callback(Some(nextMessage))}"
      case None =>  q"(_: Throwable) => ()"
    }
    val done = onDone match {
      case Some(callback) =>  q"() => ${callback(None)}"
      case None => q"() => ()"
    }
    q"$joinObservable.observable.subscribe($next, $error, $done)" 
  }
}