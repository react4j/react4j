package react4j.annotations;

/**
 * <p>SortOrder define the orders in which methods are
 * invoked. These values are intended to be used with the
 * {@link PreRender} and {@link PostRender} annotations.
 *
 * <p>An extension library might define an PreRender method like this:</p>
 *
 * <pre>
 * &#064;PreRender(sortOrder=Interceptor.Priority.LIBRARY_BEFORE+10)
 * public interface MyExtension {
 *   &#064;PreRender(sortOrder=Interceptor.Priority.LIBRARY_BEFORE+10)
 *   void preRenderMyExtension() { ... };
 *  }
 * </pre>
 *
 * @see PreRender
 * @see PostRender
 */
public class SortOrder
{
  private SortOrder()
  {
  }  // don't allow instances

  /**
   * Start of range for early methods defined by extension libraries.
   */
  public static final int LIBRARY_BEFORE = 1000;
  /**
   * Start of range for methods defined by applications.
   */
  public static final int APPLICATION = 2000;
  /**
   * Start of range for late methods defined by extension libraries.
   */
  public static final int LIBRARY_AFTER = 3000;
}
