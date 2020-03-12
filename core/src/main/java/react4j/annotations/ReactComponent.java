package react4j.annotations;

import arez.annotations.Observe;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Annotation used to annotate a React component.
 */
@Documented
@Target( ElementType.TYPE )
public @interface ReactComponent
{
  /**
   * Enum indicating type of component.
   */
  enum Type
  {
    /**
     * The component can store state in fields, can declared lifecycle methods and may invoke scheduleRender() method.
     * The component will be scheduled to re-render if the components container re-renders and any props with
     * {@link Prop#shouldUpdateOnChange()} set to <code>true</code> has changed value from last render. A prop
     * is considered changed if when the new value and the old value are passed to
     * {@link Objects#equals(Object, Object)} and that method returns <code>false</code>. A component will also
     * be scheduled to re-render if the component invokes {@code Component.scheduleRender()}.
     */
    STATEFUL,
    /**
     * The component is a {@link #STATEFUL} component <b>and</b> will re-render the component if any Arez
     * dependencies change. The framework uses the {@link Observe} annotation to track Arez dependencies.
     */
    TRACKING,
    /**
     * The component is a {@link #TRACKING} component but it will not generate an invariant failure if a
     * render results in zero Arez dependencies.
     */
    MAYBE_TRACKING
  }

  /**
   * Return the name of the component.
   * The value defaults to the simple name name of the class. If the value is specified, the
   * value must conform to the requirements of a java identifier. It should also be unique
   * across the suite of components used within an application but this is not strictly
   * required as the name is only used for development purposes. (i.e. This is the name
   * that is used within the React DevTools).
   *
   * @return the name of the component.
   */
  @Nonnull
  String name() default "<default>";

  /**
   * Enum indicating the capabilities of the component.
   * See the {@link Type} enum for further details.
   *
   * @return an enum indicating the capabilities of the component.
   * @see Type
   */
  @Nonnull
  Type type() default Type.STATEFUL;

  /**
   * Enum controlling whether javax.inject integration is generated for the component.
   * The generated class is the same name as the component with the "Factory" suffix.
   * React4j only supports constructor injection and thus this MUST NOT be set to {@link Feature#ENABLE}
   * unless there are constructor parameters on the component. If the value is set to
   * {@link Feature#AUTODETECT} then the feature will be enabled if the component has
   * constructor parameters and the {@code javax.inject.Inject} class is present when
   * compiling the component.
   *
   * @return an enum controlling whether javax.inject integration is generated for the component.
   */
  Feature inject() default Feature.AUTODETECT;

  /**
   * Enum controlling whether Sting injection integration is generated for the component.
   * The generated class is the same name as the component with the "Factory" suffix.
   * React4j only supports constructor injection and thus this MUST NOT be set to {@link Feature#ENABLE}
   * unless there are constructor parameters on the component. If the value is set to
   * {@link Feature#AUTODETECT} then the feature will be enabled if the component has
   * constructor parameters and the {@code sting.Injectable} class is present when compiling
   * the component.
   *
   * @return an enum controlling whether sting integration is generated for the component.
   */
  Feature sting() default Feature.AUTODETECT;
}
