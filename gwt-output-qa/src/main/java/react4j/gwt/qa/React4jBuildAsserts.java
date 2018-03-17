package react4j.gwt.qa;

import javax.annotation.Nonnull;
import org.realityforge.gwt.symbolmap.SymbolEntryIndex;

/**
 * A collection of assertions about the expected symbols present in GWT javascript output.
 */
public final class React4jBuildAsserts
{
  private React4jBuildAsserts()
  {
  }

  /**
   * This assertion verifies that the standard inlines have occurred.
   *
   * @param index the index that contains all symbols for output target.
   */
  public static void assertStandardOutputs( @Nonnull final SymbolEntryIndex index )
  {
    // This should never appear as it is not meant to be GWT compiled
    index.assertNoClassNameMatches( "react4j\\.annotations\\..*" );
    index.assertNoClassNameMatches( "react4j\\.core\\.ReactConfig" );

    // This should be eliminated as it will improve the ability for GWT compiler to dead-code-eliminate
    index.assertNoMemberMatches( "react4j\\.core\\.ReactConfig", "$clinit" );
  }

  /**
   * This assertion verifies that the symbols that are conditional on the `react4j.check_component_state_invariants`
   * setting are present if enabled and not present if not enabled.
   *
   * @param index   the index that contains all symbols for output target.
   * @param enabled true if setting is enabled, false otherwise.
   */
  public static void assertCheckComponentStateInvariants( @Nonnull final SymbolEntryIndex index, final boolean enabled )
  {
    index.assertSymbol( "react4j\\.core\\.ComponentPhase", enabled );
    index.assertSymbol( "react4j\\.core\\.LifecycleMethod", enabled );
    index.assertSymbol( "react4j\\.core\\.LifecycleMethod", "_phase", enabled );
    index.assertSymbol( "react4j\\.core\\.LifecycleMethod", "_lifecycleMethod", enabled );
  }

  /**
   * Assert normal react4j outputs based on specified compile time settings.
   *
   * @param index                         the index that contains all symbols for output target.
   * @param checkComponentStateInvariants the value of the `react4j.check_component_state_invariants` setting.
   */
  public static void assertReact4jOutputs( @Nonnull final SymbolEntryIndex index,
                                           final boolean checkComponentStateInvariants )
  {
    assertStandardOutputs( index );
    assertCheckComponentStateInvariants( index, checkComponentStateInvariants );
  }
}
