package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsFunction;

/**
 * Annotation used to specify a callback passed to react components.
 * This is used to generate a helper method in factory class that wraps
 * the call back and gives it a user friendly name for debugging. This
 * will be optimized away when ReactConfig.enableComponentNames() returns false.
 *
 * <p>Using this annotation and the corresponding helper method is entirely
 * optional but does provide some value when building components that you
 * want to easily interact with from the React DevTools.</p>
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
