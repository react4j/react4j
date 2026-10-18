package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Container for repeated {@link CssImport} declarations. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface CssImports {
    CssImport[] value();
}
