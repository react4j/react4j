package react.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;

final class EventHandlerDescriptor
{
  @Nonnull
  private final String _name;
  @Nonnull
  private final ExecutableElement _method;
  @Nonnull
  private final ExecutableType _methodType;
  @Nonnull
  private final TypeElement _eventHandlerType;

  EventHandlerDescriptor( @Nonnull final String name,
                          @Nonnull final ExecutableElement method,
                          @Nonnull final ExecutableType methodType,
                          @Nonnull final TypeElement eventHandlerType )
  {
    _name = Objects.requireNonNull( name );
    _method = Objects.requireNonNull( method );
    _methodType = Objects.requireNonNull( methodType );
    _eventHandlerType = Objects.requireNonNull( eventHandlerType );
  }

  @Nonnull
  String getName()
  {
    return _name;
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

  @Nonnull
  TypeElement getEventHandlerType()
  {
    return _eventHandlerType;
  }
}
