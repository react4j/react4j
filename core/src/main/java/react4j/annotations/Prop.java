package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import jsinterop.base.JsPropertyMap;

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
   * This setting is used when the annotation processors is responsible for implementing
   * the {@link react4j.Component#shouldComponentUpdate(JsPropertyMap)} lifecycle
   * method. If set to {@link Feature#ENABLE} then the code will check the prop when implementing
   * <code>shouldComponentUpdate</code>. If set to {@link Feature#DISABLE} then the prop will be ignored
   * when implementing <code>shouldComponentUpdate</code>. If set to {@link Feature#AUTODETECT} then the
   * annotation processor will treat it as {@link Feature#ENABLE}.
   *
   * @return the enum indicating whether prop should trigger a change if it updated.
   */
  Feature shouldUpdateOnChange() default Feature.AUTODETECT;

  /**
   * Indicate whether prop is Arez observable.
   * If set to {@link Feature#ENABLE} the generated class will make the prop observable and report changes
   * in the prop when the prop changes. The component MUST be a subclass of <code>ReactArezComponent</code>.
   * If set to {@link Feature#DISABLE} then the prop will not be observable. If set to {@link Feature#AUTODETECT}
   * then the prop will be observable if the component is a subclass of <code>ReactArezComponent</code>, the value
   * of {@link #shouldUpdateOnChange()} is equivalent to {@link Feature#ENABLE} and the component has a method
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
}
