package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation used to associate a parameter with a input on a method annotated with {@link OnInputChange} .
 * The type of the parameter must be the same as the return type of the input method.
 */
@Documented
@Target( ElementType.PARAMETER )
public @interface InputRef
{
  /**
   * Return the name of the associated input.
   *
   * @return the name of the input.
   */
  @Nonnull
  String value();
}
