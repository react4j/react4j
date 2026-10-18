package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Exposes a local or imported animation name, or a Keyframes definition's own name. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface CssAnimation {
    /** The CSS name; defaults to the method name, with a get prefix removed. */
    String name() default "<default>";
}
