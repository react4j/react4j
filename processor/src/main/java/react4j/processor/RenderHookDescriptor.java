package react4j.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

final class RenderHookDescriptor
{
  private final int _sortOrder;
  @Nonnull
  private final ExecutableElement _method;
  @Nonnull
  private final ExecutableType _methodType;

  RenderHookDescriptor( final int sortOrder,
                        @Nonnull final ExecutableElement method,
                        @Nonnull final ExecutableType methodType )
  {
    _sortOrder = sortOrder;
    _method = Objects.requireNonNull( method );
    _methodType = Objects.requireNonNull( methodType );
  }

  int getSortOrder()
  {
    return _sortOrder;
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
