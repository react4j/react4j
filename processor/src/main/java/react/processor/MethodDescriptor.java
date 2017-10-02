package react.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

final class MethodDescriptor
{
  @Nonnull
  private final ExecutableElement _method;
  @Nonnull
  private final ExecutableType _methodType;

  MethodDescriptor( @Nonnull final ExecutableElement method, @Nonnull final ExecutableType methodType )
  {
    _method = Objects.requireNonNull( method );
    _methodType = Objects.requireNonNull( methodType );
  }

  @Nonnull
  ExecutableElement getMethod()
  {
    return _method;
  }

  @Nonnull
  ExecutableType getMethodType()
  {
    return _methodType;
  }

  @Override
  public boolean equals( final Object o )
  {
    if ( this == o )
    {
      return true;
    }
    else if ( null == o || getClass() != o.getClass() )
    {
      return false;
    }
    else
    {
      final MethodDescriptor that = (MethodDescriptor) o;
      return getMethod().equals( that.getMethod() ) && getMethodType().equals( that.getMethodType() );
    }
  }

  @Override
  public int hashCode()
  {
    int result = getMethod().hashCode();
    result = 31 * result + getMethodType().hashCode();
    return result;
  }
}
