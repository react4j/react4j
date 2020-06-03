package react4j.annotations;

import elemental2.core.JsError;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import react4j.ReactErrorInfo;

/**
 * Identifies the method that is invoked to render the component.
 * There must be exactly one method annotated with this annotation within the component.
 * The method must return a value of type {@link react4j.ReactNode}.
 *
 * <p>The method must also conform to the following constraints:</p>
 * <ul>
 * <li>Must not be annotated with any other react4j annotation</li>
 * <li>Must not be private</li>
 * <li>Must not be static</li>
 * <li>Must not be abstract</li>
 * <li>Must not throw exceptions</li>
 * <li>Must be accessible from the same package as the class annotated by {@link ReactComponent}</li>
 * <li>
 *   Should not be public as not expected to be invoked outside the component. A warning will be generated but can
 *   be suppressed by the {@link SuppressWarnings} or {@link SuppressReact4jWarnings} annotations with a key
 *   "React4j:PublicMethod". This warning is also suppressed by the annotation processor if it is implementing
 *   an interface method.
 * </li>
 * <li>
 *   Should not be protected if in the class annotated with the {@link ReactComponent} annotation as the method is not
 *   expected to be invoked outside the component. A warning will be generated but can be suppressed by the
 *   {@link SuppressWarnings} or {@link SuppressReact4jWarnings} annotations with a key "React4j:ProtectedMethod".
 * </li>
 * </ul>
 */
@Documented
@Target( ElementType.METHOD )
public @interface Render
{
}
