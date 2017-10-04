package react4j.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark elements that are unsupported.
 * This means that it is possible that these elements will be removed or modified
 * in the future and code should not rely on it.
 */
@Documented
@Retention( RetentionPolicy.SOURCE )
@Target( { ElementType.PACKAGE,
           ElementType.TYPE,
           ElementType.ANNOTATION_TYPE,
           ElementType.CONSTRUCTOR,
           ElementType.METHOD } )
public @interface Unsupported
{
  /**
   * Specify the reason why this element is unsupported.
   *
   * @return the reason why this element is unsupported.
   */
  String value() default "";
}
