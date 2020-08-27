package react4j.hfg.util.fetch.fetch;

import java.util.Objects;
import javax.annotation.Nonnull;

public final class FetchException
  extends Exception
{
  @Nonnull
  private final String _url;

  public FetchException( @Nonnull final String url,@Nonnull final Throwable cause )
  {
    super( cause );
    _url = Objects.requireNonNull(url);
  }

  @Nonnull
  public String getUrl()
  {
    return _url;
  }
}
