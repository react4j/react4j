package react4j.processor;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;

final class OnPropChangeDescriptor
{
  @Nonnull
  private final ExecutableElement _method;
  @Nonnull
  private final List<PropDescriptor> _props;
  /**
   * Flag set to true if the hook method is invoked prio to update.
   */
  private final boolean _preUpdate;

  OnPropChangeDescriptor( @Nonnull final ExecutableElement method,
                          @Nonnull final List<PropDescriptor> props,
                          final boolean preUpdate )
  {
    _method = Objects.requireNonNull( method );
    _props = Objects.requireNonNull( props );
    _preUpdate = preUpdate;
    _props.forEach( PropDescriptor::markAsOnChangePresent );
  }

  @Nonnull
  ExecutableElement getMethod()
  {
    return _method;
  }

  boolean isPreUpdate()
  {
    return _preUpdate;
  }

  @Nonnull
  List<PropDescriptor> getProps()
  {
    return _props;
  }
}
