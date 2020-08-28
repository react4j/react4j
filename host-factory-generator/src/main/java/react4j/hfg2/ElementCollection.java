package react4j.hfg2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public final class ElementCollection
{
  @Nonnull
  private final Map<String, Element> _elements = new HashMap<>();

  public Element element( @Nonnull final String name,
                          @Nonnull final String domInterface )
  {
    return element( name, domInterface, true );
  }

  public Element element( @Nonnull final String name,
                          @Nonnull final String domInterface,
                          final boolean supportsChildren )
  {
    assert !_elements.containsKey( name );
    final Element element = new Element( name, domInterface, supportsChildren );
    _elements.put( name, element );
    return element;
  }

  @Nonnull
  public Collection<Element> elements()
  {
    return _elements.values();
  }
}
