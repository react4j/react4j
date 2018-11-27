package react4j.downstream;

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
    final SymbolEntryIndex index = getSymbolMapIndex( getArchiveDir(), "raw.after" );
    React4jBuildAsserts.assertReact4jOutputs( index );
  }
}
