package react4j.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;

final class ScheduleRenderDescriptor
{
  @Nonnull
  private final ExecutableElement _method;
  private final boolean _skipShouldComponentUpdate;

  ScheduleRenderDescriptor( @Nonnull final ExecutableElement method, final boolean skipShouldComponentUpdate )
  {
    _method = Objects.requireNonNull( method );
    _skipShouldComponentUpdate = skipShouldComponentUpdate;
  }

  @Nonnull
  ExecutableElement getMethod()
  {
    return _method;
  }

  boolean skipShouldComponentUpdate()
  {
    return _skipShouldComponentUpdate;
  }
}
