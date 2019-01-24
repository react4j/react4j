package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation used to specify a React component.
 */
@Documented
@Target( ElementType.TYPE )
public @interface ReactComponent
{
  /**
   * Return the name of the component.
   * The value defaults to the simple name name of the class. If the value is specified, the
   * value must conform to the requirements of a java identifier. It should also be unique
   * across the suite of components used within an application but this is not strictly
   * required as the name is only used for development purposes. (i.e. This is the name
   * that is used within the React DevTools).
   *
   * @return the name of the component.
   */
  @Nonnull
  String name() default "<default>";

  /**
   * Indicate whether the generated component class is expected to be created and injected by the injection framework.
   * {@link Feature#ENABLE} will force injection framework integration, {@link Feature#DISABLE}
   * will force no injection framework integration and {@link Feature#AUTODETECT} will enable injection framework
   * integration  if any fields or methods in the component or any parent type has an {@link javax.inject.Inject}
   * annotation.
   *
   * @return enum controlling injection framework integration.
   */
  Feature inject() default Feature.AUTODETECT;

  /**
   * React components that are arez enabled will generate an error if they observe no arez dependencies and this parameter is false.
   *
   * @return true to avoid arez components generating invariant failures if they observe no reactive components.
   */
  boolean allowNoArezDeps() default false;
}
