package react4j.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;

final class ScheduleRenderDescriptor
{
  @Nonnull
  private final ExecutableElement _method;
  private final boolean _skipShouldViewUpdate;

  ScheduleRenderDescriptor( @Nonnull final ExecutableElement method, final boolean skipShouldViewUpdate )
  {
    _method = Objects.requireNonNull( method );
    _skipShouldViewUpdate = skipShouldViewUpdate;
  }

  @Nonnull
  ExecutableElement getMethod()
  {
    return _method;
  }

  boolean skipShouldViewUpdate()
  {
    return _skipShouldViewUpdate;
  }
}
