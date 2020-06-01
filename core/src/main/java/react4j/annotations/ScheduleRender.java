package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Identify a method that can be invoked to schedule the component for re-rendering.
 * This annotation is rarely required as the underlying arez reactivity infrastructure
 * should be used in preference to this method. The method primarily exists for backwards
 * compatibility with earlier versions of the framework.
 *
 * <p>The method must also conform to the following constraints:</p>
 * <ul>
 * <li>Must not be annotated with any other react4j annotation</li>
 * <li>Must be abstract</li>
 * <li>Must have 0 parameters</li>
 * <li>Must not return a value</li>
 * <li>Must not be static</li>
 * <li>Must not throw exceptions</li>
 * <li>Must be accessible from the same package as the class annotated by {@link ReactComponent}</li>
 * <li>
 *   Should not be public as not expected to be invoked outside the component. A warning will be generated but can
 *   be suppressed by the {@link SuppressWarnings} or {@link SuppressReact4jWarnings} annotations with a key
 *   "React4j:PublicMethod". This warning is also suppressed by the annotation processor if it is implementing
 *   an interface method.
 * </li>
 * <li>
 *   Should not be protected if enclosed in the class annotated with the {@link ReactComponent} annotation as the
 *   method is not expected to be invoked outside the component. A warning will be generated but can be suppressed
 *   by the {@link SuppressWarnings} or {@link SuppressReact4jWarnings} annotations with a
 *   key "React4j:ProtectedMethod".
 * </li>
 * </ul>
 */
@Documented
@Target( ElementType.METHOD )
public @interface ScheduleRender
{
  /**
   * Determines whether the component will invoke the <code>shouldComponentUpdate()</code> before invoking the render method.
   *
   * @return true to skip "shouldComponentUpdate" phase, false otherwise.
   */
  boolean skipShouldComponentUpdate() default true;
}
