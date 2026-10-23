package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Supplies a finite, precompiled, flattened utility stylesheet with defining-owner naming.
 * Declare exactly one literal text block or explicitly supplied package-relative file.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface UtilityCssLibrary {
    String value() default "";

    /** One package-relative CSS input, explicitly declared on the compilation classpath. */
    String file() default "";

    /** Authored class hooks kept outside managed utility accessor lists. */
    String[] externalClasses() default {};

    /** External custom property names without --. */
    String[] externalProperties() default {};

    String[] externalAnimations() default {};

    String[] externalFonts() default {};

    String[] externalContainers() default {};
}
