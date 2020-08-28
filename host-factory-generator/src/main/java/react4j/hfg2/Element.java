package react4j.hfg2;

import java.util.Objects;
import javax.annotation.Nonnull;

public final class Element
{
  @Nonnull
  private final String _name;
  @Nonnull
  private final String _domInterface;
  private final boolean _supportsChildren;

  Element( @Nonnull final String name, @Nonnull final String domInterface, final boolean supportsChildren )
  {
    _name = Objects.requireNonNull( name );
    _domInterface = Objects.requireNonNull( domInterface );
    _supportsChildren = supportsChildren;
  }

  @Nonnull
  public String getName()
  {
    return _name;
  }

  @Nonnull
  public String getDomInterface()
  {
    return _domInterface;
  }

  public boolean isSupportsChildren()
  {
    return _supportsChildren;
  }
}
