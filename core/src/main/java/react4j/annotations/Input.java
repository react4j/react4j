package react4j.annotations;

import arez.annotations.AutoObserve;
import arez.annotations.ComponentDependency;
import arez.annotations.Memoize;
import arez.annotations.Observable;
import arez.annotations.Observe;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation used to specify an input.
 * The property is extracted from Reacts underlying props object. By default the input is passed as a
 * value in when creating the view but it can also be retrieved from the react context.
 * When applied to an abstract method, the input is mutable.
 * When applied to a constructor parameter, the input is immutable.
 *
 * <p>If applied to a method, the method that is annotated with this annotation must also comply with the
 * following constraints:</p>
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
 * <li>
 *   Should be annotated with either {@link javax.annotation.Nonnull} or {@link javax.annotation.Nullable} if the
 *   return type is not a primitive. A warning will be generated but can be suppressed by the {@link SuppressWarnings}
 *   or {@link SuppressReact4jWarnings} annotations with a key "React4j:MissingInputNullability".
 * </li>
 * </ul>
 */
@Documented
@Target( { ElementType.METHOD, ElementType.PARAMETER } )
public @interface Input
{
  /**
   * Return the name of the input.
   * The name is the key used when accessing the input from the inputs object. It is also used when creating
   * the builder steps associated with the inputs that set {@link #source()} to {@link Source#DEFAULT}.
   *
   * @return the name of the input.
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
   * The setting controlling where the input value is source from.
   * If the source is set to {@link Source#CONTEXT} then the input is sometimes described as a "TreeInput"
   * as it is transparently passed from a parent view to all child views. A "TreeInput" does not
   * have to be specified by the user when creating the view.
   *
   * @return the setting controlling where the input value is source from.
   */
  Source source() default Source.DEFAULT;

  /**
   * Setting indicating whether the input should be supplied when the view is constructed.
   * This influences validation when enabled and how the Builder class is created.
   * If set to {@link Feature#ENABLE} then the user MUST supply the input and the builder will require the user
   * to specify the value. If set to {@link Feature#DISABLE} then the user can optionally supply the input.
   * If set to {@link Feature#AUTODETECT} then the annotation processor will treat it as {@link Feature#DISABLE}
   * if there is a corresponding {@link InputDefault} for the input or the {@link #source()} parameter is set to
   * {@link Source#CONTEXT}, otherwise it will be treated as {@link Feature#ENABLE}. The value of this setting
   * must not be {@link Feature#ENABLE} when {@link #source()} is set to {@link Source#CONTEXT}.
   *
   * @return the flag indicating whether the input needs to be supplied.
   */
  Feature require() default Feature.AUTODETECT;

  /**
   * Indicate whether the input should be annotated by {@link Observable}.
   *
   * <p>If set to {@link Feature#AUTODETECT} then the input will be observable if and only if:</p>
   * <ul>
   * <li>the input is not immutable.</li>
   * <li>the view has at least one method annotated with {@link Memoize} or {@link Observe}.</li>
   * </ul>
   *
   * @return the enum indicating whether input is observable.
   */
  Feature observable() default Feature.AUTODETECT;

  /**
   * Enum where the input is sourced from.
   */
  enum Source
  {
    /**
     * The input value is passed to the view during construction.
     */
    DEFAULT,
    /**
     * The input value is retrieved from the react context.
     */
    CONTEXT
  }
}
