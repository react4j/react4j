package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Identifies a method that is called after a component is rendered.
 * Render in this circumstance means that a component has been mounted on the DOM
 * or that changes to the component have been  applied to the dom.
 * There must only be one method annotated with this annotation in a single component.
 * This method is invoked in the "commit" phase using reacts <code>componentDidUpdate()</code>
 * and <code>componentDidMount()</code> lifecycle method.
 *
 * <p>The method must also conform to the following constraints:</p>
 * <ul>
 * <li>Must not be annotated with any other react4j annotation</li>
 * <li>Must have 0 parameters</li>
 * <li>Must not return a value</li>
 * <li>Must not be private</li>
 * <li>Must not be public</li>
 * <li>Must not be static</li>
 * <li>Must not be abstract</li>
 * <li>Must not throw exceptions</li>
 * <li>Must be accessible from the same package as the class annotated by {@link ReactComponent}</li>
 * </ul>
 */
@Documented
@Target( ElementType.METHOD )
public @interface PostMountOrUpdate
{
}
