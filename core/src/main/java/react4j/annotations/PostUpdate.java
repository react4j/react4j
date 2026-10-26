package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Identifies a method that is called after a view is updated or re-rendered.
 * Re-render in this circumstance means that a view has been mounted on the DOM
 * and changes to the view are being applied to the dom.
 * There may be zero or more methods annotated with this annotation in a view.
 * This method is invoked in the "commit" phase using reacts <code>componentDidUpdate()</code>
 * lifecycle method.
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
public @interface PostUpdate {}
