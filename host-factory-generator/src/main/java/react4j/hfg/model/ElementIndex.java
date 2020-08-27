package react4j.hfg.model;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTransient;

@JsonbPropertyOrder( { "name", "lastUpdatedAt" } )
public final class ElementIndex
{
  @JsonbTransient
  private Index index;
  private String name;
  private long lastUpdatedAt;

  public String getName()
  {
    return name;
  }

  public void setName( final String name )
  {
    this.name = name;
  }

  public long getLastUpdatedAt()
  {
    return lastUpdatedAt;
  }

  public void setLastUpdatedAt( final long lastUpdatedAt )
  {
    this.lastUpdatedAt = lastUpdatedAt;
  }

  @Nonnull
  public Index getIndex()
  {
    assert null != index;
    return index;
  }

  void setIndex( @Nonnull final Index index )
  {
    this.index = Objects.requireNonNull( index );
  }
}
