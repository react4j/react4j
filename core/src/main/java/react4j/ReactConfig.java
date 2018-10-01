package react4j;

import org.realityforge.braincheck.BrainCheckConfig;

/**
 * Location of all compile time configuration settings for framework.
 */
public final class ReactConfig
{
  private static final ConfigProvider PROVIDER = new ConfigProvider();
  private static final boolean PRODUCTION_MODE = PROVIDER.isProductionMode();
  private static final boolean ENABLE_NAMES = PROVIDER.enableComponentNames();
  private static final boolean SHOULD_MINIMIZE_PROP_KEYS = PROVIDER.shouldMinimizePropKeys();
  private static final boolean SHOULD_VALIDATE_PROP_VALUES = PROVIDER.shouldValidatePropValues();
  private static final boolean CHECK_COMPONENT_STATE_INVARIANTS = PROVIDER.checkComponentStateInvariants();
  private static final boolean CHECK_INVARIANTS = PROVIDER.shouldCheckInvariants();

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
   * @return true to enable human readable names for components.
   */
  public static boolean enableComponentNames()
  {
    return ENABLE_NAMES;
  }

  /**
   * Return true if the prop keys should be minimized.
   * This will significantly reduce the size of the compiled output but will make inspecting the props
   * in DevTools difficult if not impossible.
   *
   * @return true to minimize prop keys.
   */
  public static boolean shouldMinimizePropKeys()
  {
    return SHOULD_MINIMIZE_PROP_KEYS;
  }

  /**
   * Return true if the prop value should be validated when initially set or when changed.
   *
   * @return true to validate prop values.
   */
  public static boolean shouldValidatePropValues()
  {
    return SHOULD_VALIDATE_PROP_VALUES;
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

  private static final class ConfigProvider
    extends AbstractConfigProvider
  {
    @GwtIncompatible
    @Override
    boolean isProductionMode()
    {
      return "production".equals( System.getProperty( "react4j.environment", "production" ) );
    }

    @GwtIncompatible
    @Override
    boolean enableComponentNames()
    {
      return "true".equals( System.getProperty( "react4j.enable_component_names",
                                                isProductionMode() ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldMinimizePropKeys()
    {
      return "true".equals( System.getProperty( "react4j.minimize_prop_keys", isProductionMode() ? "true" : "false" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldValidatePropValues()
    {
      return "true".equals( System.getProperty( "react4j.validate_prop_values", isProductionMode() ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean checkComponentStateInvariants()
    {
      return "true".equals( System.getProperty( "react4j.check_component_state_invariants",
                                                isProductionMode() ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldCheckInvariants()
    {
      return "true".equals( System.getProperty( "react4j.check_invariants", "true" ) );
    }
  }

  @SuppressWarnings( { "unused", "StringEquality" } )
  private static abstract class AbstractConfigProvider
  {
    boolean isProductionMode()
    {
      return "production" == System.getProperty( "react4j.environment" );
    }

    boolean enableComponentNames()
    {
      return "true" == System.getProperty( "react4j.enable_component_names" );
    }

    boolean shouldMinimizePropKeys()
    {
      return "true" == System.getProperty( "react4j.minimize_prop_keys" );
    }

    boolean shouldValidatePropValues()
    {
      return "true" == System.getProperty( "react4j.validate_prop_values" );
    }

    boolean checkComponentStateInvariants()
    {
      return "true" == System.getProperty( "react4j.check_component_state_invariants" );
    }

    boolean shouldCheckInvariants()
    {
      return "true" == System.getProperty( "react4j.check_invariants" );
    }
  }
}
