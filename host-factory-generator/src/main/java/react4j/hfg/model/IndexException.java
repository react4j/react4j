package react4j.hfg.model;

import javax.annotation.Nonnull;

public abstract class IndexException
  extends Exception
{
  public IndexException()
  {
  }

  public IndexException( @Nonnull final String message )
  {
    super( message );
  }

  public IndexException( @Nonnull final String message, @Nonnull final Throwable cause )
  {
    super( message, cause );
  }

  public IndexException( @Nonnull final Throwable cause )
  {
    super( cause );
  }
}
