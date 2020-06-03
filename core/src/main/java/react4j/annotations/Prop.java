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
 * The property is extracted from Reacts underlying props object. By default the prop is passed as
 * a value in when creating the view but it can also be retrieved from the react context.
 *
 * <p>The method that is annotated with this annotation must also comply with the following constraints:</p>
 * <ul>
 * <li>Must not be annotated with any other react annotation</li>
 * <li>Must have 0 parameters</li>
 * <li>Must return a value</li>
 * <li>Must be an abstract instance method</li>
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
@Target( ElementType.METHOD )
public @interface Prop
{
  /**
   * Return the name of the prop.
   * The name is the key used when accessing the prop from the props object. It is also used when creating
   * the builder steps associated with the props that set {@link #source()} to {@link Source#DEFAULT}.
   *
   * @return the name of the prop.
   */
  @Nonnull
  String name() default "<default>";

  /**
   * Return the qualifier used to access value from context.
   * It must only be specified if {@link #source()} is set to {@link Source#CONTEXT}.
   *
   * @return the qualifier used to access value from context.
   */
  @Nonnull
  String qualifier() default "";

  /**
   * The setting controlling where the prop value is source from.
   * If the source is set to {@link Source#CONTEXT} then the prop is sometimes described as a "TreeProp"
   * as it is transparently passed from a parent view to all child views. A "TreeProp" does not
   * have to be specified by the user when creating the view.
   *
   * @return the setting controlling where the prop value is source from.
   */
  Source source() default Source.DEFAULT;

  /**
   * Setting indicating whether the prop should be supplied when the view is constructed.
   * This influences validation when enabled and how the Builder class is created.
   * If set to {@link Feature#ENABLE} then the user MUST supply the prop and the builder will require the user
   * to specify the value. If set to {@link Feature#DISABLE} then the user can optionally supply the prop.
   * If set to {@link Feature#AUTODETECT} then the annotation processor will treat it as {@link Feature#DISABLE}
   * if there is a corresponding {@link PropDefault} for the prop or the {@link #source()} parameter is set to
   * {@link Source#CONTEXT}, otherwise it will be treated as {@link Feature#ENABLE}. The value of this setting
   * must not be {@link Feature#ENABLE} when {@link #source()} is set to {@link Source#CONTEXT}.
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
   * <li>the view has at least one method annotated with {@link Memoize} or {@link Observe}.</li>
   * </ul>
   *
   * @return the enum indicating whether prop is observable.
   */
  Feature observable() default Feature.AUTODETECT;

  /**
   * Return an enum indicating whether the view should check whether the value of the prop is disposed
   * prior to rendering. If the value is disposed then the render method will exit early and return null.
   * If this parameter is set to {@link Feature#AUTODETECT} then the annotation processor will inspect the
   * type of the prop and treat it as {@link Feature#ENABLE} if the type is annotated with the {@link ArezComponent}
   * annotation or the {@link ActAsComponent} annotation.
   *
   * @return an enum indicating whether the view should check whether the value of the prop is disposed prior to rendering.
   */
  Feature disposable() default Feature.AUTODETECT;

  /**
   * True if the prop is not expected to change after initial value is set. If the value of the prop does change
   * then it is expected that the react view will be unmounted and a new view created. This is implemented
   * by synthesizing a key for the view every time the view that is derived from this prop. To enable this
   * the annotation processor must be able to identify the type of the prop so that a key can be synthesized. The
   * following types are supported by the annotation processor;
   *
   * <ul>
   * <li>primitive types (i.e. boolean, short etc) and their corresponding boxed types (i.e. {@link java.lang.Boolean}, {@link java.lang.Short} etc).</li>
   * <li>the {@link java.lang.String} type</li>
   * <li>any class that implements {@link Keyed}</li>
   * <li>any class that is annotated with {@link ArezComponent} where the {@link ArezComponent#requireId()} parameter does not resolve to {@link arez.annotations.Feature#DISABLE}</li>
   * <li>any class or interface that is annotated with {@link ActAsComponent}. It is assumed that every implementation is an Arez component where the {@link ArezComponent#requireId()} parameter does not resolve to {@link arez.annotations.Feature#DISABLE}</li>
   * <li>any class or interface that is compatible with @link arez.component.Identifiable}.</li>
   * </ul>
   *
   * <p>It should be noted that if a type implements {@link Keyed} and is annotated with either {@link ArezComponent}
   * or {@link ActAsComponent} then the annotation processor will assume the {@link Keyed} interface is to used in
   * preference to other alternative strategies.</p>
   *
   * @return true if changing the prop recreates the view.
   */
  boolean immutable() default false;

  /**
   * Enum where the prop is sourced from.
   */
  enum Source
  {
    /**
     * The prop value is passed to the view during construction.
     */
    DEFAULT,
    /**
     * The prop value is retrieved from the react context.
     */
    CONTEXT
  }
}
