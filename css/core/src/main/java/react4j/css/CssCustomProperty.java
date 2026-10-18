package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Exposes an owned or imported custom-property name, including its -- prefix. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface CssCustomProperty {
    /** The CSS name without --; defaults to the method name, with a get prefix removed. */
    String name() default "<default>";
}
