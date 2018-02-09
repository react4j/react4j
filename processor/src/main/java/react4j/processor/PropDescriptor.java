package react4j.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;

@SuppressWarnings( "Duplicates" )
final class PropDescriptor
{
  @Nonnull
  private final String _name;
  @Nonnull
  private final ExecutableElement _method;
  @Nonnull
  private final ExecutableType _methodType;

  PropDescriptor( @Nonnull final String name,
                  @Nonnull final ExecutableElement method,
                  @Nonnull final ExecutableType methodType )
  {
    _name = Objects.requireNonNull( name );
    _method = Objects.requireNonNull( method );
    _methodType = Objects.requireNonNull( methodType );
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
}
