package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Static CSS declarations, nested same-element rules and owner-local keyframes for an abstract style class.
 * Definitions must not declare fields, constructors or instance initializers; nested definitions must be static.
 * The package-access generated subclass provides a singleton through its static {@code get()} method.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Css {
    String value();
}
