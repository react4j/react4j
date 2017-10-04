package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsFunction;

/**
 * Annotation used to specify a callback for a react event handler.
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
public @interface EventHandler
{
  /**
   * Return the name of the callback.
   * The value defaults to the name of the method. The value must conform to the
   * requirements of a java identifier. It should also be unique across the component
   * but this is not strictly required as the name is only used for development
   * purposes. (i.e. This is the name that is used within the React DevTools).
   *
   * @return the name of the event handler.
   */
  @Nonnull
  String name() default "<default>";

  /**
   * The type of the callback.
   * The type should be an interface and the interface should be annotated with
   * {@link jsinterop.annotations.JsFunction} to ensure that the method can be
   * given a useful debug name.
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
