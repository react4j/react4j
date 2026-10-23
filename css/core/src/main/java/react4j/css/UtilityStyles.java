package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Selects one utility library for a stateless abstract style class, optionally combined with Css. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface UtilityStyles {
    Class<?> value();
}
