package react.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation used to specify a React component.
 * The class must be a concrete subclass of react.core.SideComponent with a single constructor that
 * takes the underlying react component.
 */
@Documented
@Target( ElementType.TYPE )
public @interface ReactComponent
{
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
}
