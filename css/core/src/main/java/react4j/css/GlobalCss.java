package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Defines global CSS retained only when selected by a bundle or required by retained styles. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface GlobalCss {
    String value();
}
