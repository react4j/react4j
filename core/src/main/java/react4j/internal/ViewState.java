package react4j.internal;

/**
 * Constants representing the state of a view.
 * Used by the generated classes.
 */
public final class ViewState
{
  public static final int IDLE = 0;
  /**
   * The view needs to be re-rendered, a dependency has updated.
   */
  public static final int SCHEDULED = 1;
  /**
   * The view has been unmounted and should not be re-rendered.
   */
  public static final int UNMOUNTED = 2;

  private ViewState()
  {
  }
}
