package react4j.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;

final class ReactProcessorException
  extends RuntimeException
{
  @Nonnull
  private final Element _element;

  ReactProcessorException( @Nonnull final String message, @Nonnull final Element element )
  {
    super( message );
    _element = Objects.requireNonNull( element );
  }

  @Nonnull
  Element getElement()
  {
    return _element;
  }
}
