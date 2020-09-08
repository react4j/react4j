package react4j.dom.proptypes.html.attributeTypes;

import java.util.Objects;
import javax.annotation.Nonnull;

public enum Target
{
  blank( "_blank" ),
  self( "_self" ),
  parent( "_parent" ),
  top( "_top" );
  @Nonnull
  private final String _value;

  Target( @Nonnull final String value )
  {
    _value = Objects.requireNonNull( value );
  }

  @Nonnull
  public String getValue()
  {
    return _value;
  }
}
