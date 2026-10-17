package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Declares global styles required by a component or global stylesheet, in dependency order. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface CssIncludes {
    Class<?>[] value();
}
