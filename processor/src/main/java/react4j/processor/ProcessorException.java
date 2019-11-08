package react4j.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;

public final class ProcessorException
  extends RuntimeException
{
  @Nonnull
  private final Element _element;

  public ProcessorException( @Nonnull final String message, @Nonnull final Element element )
  {
    super( message );
    _element = Objects.requireNonNull( element );
  }

  @Nonnull
  public Element getElement()
  {
    return _element;
  }
}
