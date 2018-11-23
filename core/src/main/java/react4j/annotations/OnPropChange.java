package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;

/**
 * Identifies a method that is called when the prop value is changed and after the component has rendered.
 * This method is invoked from the {@link react4j.Component#shouldComponentUpdate(JsPropertyMap)} lifecycle
 * method. The method may be defined with one parameter which is the old value of the prop. The method can
 * also have zero parameters if the component author just wants to be notified when the prop is changed.
 *
 * <p>The method must also conform to the following constraints:</p>
 * <ul>
 * <li>Must not be annotated with any other react4j annotation</li>
 * <li>Must have either 0 or 1 parameters. If present the parameter must match the type of the prop</li>
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
   * Return the name of the associated prop. If the value is not specified then the name
   * will be derived assuming that the method name matches the pattern "on[Name]Change"
   *
   * @return the name of the prop.
   */
  @Nonnull
  String name() default "<default>";
}
