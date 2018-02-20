package react4j.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

final class StateValueDescriptor
{
  @Nonnull
  private final String _name;
  @Nullable
  private ExecutableElement _getter;
  @Nullable
  private ExecutableType _getterType;
  @Nullable
  private ExecutableElement _setter;
  @Nullable
  private ExecutableType _setterType;

  StateValueDescriptor( @Nonnull final String name )
  {
    _name = Objects.requireNonNull( name );
  }

  @Nonnull
  String getName()
  {
    return _name;
  }

  void setGetter( @Nonnull final ExecutableElement getter, @Nonnull final ExecutableType getterType )
  {
    _getter = Objects.requireNonNull( getter );
    _getterType = Objects.requireNonNull( getterType );
  }

  boolean hasGetter()
  {
    return null != _getter;
  }

  @Nonnull
  ExecutableElement getGetter()
  {
    assert null != _getter;
    return _getter;
  }

  @Nonnull
  ExecutableType getGetterType()
  {
    assert null != _getterType;
    return _getterType;
  }

  boolean hasSetter()
  {
    return null != _setter;
  }

  void setSetter( @Nonnull final ExecutableElement setter, @Nonnull final ExecutableType setterType )
  {
    _setter = Objects.requireNonNull( setter );
    _setterType = Objects.requireNonNull( setterType );
  }

  @Nonnull
  ExecutableElement getSetter()
  {
    assert null != _setter;
    return _setter;
  }

  @Nonnull
  ExecutableType getSetterType()
  {
    assert null != _setterType;
    return _setterType;
  }
}
