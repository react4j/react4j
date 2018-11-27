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
   * Assert normal react4j outputs based on specified compile time settings.
   *
   * @param index the index that contains all symbols for output target.
   */
  public static void assertReact4jOutputs( @Nonnull final SymbolEntryIndex index )
  {
    assertStandardOutputs( index );
  }
}
