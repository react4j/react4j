package react4j.core;

import org.realityforge.braincheck.BrainCheckConfig;

/**
 * Location of all compile time configuration settings for framework.
 */
public final class ReactConfig
{
  private static final boolean PRODUCTION_MODE =
    System.getProperty( "react4j.environment", "production" ).equals( "production" );
  private static boolean ENABLE_NAMES =
    "true".equals( System.getProperty( "react4j.enable_component_names", PRODUCTION_MODE ? "false" : "true" ) );
  private static final boolean CHECK_COMPONENT_STATE_INVARIANTS =
    "true".equals( System.getProperty( "react4j.check_component_state_invariants",
                                       PRODUCTION_MODE ? "false" : "true" ) );
  private static final boolean CHECK_INVARIANTS =
    "true".equals( System.getProperty( "react4j.check_invariants", PRODUCTION_MODE ? "false" : "true" ) );

  private ReactConfig()
  {
  }

  /**
   * Return true if in production mode.
   * Production mode sets the default values for other compile time constants to the variant
   * that assumes your code is correct and does not generate additional assert or debug statements.
   * The individual configuration settings can still be specified to override this value.
   *
   * @return true if in production mode.
   */
  public static boolean isProductionMode()
  {
    return PRODUCTION_MODE;
  }

  /**
   * Return true if components should have human readable names specified.
   * Useful if you want to interact via DevTools or other tool chains.
   *
   * @return to enable human readable names for components.
   */
  public static boolean enableComponentNames()
  {
    return ENABLE_NAMES;
  }

  /**
   * Return true if we should check that the user interacts with React component in a way compatible with the state.
   * i.e. setInitialState should only be invoked at the start, setState should not be invoked while render
   * is running etc.
   *
   * @return true to enable invariant checking about how we interact with native react component.
   */
  public static boolean checkComponentStateInvariants()
  {
    return CHECK_COMPONENT_STATE_INVARIANTS;
  }

  /**
   * Return true if invariants will be checked.
   *
   * @return true if invariants will be checked.
   */
  public static boolean shouldCheckInvariants()
  {
    return CHECK_INVARIANTS && BrainCheckConfig.checkInvariants();
  }
}
