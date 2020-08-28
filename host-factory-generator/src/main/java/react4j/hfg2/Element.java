package react4j.hfg2;

import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

public final class Element
{
  @Nonnull
  private final String _name;
  @Nonnull
  private final Set<String> _classifications;
  @Nonnull
  private final String _domInterface;
  private final boolean _supportsChildren;

  Element( @Nonnull final String name,
           @Nonnull final Set<String> classifications,
           @Nonnull final String domInterface,
           final boolean supportsChildren )
  {
    _name = Objects.requireNonNull( name );
    _classifications = Objects.requireNonNull( classifications );
    _domInterface = Objects.requireNonNull( domInterface );
    _supportsChildren = supportsChildren;
  }

  @Nonnull
  public String getName()
  {
    return _name;
  }

  @Nonnull
  public Set<String> getClassifications()
  {
    return _classifications;
  }

  @Nonnull
  public String getDomInterface()
  {
    return _domInterface;
  }

  public boolean supportsChildren()
  {
    return _supportsChildren;
  }
}
