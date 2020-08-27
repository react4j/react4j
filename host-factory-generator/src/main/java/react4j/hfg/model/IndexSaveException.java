package react4j.hfg.model;

import java.util.Objects;
import javax.annotation.Nonnull;

public final class IndexSaveException
  extends IndexException
{
  @Nonnull
  private final Index _index;

  public IndexSaveException( @Nonnull final Index index, @Nonnull final Throwable cause )
  {
    super( cause );
    _index = Objects.requireNonNull( index );
  }

  @Nonnull
  public Index getIndex()
  {
    return _index;
  }
}
