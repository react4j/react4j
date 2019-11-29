package react4j.annotations;

import arez.annotations.ActAsComponent;
import arez.annotations.ArezComponent;
import arez.annotations.Memoize;
import arez.annotations.Observable;
import arez.annotations.Observe;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import react4j.Keyed;

/**
 * Annotation used to specify an abstract method that returns a prop.
 * The property is extracted from Reacts underlying props object.
 *
 * <p>The method that is annotated with this annotation must also comply with the following constraints:</p>
 * <ul>
 * <li>Must not be annotated with any other react annotation</li>
 * <li>Must have 0 parameters</li>
 * <li>Must return a value</li>
 * <li>Must be an abstract instance method</li>
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
public @interface Prop
{
  /**
   * Return the name of the prop.
   * If the underlying method conforms to java beans accessor conventions (i.e. starts with "is" and is a boolean
   * or starts with "get") then the name is the same as the java bean convention dictates, otherwise the name of
   * the method is used as the default value for the prop.
   *
   * @return the name of the prop.
   */
  @Nonnull
  String name() default "<default>";

  /**
   * Return enum indicating whether prop should be when component is constructed.
   * This influences validation when enabled and how the Builder class is created.
   * If set to {@link Feature#ENABLE} then the user MUST supply the prop and the builder will require the user
   * to specify the value. If set to {@link Feature#DISABLE} then the user can optionally supply the prop.
   * If set to {@link Feature#AUTODETECT} then the annotation processor will treat it as {@link Feature#DISABLE}
   * if there is a corresponding {@link PropDefault} for the prop, otherwise it will be treated as
   * {@link Feature#ENABLE}.
   *
   * @return the flag indicating whether the prop needs to be supplied.
   */
  Feature require() default Feature.AUTODETECT;

  /**
   * Indicate whether changing the value of the prop should schedule a re-render.
   * If set to {@link Feature#AUTODETECT} then the annotation processor will treat it as
   * {@link Feature#ENABLE} unless {@link #immutable()} is set to <code>true</code>.
   *
   * @return the enum indicating whether prop should trigger a render if it is modified.
   */
  Feature shouldUpdateOnChange() default Feature.AUTODETECT;

  /**
   * Indicate whether the prop should be annotated by {@link Observable}.
   *
   * <p>If set to {@link Feature#AUTODETECT} then the prop will be observable if and only if:</p>
   * <ul>
   * <li>{@link #shouldUpdateOnChange()} is not set to {@link Feature#DISABLE}.</li>
   * <li>the component has at least one method annotated with {@link Memoize} or {@link Observe}.</li>
   * </ul>
   *
   * @return the enum indicating whether prop is observable.
   */
  Feature observable() default Feature.AUTODETECT;

  /**
   * Return an enum indicating whether the component should check whether the value of the prop is disposed
   * prior to rendering. If the value is disposed then the render method will exit early and return null.
   * If this parameter is set to {@link Feature#AUTODETECT} then the annotation processor will inspect the
   * type of the prop and treat it as {@link Feature#ENABLE} if the type is annotated with the {@link ArezComponent}
   * annotation or the {@link ActAsComponent} annotation.
   *
   * @return an enum indicating whether the component should check whether the value of the prop is disposed prior to rendering.
   */
  Feature disposable() default Feature.AUTODETECT;

  /**
   * True if the prop is not expected to change after initial value is set. If the value of the prop does change
   * then it is expected that the react component will be unmounted and a new component created. This is implemented
   * by synthesizing a key for the component every time the component that is derived from this prop. To enable this
   * the annotation processor must be able to identify the type of the prop so that a key can be synthesized. The
   * following types are supported by the annotation processor;
   *
   * <ul>
   * <li>primitive types (i.e. boolean, short etc) and their corresponding boxed types (i.e. {@link java.lang.Boolean}, {@link java.lang.Short} etc).</li>
   * <li>the {@link java.lang.String} type</li>
   * <li>any class that implements {@link Keyed}</li>
   * <li>any class that is annotated with {@link ArezComponent} where the {@link ArezComponent#requireId()} parameter does not resolve to {@link arez.annotations.Feature#DISABLE}</li>
   * <li>any class or interface that is annotated with {@link ActAsComponent}. It is assumed that every implementation is an Arez component where the {@link ArezComponent#requireId()} parameter does not resolve to {@link arez.annotations.Feature#DISABLE}</li>
   * </ul>
   *
   * <p>It should be noted that if a type implements {@link Keyed} and is annotated with either {@link ArezComponent}
   * or {@link ActAsComponent} then the annotation processor will assume the {@link Keyed} interface is to used in
   * preference to other alternative strategies.</p>
   *
   * <p>In the future, the annotation processor may include additional allowable types such as those that implement
   * {@link arez.component.Identifiable} directly or other primitive types within the runtime library. These other
   * types will be added if demand is established, othewise an additional hook will be added to allow users to
   * customize key generation using a static method on the component. </p>
   *
   * @return true if changing the prop recreates the component.
   */
  boolean immutable() default false;
}
