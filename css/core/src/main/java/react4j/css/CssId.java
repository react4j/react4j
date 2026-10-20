package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Exposes a definition-scoped CSS ID, without the # prefix. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface CssId {
    /** The ID without #; defaults to the method name, with a get prefix removed. */
    String name() default "<default>";
}
