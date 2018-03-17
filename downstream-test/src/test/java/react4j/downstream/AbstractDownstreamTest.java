package react4j.downstream;

import gir.sys.SystemProperty;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public abstract class AbstractDownstreamTest
{
  @Nonnull
  protected final String getArezVersion()
  {
    return SystemProperty.get( "react4j.version" );
  }

  @Nonnull
  protected final Properties loadProperties( @Nonnegative final File file )
    throws IOException
  {
    final Properties properties = new Properties();
    try ( final FileReader fileReader = new FileReader( file ) )
    {
      properties.load( fileReader );
    }
    return properties;
  }

  @Nonnull
  protected final Path getFixtureDir()
  {
    return Paths
      .get( SystemProperty.get( "react4j.deploy_test.fixture_dir" ) )
      .toAbsolutePath()
      .normalize();
  }

  @Nonnull
  protected final Path getArchiveDir()
  {
    return getWorkDir().resolve( "archive" );
  }

  @Nonnull
  protected final Path getWorkDir()
  {
    return Paths
      .get( SystemProperty.get( "react4j.deploy_test.work_dir" ) )
      .toAbsolutePath()
      .normalize();
  }
}
