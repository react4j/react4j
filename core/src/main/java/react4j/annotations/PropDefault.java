package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation used to specify the default value for a prop.
 * The annotation can be applied to a static field or static method on the view.
 * The field or method is then accessed when initializing the default props for the view.
 *
 * <p>If a method is annotated with this annotation then it must also comply with the following constraints:</p>
 * <ul>
 * <li>Must have 0 parameters</li>
 * <li>Must return a value with the same type as the associated <code>@Prop</code> annotated method</li>
 * <li>Must be a static method</li>
 * <li>Must not be private</li>
 * <li>Must not throw exceptions</li>
 * </ul>
 *
 * <p>If a field is annotated with this annotation then it must also comply with the following constraints:</p>
 * <ul>
 * <li>Must have the same type as the associated <code>@Prop</code> annotated method</li>
 * <li>Must be a static field</li>
 * <li>Must be a final field</li>
 * <li>Must not be private</li>
 * <li>Must be accessible from the same package as the class annotated by {@link View}</li>
 * </ul>
 */
@Documented
@Target( { ElementType.METHOD, ElementType.FIELD } )
public @interface PropDefault
{
  /**
   * Return the name of the associated prop.
   *
   * <p>If the annotation is applied to a method, this value will be derived if the method name matches
   * the pattern "get[Name]Default", otherwise it must be specified.</p>
   *
   * <p>If the annotation is applied to a field, this value will be derived if the field name matches
   * the pattern "DEFAULT_[NAME]", otherwise it must be specified.</p>
   *
   * @return the name of the prop.
   */
  @Nonnull
  String name() default "<default>";
}
