package react4j.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;

final class CallbackDescriptor
{
  @Nonnull
  private final String _name;
  @Nonnull
  private final ExecutableElement _method;
  @Nonnull
  private final ExecutableType _methodType;
  @Nonnull
  private final TypeElement _callbackType;
  @Nonnull
  private final ExecutableElement _callbackMethod;

  CallbackDescriptor( @Nonnull final String name,
                      @Nonnull final ExecutableElement method,
                      @Nonnull final ExecutableType methodType,
                      @Nonnull final TypeElement callbackType,
                      @Nonnull final ExecutableElement callbackMethod )
  {
    _name = Objects.requireNonNull( name );
    _method = Objects.requireNonNull( method );
    _methodType = Objects.requireNonNull( methodType );
    _callbackType = Objects.requireNonNull( callbackType );
    _callbackMethod = Objects.requireNonNull( callbackMethod );
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
  TypeElement getCallbackType()
  {
    return _callbackType;
  }

  @Nonnull
  ExecutableElement getCallbackMethod()
  {
    return _callbackMethod;
  }
}
