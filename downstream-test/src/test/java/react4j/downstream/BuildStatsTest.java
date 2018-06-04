package react4j.downstream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.realityforge.gwt.symbolmap.SymbolEntry;
import org.realityforge.gwt.symbolmap.SymbolEntryIndex;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class BuildStatsTest
  extends AbstractDownstreamTest
{
  @Test
  public void raw()
    throws Exception
  {
    compareSizesForBranch( "raw" );
  }

  @Test
  public void arez()
    throws Exception
  {
    compareSizesForBranch( "arez" );
  }

  @Test
  public void dagger()
    throws Exception
  {
    compareSizesForBranch( "dagger" );
  }

  private void compareSizesForBranch( @Nonnull final String branch )
    throws Exception
  {
    final Properties buildStatistics = loadBuildStatistics();
    final Properties fixtureStatistics = loadFixtureStatistics();

    final long fixtureSize =
      extractSize( fixtureStatistics, getArezVersion() + "." + branch );
    final String beforeBuild = branch + "." + "before";
    final String afterBuild = branch + "." + "after";
    final long beforeSize = extractSize( buildStatistics, beforeBuild );
    final long afterSize = extractSize( buildStatistics, afterBuild );

    if ( 0 != fixtureSize )
    {
      if ( fixtureSize != afterSize )
      {
        fail( "Build size does not match the value specified in fixtures file for branch '" + branch +
              "'. The fixture specifies the value as " + fixtureSize + " while the actual size is " +
              afterSize + ". If this is acceptable then re-run the build passing PRODUCT_VERSION=... " +
              "and STORE_BUILD_STATISTICS=true to update the fixture file." );
      }
    }
    else
    {
      if ( beforeSize != afterSize )
      {
        reportSymbolDifferences( beforeBuild, afterBuild );
        fail( "Build size changed after upgrading react4j in branch '" + branch +
              "' from " + beforeSize + " to " + afterSize + ". If this is " +
              "acceptable then re-run the build passing PRODUCT_VERSION=... " +
              "and STORE_BUILD_STATISTICS=true to update the fixture file." );
      }
    }
  }

  private void reportSymbolDifferences( @Nonnull final String beforeBuild, @Nonnull final String afterBuild )
    throws Exception
  {
    final SymbolEntryIndex beforeIndex = getSymbolMapIndex( getArchiveDir(), beforeBuild );
    final SymbolEntryIndex afterIndex = getSymbolMapIndex( getArchiveDir(), afterBuild );
    final ArrayList<SymbolEntry> missingEntries = new ArrayList<>();
    final ArrayList<SymbolEntry> addedEntries = new ArrayList<>();
    for ( final SymbolEntry entry : beforeIndex.findSymbolsByClassName( ".*" ) )
    {
      final List<SymbolEntry> matches =
        afterIndex.findSymbolsByPatterns( Pattern.quote( entry.getClassName() ),
                                          Pattern.quote( entry.getMemberName() ) );
      if ( matches.isEmpty() )
      {
        missingEntries.add( entry );
      }
    }
    for ( final SymbolEntry entry : afterIndex.findSymbolsByClassName( ".*" ) )
    {
      final List<SymbolEntry> matches =
        beforeIndex.findSymbolsByPatterns( Pattern.quote( entry.getClassName() ),
                                           Pattern.quote( entry.getMemberName() ) );
      if ( matches.isEmpty() )
      {
        addedEntries.add( entry );
      }
    }
    if ( !missingEntries.isEmpty() || !addedEntries.isEmpty() )
    {
      System.out.println( "Differences detected in symbols compiled between the " +
                          "two variants " + beforeBuild + " and " + afterBuild );
      if ( !missingEntries.isEmpty() )
      {
        System.out.println( "Removed symbols:" );
        missingEntries.forEach( e -> System.out.println( "  " + e ) );
      }
      if ( !addedEntries.isEmpty() )
      {
        System.out.println( "Added symbols:" );
        addedEntries.forEach( e -> System.out.println( "  " + e ) );
      }
    }
  }

  @Nonnegative
  private long extractSize( @Nonnull final Properties properties,
                            @Nonnull final String prefix )
  {
    return Long.parseLong( properties.getProperty( prefix + ".todomvc.size", "0" ) );
  }

  @Nonnull
  private Properties loadBuildStatistics()
    throws IOException
  {
    return loadProperties( getWorkDir().resolve( "statistics.properties" ).toFile() );
  }

  @Nonnull
  private Properties loadFixtureStatistics()
    throws IOException
  {
    return loadProperties( getFixtureDir().resolve( "statistics.properties" ).toFile() );
  }
}
