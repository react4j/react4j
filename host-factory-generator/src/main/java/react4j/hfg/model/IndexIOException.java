package react4j.hfg.model;

import javax.annotation.Nonnull;

public final class IndexIOException
  extends IndexException
{
  public IndexIOException( @Nonnull final String message,
                           @Nonnull final Throwable cause )
  {
    super( message, cause );
  }
}
