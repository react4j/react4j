package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Identifies a method that is called when the value of one of the specified props is changed.
 * Each parameter of the method is mapped to a prop that is tracked. The method will be called
 * before or after the update has occurred and will be passed the previous values of the props
 * as parameters.
 *
 * <p>Each parameter is expected to be named according to the pattern "last[MyProp]", "prev[MyProp]"
 * or "[myProp]". If name of the parameter does not follow the pattern then the name can be explicitly
 * mapped via the {@link PropRef} annotation on a parameter.</p>
 *
 * <p>The method must also conform to the following constraints:</p>
 * <ul>
 * <li>Must not be annotated with any other react4j annotation</li>
 * <li>Must have 1 or more parameters. Each parameter must map to a prop as described above.</li>
 * <li>Must not return a value</li>
 * <li>Must not be private</li>
 * <li>Must not be public</li>
 * <li>Must not be static</li>
 * <li>Must not be abstract</li>
 * <li>Must not throw exceptions</li>
 * <li>Must be accessible from the same package as the class annotated by {@link ReactComponent}</li>
 * </ul>
 */
@Documented
@Target( ElementType.METHOD )
public @interface OnPropChange
{
  /**
   * Phase in which method should be invoked.
   */
  enum Phase
  {
    /**
     * Method should be invoked before update.
     */
    PRE,
    /**
     * Method should be invoked after update.
     */
    POST
  }

  /**
   * The phase in which the method should be invoked.
   *
   * @return the phase in which the method should be invoked.
   */
  Phase phase() default Phase.PRE;
}
