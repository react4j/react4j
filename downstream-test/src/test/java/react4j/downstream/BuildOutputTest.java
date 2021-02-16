package react4j.downstream;

import gir.sys.SystemProperty;
import grim.asserts.RuleSet;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.realityforge.gwt.symbolmap.SymbolEntry;
import org.realityforge.gwt.symbolmap.SymbolEntryIndex;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class BuildOutputTest
  extends AbstractDownstreamTest
{
  @Test
  public void rawProduction()
    throws Exception
  {
    final SymbolEntryIndex index = getSymbolMapIndex( getArchiveDir(), "raw.after" );
    final RuleSet coreRuleSet = RuleSet.loadFromArchive( Paths.get( SystemProperty.get( "react4j.next.core.jar" ) ) );
    final RuleSet domRuleSet = RuleSet.loadFromArchive( Paths.get( SystemProperty.get( "react4j.next.dom.jar" ) ) );
    final RuleSet ruleSet = RuleSet.combine( coreRuleSet, domRuleSet );

    // Figure out how to source these values from the build and push these tests into downstream applications.
    final Map<String, String> compileTimeProperties = new TreeMap<>();
    compileTimeProperties.put( "react4j.environment", "production" );
    compileTimeProperties.put( "react4j.enable_component_names", "false" );
    compileTimeProperties.put( "react4j.check_invariants", "false" );
    compileTimeProperties.put( "react4j.minimize_input_keys", "true" );
    compileTimeProperties.put( "react4j.validate_input_values", "false" );
    compileTimeProperties.put( "react4j.store_debug_data_as_state", "false" );

    final List<SymbolEntry> symbols = getInvalidSymbolEntries( index, ruleSet, compileTimeProperties );
    if ( !symbols.isEmpty() )
    {
      final String message =
        "Symbols that were expected to be stripped from the output javascript have not been.\n" +
        "  Compile Time Properties: " + compileTimeProperties + "\n" +
        "  Symbols expected to be omitted but present in output:\n" +
        symbols.stream().map( s -> "    " + s + "\n" ).collect( Collectors.joining() );
      fail( message );
    }
  }

  @Nonnull
  private List<SymbolEntry> getInvalidSymbolEntries( @Nonnull final SymbolEntryIndex index,
                                                     @Nonnull final RuleSet ruleSet,
                                                     @Nonnull final Map<String, String> compileTimeProperties )
  {
    final List<SymbolEntry> symbols = new ArrayList<>();
    for ( final SymbolEntry entry : index.getSymbolEntries() )
    {
      if ( ruleSet.shouldOmitSymbol( compileTimeProperties, entry.getClassName(), entry.getMemberName() ) )
      {
        symbols.add( entry );
      }
    }
    return symbols;
  }
}
