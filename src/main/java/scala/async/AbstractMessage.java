// Source: https://github.com/scala/scala/blob/2.11.x/src/library/scala/concurrent/impl/AbstractPromise.java
// We need to use Java because the Unsafe.CAS requires static fields which do not exist in Scala.
package scala.async;

import scala.concurrent.util.Unsafe;

abstract class AbstractMessage {
  private volatile Object _ref;
  final static long _refoffset;
  // Static initializer: executed when the class is loaded.
  static {
    try {
      _refoffset = Unsafe.instance.objectFieldOffset(AbstractMessage.class.getDeclaredField("_ref"));
    } catch (Throwable t) {
      throw new ExceptionInInitializerError(t);
    }
  }
  protected final boolean updateState(Object oldState, Object newState) {
    return Unsafe.instance.compareAndSwapObject(this, _refoffset, oldState, newState);
  }
  protected final Object getState() {
    return _ref;
  }
}