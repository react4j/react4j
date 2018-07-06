package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsFunction;

/**
 * Annotation used to specify a callback passed to react components.
 * This annotation used to generate a callback accessor method in the
 * helper class. The callback accessor method will return the same callback
 * instance each time it is called, making this ideal for use in "PureComponent"
 * style components. If the callback is annotated with <code>@JsFunction</code>
 * then the code gives the callback a user friendly name for debugging. The
 * code for specifying the friendly name will be optimized away when
 * ReactConfig.enableComponentNames() returns false.
 *
 * <p>Using this annotation and the corresponding helper method is entirely
 * optional but does provide some value when building components that you
 * want to easily interact with from the React DevTools or building a component
 * that compares props values to determine whether it needs to re-render.</p>
 *
 * <p>The method that is annotated with <code>@Callback</code> must also comply with the following constraints:</p>
 * <ul>
 * <li>Must not be annotated with any other react annotation</li>
 * <li>Must not be an abstract instance method</li>
 * <li>Must not throw exceptions</li>
 * <li>Must be accessible from the same package as the class annotated by {@link ReactComponent}</li>
 * <li>Must have 0 parameters or the same set of parameters as the callback type</li>
 * <li>Must return a value of the same type as the callback type if any</li>
 * </ul>
 */
@Documented
@Target( ElementType.METHOD )
public @interface Callback
{
  /**
   * Return the name of the callback.
   * The value defaults to the name of the method. The value must conform to the
   * requirements of a java identifier and be unique across the component.
   *
   * @return the name of the callback.
   */
  @Nonnull
  String name() default "<default>";

  /**
   * The type of the callback.
   * The type should be an interface and the interface should be annotated with
   * {@link jsinterop.annotations.JsFunction} to ensure that the method can be
   * given a useful debug name.
   *
   * @return the type of the callback.
   */
  Class value() default Procedure.class;

  /**
   * The flag indicates whether the annotation processor should manage the calling context.
   * if set to {@link Feature#ENABLE} then caller context will be setup. In practical terms
   * this means that subclasses of <code>ReactArezComponent</code> will have the callback
   * annotated with an <code>arez.annotations.Action</code> annotation. if set to {@link Feature#DISABLE}
   * then the code that invokes the callback is responsible for setting up the calling context. This is
   * typically used when you are passing a "render" prop into a component that invokes the callback to
   * render react4j.ReactNode. The {@link Feature#AUTODETECT} value will detect whether the component needs
   * calling context setup and do so if necessary. At the moment this means that subclasses of
   * <code>ReactArezComponent</code> are enabled.
   *
   * @return the flag to control calling context.
   */
  Feature initCallbackContext() default Feature.AUTODETECT;

  /**
   * Interface for performing an action that does not return a value.
   */
  @FunctionalInterface
  @JsFunction
  interface Procedure
  {
    /**
     * Perform an action.
     */
    void call();
  }
}
