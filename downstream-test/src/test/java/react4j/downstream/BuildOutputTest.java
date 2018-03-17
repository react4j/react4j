package react4j.downstream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import org.realityforge.gwt.symbolmap.SymbolEntryIndex;
import org.testng.annotations.Test;
import react4j.gwt.qa.React4jBuildAsserts;

public class BuildOutputTest
  extends AbstractDownstreamTest
{
  @Test
  public void rawProduction()
    throws Exception
  {
    final String build = "raw.after";

    final SymbolEntryIndex index = SymbolEntryIndex.readSymbolMapIntoIndex( getSymbolMapPath( build ) );

    React4jBuildAsserts.assertReact4jOutputs( index, false );
  }

  @Nonnull
  private Path getSymbolMapPath( @Nonnull final String build )
    throws IOException
  {
    final Path symbolMapsDir =
      getArchiveDir()
        .resolve( build )
        .resolve( "assets" )
        .resolve( "WEB-INF" )
        .resolve( "deploy" )
        .resolve( "todomvc" )
        .resolve( "symbolMaps" );

    return Files.list( symbolMapsDir ).findFirst().orElseThrow( AssertionError::new );
  }
}
