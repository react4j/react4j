package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Defines a reusable animation frame body whose CSS name derives from the annotated Java class. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Keyframes {
    String value();
}
