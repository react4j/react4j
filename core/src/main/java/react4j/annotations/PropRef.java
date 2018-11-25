package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation used to associate a parameter with a prop.
 * The type of the parameter must be the same as the return type of the prop method.
 */
@Documented
@Target( ElementType.PARAMETER )
public @interface PropRef
{
  /**
   * Return the name of the associated prop.
   *
   * @return the name of the prop.
   */
  @Nonnull
  String value();
}
