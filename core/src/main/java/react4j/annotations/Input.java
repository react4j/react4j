package react4j.annotations;

import arez.annotations.Memoize;
import arez.annotations.Observable;
import arez.annotations.Observe;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

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
 *   Nullness follows the declaring JSpecify scope. Use {@link org.jspecify.annotations.Nullable}
 *   for nullable input values, or declare a {@link org.jspecify.annotations.NullMarked} scope for
 *   non-null defaults. Legacy nullability annotations are ignored.
 * </li>
 * </ul>
 */
@Documented
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Input {
    /**
     * Return the name of the input.
     * The name is the key used when accessing the input from the inputs object. It is also used when creating
     * the builder steps associated with inputs that are not sourced from tree context.
     *
     * @return the name of the input.
     */
    String name() default "<default>";

    /**
     * Return the qualifier used to access value from context.
     * It must only be specified if {@link #fromTreeContext()} is set to {@code true}.
     *
     * @return the qualifier used to access value from context.
     */
    String qualifier() default "";

    /**
     * Return {@code true} if the input value should be sourced from tree context rather than supplied when
     * creating the view. Inputs sourced from tree context are transparently passed from a parent view to
     * descendant views and do not need to be specified by the user when creating the view.
     *
     * @return {@code true} if the input value should be sourced from tree context.
     */
    boolean fromTreeContext() default false;

    /**
     * Setting indicating whether the input should be supplied when the view is constructed.
     * This influences validation when enabled and how the Builder class is created.
     * If set to {@link Feature#ENABLE} then the user MUST supply the input and the builder will require the user
     * to specify the value. If set to {@link Feature#DISABLE} then the user can optionally supply the input.
     * If set to {@link Feature#AUTODETECT} then the annotation processor will treat it as {@link Feature#DISABLE}
     * if the input is nullable, has a corresponding {@link InputDefault}, or {@link #fromTreeContext()}
     * is {@code true}; otherwise it is treated as {@link Feature#ENABLE}. A required nullable input still
     * requires a builder call and permits a null value. An optional non-null reference input requires
     * a non-null default or a tree-context source. Parametric nullness requires explicit requiredness
     * or a valid default. The value of this setting must not
     * be {@link Feature#ENABLE} when {@link #fromTreeContext()} is {@code true}.
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
}
