package react4j.processor;

import com.squareup.javapoet.TypeName;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

/**
 * Represents a step in the builder. There are a few "intrinsic" methods that are handled by generator
 * but the rest are driven by Prop annotation details. The intrinsics are as follows:
 *
 * <ul>
 * <li>name = key: key intrinsic. Standard implementation but does not come from a @Prop annotated method..</li>
 * <li>name = build: no parameters returns ReactNode and terminates build. This has custom code in implementation.</li>
 * <li>name = child, key = child: This is for single child components and the implementation caches child and returns build().</li>
 * <li>name = children: This is for multi child components and the implementation creates JsArray if null and adds all supplied non-null children to array before invoking build().</li>
 * <li>name = child, key = children: This is for multi child components built one child at a time. The implementation returns if null passed to it, initializes JsArray if null, adds child to array.</li>
 * </ul>
 */
final class StepMethod
{
  /**
   * The name of the prop and the corresponding builder method.
   */
  @Nonnull
  private final String _name;
  /**
   * The key under which the prop is added into props that are passed to component.
   */
  @Nonnull
  private final String _key;
  /**
   * The expected type of the prop.
   */
  @Nonnull
  private final TypeName _type;
  @Nullable
  private final PropDescriptor _prop;
  /**
   * After this method is called should the builder STAY on the same step, ADVANCE to the next step or TERMINATE builder and call build().
   */
  @Nonnull
  private final StepMethodType _stepMethodType;

  StepMethod( @Nonnull final PropDescriptor prop, @Nonnull final StepMethodType stepMethodType )
  {
    this( prop.getName(),
          prop.getName(),
          TypeName.get( prop.getMethodType().getReturnType() ),
          prop,
          stepMethodType );
  }

  StepMethod( @Nonnull final String name,
              @Nonnull final String key,
              @Nonnull final TypeName type,
              @Nullable final PropDescriptor prop,
              @Nonnull final StepMethodType stepMethodType )
  {
    _name = Objects.requireNonNull( name );
    _key = Objects.requireNonNull( key );
    _type = Objects.requireNonNull( type );
    _prop = prop;
    _stepMethodType = Objects.requireNonNull( stepMethodType );
  }

  @Nonnull
  String getName()
  {
    return _name;
  }

  @Nonnull
  String getKey()
  {
    return _key;
  }

  @Nonnull
  TypeName getType()
  {
    return _type;
  }

  @Nullable
  PropDescriptor getProp()
  {
    return _prop;
  }

  @Nullable
  ExecutableElement getPropMethod()
  {
    return null != _prop ? _prop.getMethod() : null;
  }

  @Nullable
  ExecutableType getPropMethodType()
  {
    return null != _prop ? _prop.getMethodType() : null;
  }

  @Nonnull
  StepMethodType getStepMethodType()
  {
    return _stepMethodType;
  }

  boolean isBuildIntrinsic()
  {
    return getName().equals( "build" );
  }

  boolean isKeyIntrinsic()
  {
    return getName().equals( "key" );
  }

  boolean isChildIntrinsic()
  {
    return getName().equals( "child" ) && getKey().equals( "child" );
  }

  boolean isChildrenIntrinsic()
  {
    return getName().equals( "children" ) && getKey().equals( "children" );
  }

  boolean isChildrenStreamIntrinsic()
  {
    return getName().equals( "children" ) && getKey().equals( "*children_stream*" );
  }
}
