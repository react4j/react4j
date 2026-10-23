package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Binds a nonempty, ordered list of managed utility classes to an abstract String accessor. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface UtilityCss {
    String value();
}
