package react4j.internal.arez;

/**
 * Constants representing the state of a react component.
 * Used by the generated classes.
 */
public final class ComponentState
{
  public static final int IDLE = 0;
  /**
   * The component needs to be re-rendered, a dependency has updated.
   */
  public static final int SCHEDULED = 1;
  /**
   * The component has been unmounted and should not be re-rendered.
   */
  public static final int UNMOUNTED = 2;

  private ComponentState()
  {
  }
}
