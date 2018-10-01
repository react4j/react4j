package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation used to annotate a pair of methods to access and mutate reacts underlying state object.
 * The annotation need only be applied to one of the methods; either the getter or setter.
 *
 * <p>The methods that are annotated with this annotation must also comply with the following constraints:</p>
 * <ul>
 * <li>Must not be annotated with any other react annotation</li>
 * <li>Must be an abstract instance method</li>
 * <li>Must not throw exceptions</li>
 * <li>Must be accessible from the same package as the class annotated by {@link ReactComponent}</li>
 * </ul>
 * <p>The getter must also comply with the following constraints:</p>
 * <ul>
 * <li>Must have 0 parameters</li>
 * <li>Must return a value</li>
 * <li>The type of the return value must be identical to the type of setters parameter</li>
 * </ul>
 * <p>The setter must also comply with the following constraints:</p>
 * <ul>
 * <li>Must have 1 parameter</li>
 * <li>Must not return a value</li>
 * <li>The type of the parameter must be identical to the type of the getters return value</li>
 * </ul>
 */
@Documented
@Target( ElementType.METHOD )
public @interface State
{
  /**
   * Return the name of the state value.
   * This is the key that is used to access the value in the underlying state object.
   * If the underlying method conforms to java beans accessor conventions (i.e. starts with "is" and is a boolean
   * or starts with "get") then the name is the same as the java bean convention dictates, otherwise the name of
   * the method is used as the default value for the state value.
   *
   * @return the name of the state value.
   */
  @Nonnull
  String name() default "<default>";
}
