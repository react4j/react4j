package react4j.downstream;

import java.io.IOException;
import java.util.Properties;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
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
    throws IOException
  {
    final Properties buildStatistics = loadBuildStatistics();
    final Properties fixtureStatistics = loadFixtureStatistics();

    final long fixtureSize =
      extractSize( fixtureStatistics, getArezVersion() + "." + branch );
    final long beforeSize = extractSize( buildStatistics, branch + "." + "before" );
    final long afterSize = extractSize( buildStatistics, branch + "." + "after" );

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
        fail( "Build size changed after upgrading react4j in branch '" + branch +
              "' from " + beforeSize + " to " + afterSize + ". If this is " +
              "acceptable then re-run the build passing PRODUCT_VERSION=... " +
              "and STORE_BUILD_STATISTICS=true to update the fixture file." );
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
