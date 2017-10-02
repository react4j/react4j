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
}
