package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Imports a {@link Keyframes} class under a local animation name in a CSS definition. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(CssImports.class)
public @interface CssImport {
    String name();

    Class<?> value();
}
