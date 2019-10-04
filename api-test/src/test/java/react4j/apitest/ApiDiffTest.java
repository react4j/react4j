package react4j.apitest;

import gir.io.Exec;
import gir.sys.SystemProperty;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.annotation.Nonnull;
import javax.json.Json;
import javax.json.JsonArray;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class ApiDiffTest
{
  @Test
  public void compareApi()
    throws Exception
  {
    final boolean storeApiDiff = SystemProperty.get( "react4j.api_test.store_api_diff" ).equals( "true" );
    final File reportFile = storeApiDiff ? getFixtureReport() : File.createTempFile( "apidiff", ".json" );
    try
    {
      generateReport( reportFile );
      final JsonArray differences = Json.createReader( new FileInputStream( reportFile ) ).readArray();
      final File fixture = getFixtureReport();
      if ( !fixture.exists() )
      {
        if ( !differences.isEmpty() )
        {
          fail( "Unable to locate test fixture describing expected API changes when there is " + differences.size() +
                " differences detected. Expected fixture file to be located at " + fixture.getAbsolutePath() );
        }
      }
      else if ( storeApiDiff && differences.isEmpty() )
      {
        if ( fixture.exists() )
        {
          System.out.println( "Deleting existing fixture file as no API differences detected. Fixture: " + fixture );
          assertTrue( fixture.delete() );
        }
      }
      else
      {
        final byte[] reportData = Files.readAllBytes( reportFile.toPath() );
        final byte[] expectedData = Files.readAllBytes( fixture.toPath() );
        assertEquals( new String( reportData, StandardCharsets.UTF_8 ),
                      new String( expectedData, StandardCharsets.UTF_8 ) );
      }
    }
    finally
    {
      if ( !storeApiDiff )
      {
        assertTrue( !reportFile.exists() || reportFile.delete() );
      }
    }
  }

  private void generateReport( @Nonnull final File reportFile )
  {
    final String oldCoreApiLabel =
      "org.realityforge.react4j:react4j-core:jar:" + SystemProperty.get( "react4j.prev.version" );
    final String oldCoreApi = oldCoreApiLabel + "::" + SystemProperty.get( "react4j.prev.core.jar" );
    final String oldDomApiLabel =
      "org.realityforge.react4j:react4j-dom:jar:" + SystemProperty.get( "react4j.prev.version" );
    final String oldDomApi = oldDomApiLabel + "::" + SystemProperty.get( "react4j.prev.dom.jar" );
    final String newCoreApiLabel =
      "org.realityforge.react4j:react4j-core:jar:" + SystemProperty.get( "react4j.next.version" );
    final String newCoreApi = newCoreApiLabel + "::" + SystemProperty.get( "react4j.next.core.jar" );
    final String newDomApiLabel =
      "org.realityforge.react4j:react4j-dom:jar:" + SystemProperty.get( "react4j.next.version" );
    final String newDomApi = newDomApiLabel + "::" + SystemProperty.get( "react4j.next.dom.jar" );
    Exec.system( "java",
                 "-jar", SystemProperty.get( "react4j.revapi.jar" ),
                 "--old-api", oldCoreApi,
                 "--old-api", oldDomApi,
                 "--new-api", newCoreApi,
                 "--new-api", newDomApi,
                 "--output-file", reportFile.toString() );
  }

  @Nonnull
  private File getFixtureReport()
  {
    return new File( SystemProperty.get( "react4j.api_test.fixture_dir" ),
                     SystemProperty.get( "react4j.prev.version" ) + "-" +
                     SystemProperty.get( "react4j.next.version" ) + ".json" );
  }
}
