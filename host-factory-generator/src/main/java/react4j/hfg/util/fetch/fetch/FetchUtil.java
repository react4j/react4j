package react4j.hfg.util.fetch.fetch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class FetchUtil
{
  private static final int CONNECT_TIMEOUT = 60_000;
  private static final int READ_TIMEOUT = 60_000;

  private FetchUtil()
  {
  }

  @Nullable
  public static FetchResult downloadURL( @Nonnull final String url, final long lastModifiedAt )
    throws FetchException
  {
    try
    {
      final URL sourceURL = new File( "." ).toURI().resolve( url ).toURL();
      if ( 0 != lastModifiedAt &&
           ( "http".equals( sourceURL.getProtocol() ) || "https".equals( sourceURL.getProtocol() ) ) )
      {
        final HttpURLConnection connection = (HttpURLConnection) newUrlConnection( sourceURL );
        connection.setIfModifiedSince( lastModifiedAt );
        connection.setRequestMethod( "HEAD" );

        if ( HttpURLConnection.HTTP_NOT_MODIFIED == connection.getResponseCode() )
        {
          return null;
        }
      }

      final URLConnection connection = newUrlConnection( sourceURL );
      try ( final InputStream inputStream = new BufferedInputStream( connection.getInputStream() ) )
      {
        final long lastModified = connection.getLastModified();
        final Path file = Files.createTempFile( "react4j", ".html" );
        try ( final OutputStream outputStream = new BufferedOutputStream( new FileOutputStream( file.toFile() ) ) )
        {
          final byte[] buffer = new byte[ 1024 * 4 ];
          int count;
          while ( -1 != ( count = inputStream.read( buffer ) ) )
          {
            outputStream.write( buffer, 0, count );
          }
        }
        return new FetchResult( url, file, 0 == lastModified ? System.currentTimeMillis() : lastModified );
      }
    }
    catch ( final IOException ioe )
    {
      throw new FetchException( url, ioe );
    }
  }

  @Nonnull
  private static URLConnection newUrlConnection( @Nonnull final URL sourceURL )
    throws IOException
  {
    final URLConnection connection = sourceURL.openConnection();
    connection.setConnectTimeout( CONNECT_TIMEOUT );
    connection.setReadTimeout( READ_TIMEOUT );
    return connection;
  }
}
