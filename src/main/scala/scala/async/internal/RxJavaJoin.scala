package scala.async.internal

object RxJavaJoin extends JoinBase {
  type RS = RxJavaSystem.type
  val reactiveSystem: RxJavaSystem
}