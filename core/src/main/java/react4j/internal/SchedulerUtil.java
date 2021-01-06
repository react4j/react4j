package react4j.internal;

import arez.Arez;
import arez.Disposable;
import arez.annotations.ArezComponent;
import javax.annotation.Nullable;
import zemeckis.Zemeckis;

/**
 * Utilities for interacting with the Arez scheduler.
 */
public final class SchedulerUtil
{
  /**
   * A non-null lock that will be released in the next micro-task which will schedule any renders required.
   */
  @Nullable
  private static Disposable c_schedulerLock;

  private SchedulerUtil()
  {
  }

  /**
   * The first time an react4j view is rendered it will lock the Arez scheduler and release
   * the lock in the micro-task immediately following the task that prompted the render. If this
   * is not done it is possible that Arez can re-trigger a view render when the scheduler is
   * triggered after the tracked render completes but before the render method has returned to the
   * react runtime. This results in error message from react as a setState()/forceRender() was invoked
   * while still within a render() method.
   *
   * <p>NOTE: While render methods are read-only transactions, they can un-observe components with
   * {@link ArezComponent#disposeOnDeactivate()} set to <code>true</code> that would result in the
   * arez component being disposed and triggering an update that would mark particular React
   * view/Observers as STALE and trigger a re-render of that view.</p>
   */
  public static void pauseUntilRenderLoopComplete()
  {
    if ( null == c_schedulerLock )
    {
      c_schedulerLock = Arez.context().pauseScheduler();
      // schedule immediately after this call stack pops.
      Zemeckis.microTask( () -> {
        c_schedulerLock.dispose();
        c_schedulerLock = null;
      } );
    }
  }
}
