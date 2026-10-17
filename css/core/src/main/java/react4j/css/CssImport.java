package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Imports a CSS definition. Global styles have no name and are always required by their importer;
 * keyframes and component styles require a local name and are retained when referenced.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(CssImports.class)
public @interface CssImport {
    String name() default "";

    Class<?> value();
}
