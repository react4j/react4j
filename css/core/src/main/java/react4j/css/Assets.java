package react4j.css;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Checked asset URL accessors on a stateless abstract class. Declare files through {@link Asset}
 * methods and a Bazel asset catalogue; generated bindings do not contain file contents.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Assets {}
