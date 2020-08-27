package react4j.hfg.util.fetch.fetch;

import java.nio.file.Path;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class FetchResult
{
  @Nonnull
  private final String _url;
  @Nonnull
  private final Path _path;
  private final long _lastModifiedAt;

  FetchResult( @Nonnull final String url, @Nonnull final Path path, final long lastModifiedAt )
  {
    _url = Objects.requireNonNull( url );
    _path = Objects.requireNonNull( path );
    _lastModifiedAt = lastModifiedAt;
  }

  @Nonnull
  public String getUrl()
  {
    return _url;
  }

  @Nonnull
  public Path getPath()
  {
    return _path;
  }

  public long getLastModifiedAt()
  {
    return _lastModifiedAt;
  }
}
