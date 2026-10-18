package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Exposes a local or imported animation name, or a Keyframes definition's own name. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface CssAnimation {
    /** The local CSS name; defaults to getter-name inference. Leave unset on a Keyframes definition. */
    String name() default "<default>";
}
