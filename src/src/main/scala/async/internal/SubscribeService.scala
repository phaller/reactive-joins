package scala.async.internal

trait SubscribeService {
  self: JoinMacro =>
  import c.universe._

  type EventCallback = Option[Option[TermName] => c.Tree]

  def generateSubscription(joinObservable: Symbol, onNext: EventCallback, onError: EventCallback, onDone: EventCallback): Option[TermName] => c.Tree
}

trait RxJavaSubscribeService extends SubscribeService {
  self: JoinMacro with Util =>
  import c.universe._

  def emptyAction0 = q"new _root_.rx.functions.Action0 { def call() = {} }"
  def emptyAction1 = q"new _root_.rx.functions.Action1[Any] { def call(x: Any) = {} }"
  def emptyThrowableAction1 = q"new _root_.rx.functions.Action1[Throwable] { def call(x: Throwable) = {} }"
  
  def generateSubscription(joinObservable: Symbol, onNext: EventCallback, onError: EventCallback, onDone: EventCallback): Option[TermName] => c.Tree = {
    val next = onNext match {
      case Some(callback) => 
        val obsTpe = typeArgumentOf(joinObservable)
        val nextMessage = fresh("nextMessage")
        q"""
          new _root_.rx.functions.Action1[$obsTpe] {
            def call($nextMessage: $obsTpe) = {
              ${callback(Some(nextMessage))}
            }
          }
        """
      case None => emptyAction1
    }
    val error = onError match {
      case Some(callback) =>  
        val nextMessage = fresh("nextMessage")
        q"""
          new _root_.rx.functions.Action1[Throwable] {
            def call($nextMessage: Throwable) = {
              ${callback(Some(nextMessage))}
            }
          }
        """
      case None => emptyThrowableAction1
    }
    val done = onDone match {
      case Some(callback) =>  q"""
        new _root_.rx.functions.Action0 {
          def call() = {
            ${callback(None)}
          }
        }
      """
      case None => emptyAction0
    }
    val subscribe = q"$joinObservable.observable.subscribe($next, $error, $done)"

    (result: Option[TermName]) => result match {
      case Some(valName) => q"val $valName = $subscribe"
      case None => subscribe
    }  
  }
}