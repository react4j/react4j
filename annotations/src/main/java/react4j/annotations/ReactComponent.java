package react4j.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation used to specify a React component.
 * The class must be a concrete subclass of react4j.core.SideComponent with a single constructor that
 * takes the underlying react component.
 */
@Documented
@Target( ElementType.TYPE )
public @interface ReactComponent
{
  /**
   * Enum to control when injectible elements should be present.
   */
  enum Feature
  {
    ENABLE, DISABLE, AUTODETECT
  }

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
   * Indicate whether an @Inject annotation should be added to constructor of generated class.
   * {@link Feature#ENABLE} will force the addition of an @Inject annotation, {@link Feature#DISABLE}
   * will result in no @Inject annotation and {@link Feature#AUTODETECT} will add an @Inject
   * if any fields or methods in the react4j component or any parent type has an @Inject annotation.
   *
   * @return enum controlling present of Inject annotation on constructor.
   */
  Feature inject() default Feature.AUTODETECT;

  /**
   * Indicate whether a dagger sub-component and module is created for component.
   * Dagger is detected by searching for "dagger.Module" class in the same processing environment that
   * is compiling the react4j component.
   *
   * @return enum controlling whether dagger artifacts are generated.
   */
  Feature dagger() default Feature.AUTODETECT;
}
