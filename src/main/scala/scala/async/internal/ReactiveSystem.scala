package scala.async.internal 

trait ReactiveSystem {
  
  // not sure about the type parameter
  type Publisher
  type Processor
  type Subscriber

  trait Ops {
    def createSubscriber(
      onNext: Option[Expr[A => Unit]], 
      onError: Option[Expr[Throwable => Unit]], 
      onDone: Option[ => Expr[Unit]]): Expr[Subscriber]

    def subscribe(
      publisher: Expr[Publisher[A]], 
      subscriber: Expr[Subscriber], 
      initalRequest: Option[Expr[Long]]): Expr[Unit]
    
    def unsubscribe(handle: Expr[Subscriber]): Expr[Unit]
    def requestMore(handle: Expr[Subscriber], count: Expr[Long]): Expr[Unit]

    def createProcessor[A](firstSubscribe: [Expr[Unit]]): Expr[Processor]
    def next(s: Expr[Processor[B]], Expr[B]): Expr[Unit]
    def error(s: Expr[Processor[B]], Expr[Throwable]): Expr[Unit]
    def done(s: Expr[Processor[B]]): Expr[Unit]
  }
}

trait RxJavaSystem extends ReactiveSystem {
  self: JoinMacro =>

  import c.universe._

  type Publisher = rx.lang.scala.Observable[A]
  type Processor = rx.lang.scala.Subject[B]
  type Subscriber = RequestableSubscriber

  trait RequestableSubscriber extends rx.lang.scla.Subscriber[A] {
    def requestMore(n: Long) = request(n) // thread safe?
  }
}