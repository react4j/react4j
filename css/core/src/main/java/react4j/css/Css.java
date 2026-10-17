package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Static CSS declarations, nested same-element rules and owner-local keyframes for a style definition. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Css {
    String value();
}
