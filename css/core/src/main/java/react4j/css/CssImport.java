package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Imports a CSS definition, ID or custom property. Named component and animation imports are
 * retained when referenced; unnamed global inclusions are always required by their importer.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Repeatable(CssImports.class)
public @interface CssImport {
    /** The local alias; defaults to the source name for ID and property imports. */
    String name() default "";

    /**
     * Selects a custom property without --; name optionally supplies its local alias. The importing
     * definition must declare the local property or reference it through var().
     */
    String property() default "";

    /**
     * Selects an ID without #; name optionally supplies its local alias. The importing definition
     * must use it in CSS or expose it through a CssId accessor. Cannot be combined with property.
     */
    String id() default "";

    Class<?> value();
}
