package react4j.annotations;

import arez.annotations.Observe;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Annotation used to identify a React4j view.
 */
@Documented
@Target( ElementType.TYPE )
@ActAsStingComponent
public @interface View
{
  /**
   * Enum indicating type of view.
   */
  enum Type
  {
    /**
     * The view can store state in fields, can declared lifecycle methods and may contain methods annotated by
     * the {@link ScheduleRender} annotation.
     * The view will be scheduled to re-render if the views container re-renders and any input has changed
     * value from last render. A input is considered changed when the new value and the old value are passed to
     * {@link Objects#equals(Object, Object)} and that method returns <code>false</code>. A view will also
     * be scheduled to re-render if the view invokes a method annotated by the {@link ScheduleRender} annotation.
     */
    STATEFUL,
    /**
     * The view is a {@link #STATEFUL} view <b>and</b> will re-render the view if any Arez
     * dependencies change. The framework uses the {@link Observe} annotation to track Arez dependencies.
     */
    TRACKING,
    /**
     * The view is a {@link #TRACKING} view but it will not generate an invariant failure if a
     * render results in zero Arez dependencies.
     */
    MAYBE_TRACKING,
    /**
     * The view must not have a method annotated with {@link Render}.
     * This is useful for "view" instances that provide capabilities or interact
     * with services and thus need not have a render method.
     */
    NO_RENDER
  }

  /**
   * Return the name of the view.
   * The value defaults to the simple name of the class. If the value is specified, the
   * value must conform to the requirements of a java identifier. It should also be unique
   * across the suite of views used within an application but this is not strictly
   * required as the name is only used for development purposes. (i.e. This is the name
   * that is used within the React DevTools).
   *
   * @return the name of the view.
   */
  @Nonnull
  String name() default "<default>";

  /**
   * Enum indicating the capabilities of the view.
   * See the {@link Type} enum for further details.
   *
   * @return an enum indicating the capabilities of the view.
   * @see Type
   */
  @Nonnull
  Type type() default Type.STATEFUL;

  /**
   * Enum controlling whether javax.inject integration is generated for the view.
   * The generated class is the same name as the view with the "Factory" suffix.
   * React4j only supports constructor injection and thus this MUST NOT be set to {@link Feature#ENABLE}
   * unless there are constructor parameters on the view. If the value is set to
   * {@link Feature#AUTODETECT} then the feature will be enabled if the view has
   * constructor parameters and the {@code javax.inject.Inject} class is present when
   * compiling the view.
   *
   * @return an enum controlling whether javax.inject integration is generated for the view.
   */
  Feature inject() default Feature.AUTODETECT;

  /**
   * Enum controlling whether Sting injection integration is generated for the view.
   * The generated class is the same name as the view with the "Factory" suffix.
   * React4j only supports constructor injection and thus this MUST NOT be set to {@link Feature#ENABLE}
   * unless there are constructor parameters on the view. If the value is set to
   * {@link Feature#AUTODETECT} then the feature will be enabled if the view has
   * constructor parameters and the {@code sting.Injectable} class is present when compiling
   * the view.
   *
   * @return an enum controlling whether sting integration is generated for the view.
   */
  Feature sting() default Feature.AUTODETECT;
}
