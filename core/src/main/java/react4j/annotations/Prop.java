package react4j.annotations;

import arez.annotations.ArezComponent;
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
   * Return an enum indicating whether prop should trigger a change if it updated.
   * If set to {@link Feature#ENABLE} then the code will check the prop when implementing
   * <code>shouldComponentUpdate</code> while setting the value to {@link Feature#DISABLE} will result in
   * the value of the prop being ignored when determining whether a prop change should cause a re-render.
   * If set to {@link Feature#AUTODETECT} then the annotation processor will treat it as {@link Feature#ENABLE}.
   *
   * @return the enum indicating whether prop should trigger a render if it is modified.
   */
  Feature shouldUpdateOnChange() default Feature.AUTODETECT;

  /**
   * Indicate whether the prop is an Arez observable.
   * If set to {@link Feature#ENABLE} the generated class will make the prop observable and report changes
   * in the prop when the prop changes. The component MUST be a subclass of <code>ReactArezComponent</code>.
   * If set to {@link Feature#DISABLE} then the prop will not be observable. If set to {@link Feature#AUTODETECT}
   * then the prop will be observable if the component is a subclass of <code>ReactArezComponent</code>, the value
   * of {@link #shouldUpdateOnChange()} is not {@link Feature#DISABLE} and the component has a method
   * annotated with <code>arez.annotations.Computed</code>, <code>arez.annotations.Memoize</code> or
   * <code>arez.annotations.Observe</code>.
   *
   * @return the enum indicating whether prop is observable.
   */
  Feature observable() default Feature.AUTODETECT;

  /**
   * Return an enum indicating whether prop is disposable and should be checked before rendering component.
   * If a prop is disposable then the class will override <code>ReactArezComponent.anyPropsDisposed()</code>
   * and check whether the prop is disposed prior to attempting to render the component. If any props
   * are disposed then rendering will be aborted and the render method will return null.
   * If set to {@link Feature#ENABLE} then the code will add an isDisposed() check while if set to
   * {@link Feature#DISABLE} no such check will be generated. If set to {@link Feature#AUTODETECT} then the
   * annotation processor will inspect the type of the prop and treat it as {@link Feature#ENABLE} if the
   * type is annotated with the <code>arez.annotations.ArezComponent</code> annotation and the host component is
   * an Arez component. It is invalid to specify {@link Feature#ENABLE} unless the host component is an Arez
   * component.
   *
   * @return the enum indicating whether prop is disposable and should be checked before rendering component.
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
   * <li>any class that is annotated with {@link ArezComponent} where the {@link ArezComponent#requireId()} parameter does not resolve to {@link arez.annotations.Feature#DISABLE}</li>
   * <li>any class that implements {@link Keyed}</li>
   * </ul>
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
