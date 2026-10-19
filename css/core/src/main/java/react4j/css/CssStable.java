package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Preserves an owned CSS name when linking with compression. On a definition, covers all its owned
 * symbols; on a modifier accessor, covers only the modifier. Global symbols retain authored names.
 * Stability does not retain unused styles or change imported symbols' policies.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface CssStable {}
