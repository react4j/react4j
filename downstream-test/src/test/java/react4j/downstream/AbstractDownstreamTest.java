package react4j.downstream;

import gir.sys.SystemProperty;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.realityforge.gwt.symbolmap.SoycSizeMaps;
import org.realityforge.gwt.symbolmap.SymbolEntryIndex;

abstract class AbstractDownstreamTest
{
  @Nonnull
  final String getArezVersion()
  {
    return SystemProperty.get( "react4j.version" );
  }

  @Nonnull
  final Properties loadProperties( @Nonnull final File file )
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
  final Path getFixtureDir()
  {
    return Paths
      .get( SystemProperty.get( "react4j.deploy_test.fixture_dir" ) )
      .toAbsolutePath()
      .normalize();
  }

  @Nonnull
  final Path getArchiveDir()
  {
    return getWorkDir().resolve( "archive" );
  }

  @Nonnull
  final Path getWorkDir()
  {
    return Paths
      .get( SystemProperty.get( "react4j.deploy_test.work_dir" ) )
      .toAbsolutePath()
      .normalize();
  }

  @Nonnull
  final SoycSizeMaps getSoycSizeMaps( @Nonnull final Path archiveDir, @Nonnull final String build )
    throws Exception
  {
    return SoycSizeMaps.readFromGzFile( getStoriesPath( archiveDir, build ) );
  }

  @Nonnull
  private Path getStoriesPath( @Nonnull final Path archiveDir, @Nonnull final String build )
    throws IOException
  {
    return archiveDir
      .resolve( build )
      .resolve( "compileReports" )
      .resolve( "todomvc" )
      .resolve( "soycReport" )
      .resolve( "stories0.xml.gz" );
  }

  @Nonnull
  final SymbolEntryIndex getSymbolMapIndex( @Nonnull final Path archiveDir, @Nonnull final String build )
    throws Exception
  {
    return SymbolEntryIndex.readSymbolMapIntoIndex( getSymbolMapPath( archiveDir, build ) );
  }

  @Nonnull
  private Path getSymbolMapPath( @Nonnull final Path archiveDir, @Nonnull final String build )
    throws IOException
  {
    final Path symbolMapsDir =
      archiveDir
        .resolve( build )
        .resolve( "assets" )
        .resolve( "WEB-INF" )
        .resolve( "deploy" )
        .resolve( "todomvc" )
        .resolve( "symbolMaps" );

    return Files.list( symbolMapsDir ).findFirst().orElseThrow( AssertionError::new );
  }
}
