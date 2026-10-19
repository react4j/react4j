package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** A file relative to the package of an {@link Assets} definition. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Asset {
    String value();
}
