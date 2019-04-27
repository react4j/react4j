package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Identifies a method that is called before a component is updated or re-rendered.
 * There must only be one method annotated with this annotation in a single component.
 * This method is invoked in the "commit" phase using reacts <code>getSnapshotBeforeUpdate()</code>
 * lifecycle method.
 *
 * <p>This method is used to get access to the DOM prior to it being updated. This is useful if there is
 * state stored on the DOM that is not present in the VirtualDOM. The method annotated with {@link PreUpdate}
 * can query the DOM and then apply the necessary changes in {@link PostMountOrUpdate}. Scroll position is an example
 * of data stored on the DOM but not present in the VirtualDOM and implementing auto-scrolling to new content
 * would require use of this lifecycle method to calculate the new offset.</p>
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
public @interface PreUpdate
{
}
