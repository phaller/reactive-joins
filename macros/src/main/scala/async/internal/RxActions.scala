package scala.async.internal

trait RxActions {
  self: JoinMacro =>
  import c.universe._ 

   val emptyAction1 = q"new _root_.rx.functions.Action1[Any] { def call(x: Any) = {} }"
   val emptyThrowableAction1 = q"new _root_.rx.functions.Action1[Throwable] { def call(x: Throwable) = {} }"
   val emptyAction0 = q"new _root_.rx.functions.Action0 { def call() = {} }"
}