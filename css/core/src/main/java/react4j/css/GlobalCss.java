package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines global CSS retained only when selected by a bundle or required by retained styles.
 * Declare exactly one literal text block or explicitly supplied package-relative file.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface GlobalCss {
    String value() default "";

    /** One package-relative CSS input, explicitly declared on the compilation classpath. */
    String file() default "";
}
