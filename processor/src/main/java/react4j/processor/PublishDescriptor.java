package react4j.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

final class PublishDescriptor
{
  @Nonnull
  private final String _qualifier;
  @Nonnull
  private final ExecutableElement _method;
  @Nonnull
  private final ExecutableType _methodType;

  PublishDescriptor( @Nonnull final String qualifier,
                     @Nonnull final ExecutableElement method,
                     @Nonnull final ExecutableType methodType )
  {
    _qualifier = Objects.requireNonNull( qualifier );
    _method = Objects.requireNonNull( method );
    _methodType = Objects.requireNonNull( methodType );
  }

  @Nonnull
  String getQualifier()
  {
    return _qualifier;
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
