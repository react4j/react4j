package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Identifies a method that is called before a view is updated or re-rendered.
 * There may be zero or more methods annotated with this annotation in a view.
 * This method is invoked in the "commit" phase using reacts <code>getSnapshotBeforeUpdate()</code>
 * lifecycle method.
 *
 * <p>This method is used to get access to the DOM prior to it being updated. This is useful if there is
 * state stored on the DOM that is not present in the VirtualDOM. The method annotated with {@link PreUpdate}
 * can query the DOM and then apply the necessary changes in {@link PostUpdate}. Scroll position is an example
 * of data stored on the DOM but not present in the VirtualDOM and implementing auto-scrolling to new content
 * would require use of this lifecycle method to calculate the new offset.</p>
 *
 * <p>The same method may participate in several lifecycle phases by carrying their annotations.
 * Callbacks run in declaration order, with superclasses before subclasses. Each type's interfaces are
 * visited before its declarations, in written order with interface ancestors first and shared ancestors
 * visited once. The first ancestor annotation for each phase reserves its position. Overrides retain
 * that position but participate only in phases annotated on the overriding declaration; an unannotated
 * override disables the callback. A newly added phase takes the overriding declaration's position.</p>
 *
 * <p>The method must also conform to the following constraints:</p>
 * <ul>
 * <li>Must have 0 parameters</li>
 * <li>Must not return a value</li>
 * <li>Must not be private</li>
 * <li>Must not be static</li>
 * <li>Must not be abstract</li>
 * <li>Must not throw exceptions</li>
 * <li>Must be accessible from the same package as the class annotated by {@link View}</li>
 * <li>
 *   Should not be public as not expected to be invoked outside the view. A warning will be generated but can
 *   be suppressed by the {@link SuppressWarnings} or {@link SuppressReact4jWarnings} annotations with a key
 *   "React4j:PublicMethod". This warning is also suppressed by the annotation processor if it is implementing
 *   an interface method.
 * </li>
 * <li>
 *   Should not be protected if in the class annotated with the {@link View} annotation as the method is not
 *   expected to be invoked outside the view. A warning will be generated but can be suppressed by the
 *   {@link SuppressWarnings} or {@link SuppressReact4jWarnings} annotations with a key "React4j:ProtectedMethod".
 * </li>
 * </ul>
 */
@Documented
@Target(ElementType.METHOD)
public @interface PreUpdate {}
