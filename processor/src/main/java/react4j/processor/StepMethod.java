package react4j.processor;

import com.squareup.javapoet.TypeName;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

final class StepMethod
{
  @Nonnull
  private final String _name;
  @Nonnull
  private final String _key;
  @Nonnull
  private final TypeName _type;
  @Nullable
  private final ExecutableElement _propMethod;
  @Nullable
  private final ExecutableType _propMethodType;
  @Nonnull
  private final StepMethodType _stepMethodType;

  StepMethod( @Nonnull final String name,
              @Nonnull final String key,
              @Nonnull final TypeName type,
              @Nullable final ExecutableElement propMethod,
              @Nullable final ExecutableType propMethodType,
              @Nonnull final StepMethodType stepMethodType )
  {
    _name = Objects.requireNonNull( name );
    _key = Objects.requireNonNull( key );
    _type = Objects.requireNonNull( type );
    _propMethod = propMethod;
    _propMethodType = propMethodType;
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
  ExecutableElement getPropMethod()
  {
    return _propMethod;
  }

  @Nullable
  ExecutableType getPropMethodType()
  {
    return _propMethodType;
  }

  @Nonnull
  StepMethodType getStepMethodType()
  {
    return _stepMethodType;
  }
}
