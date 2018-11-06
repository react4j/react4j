package react4j;

import org.realityforge.braincheck.BrainCheckConfig;

/**
 * Location of all compile time configuration settings for framework.
 */
public final class ReactConfig
{
  private static final ConfigProvider PROVIDER = new ConfigProvider();
  private static final boolean ENABLE_NAMES = PROVIDER.enableComponentNames();
  private static final boolean SHOULD_MINIMIZE_PROP_KEYS = PROVIDER.shouldMinimizePropKeys();
  private static final boolean SHOULD_VALIDATE_PROP_VALUES = PROVIDER.shouldValidatePropValues();
  private static final boolean SHOULD_STORE_DEBUG_DATA_AS_STATE = PROVIDER.shouldStoreDebugDataAsState();
  private static final boolean SHOULD_FREEZE_PROPS = PROVIDER.shouldFreezeProps();
  private static final boolean CHECK_COMPONENT_STATE_INVARIANTS = PROVIDER.checkComponentStateInvariants();
  private static final boolean CHECK_INVARIANTS = PROVIDER.shouldCheckInvariants();

  private ReactConfig()
  {
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
   * Return true if react state should be used to store debug data.
   * Useful if you want to inspect the debug data via DevTools. This feature is resource intensive
   * and should not be enabled in production.
   *
   * @return true if react state should be used to store debug data.
   */
  public static boolean shouldStoreDebugDataAsState()
  {
    return SHOULD_STORE_DEBUG_DATA_AS_STATE;
  }

  /**
   * Return true if we should check that the user interacts with React component in a way compatible with the state.
   *
   * @return true to enable invariant checking about how we interact with native react component.
   */
  static boolean checkComponentStateInvariants()
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

  /**
   * Return true if props should be frozen before being passed to react.
   *
   * @return true if props should be frozen before being passed to react.
   */
  public static boolean shouldFreezeProps()
  {
    return SHOULD_FREEZE_PROPS;
  }

  private static final class ConfigProvider
    extends AbstractConfigProvider
  {
    @GwtIncompatible
    @Override
    boolean enableComponentNames()
    {
      return "true".equals( System.getProperty( "react4j.enable_component_names", "false" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldMinimizePropKeys()
    {
      return "true".equals( System.getProperty( "react4j.minimize_prop_keys", "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldValidatePropValues()
    {
      return "true".equals( System.getProperty( "react4j.validate_prop_values", "false" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldStoreDebugDataAsState()
    {
      return "true".equals( System.getProperty( "react4j.store_debug_data_as_state", "false" ) );
    }

    @GwtIncompatible
    @Override
    boolean checkComponentStateInvariants()
    {
      return "true".equals( System.getProperty( "react4j.check_component_state_invariants", "false" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldCheckInvariants()
    {
      return "true".equals( System.getProperty( "react4j.check_invariants", "false" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldFreezeProps()
    {
      return "true".equals( System.getProperty( "react4j.freeze_props", "false" ) );
    }
  }

  @SuppressWarnings( { "unused", "StringEquality" } )
  private static abstract class AbstractConfigProvider
  {
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

    boolean shouldStoreDebugDataAsState()
    {
      return "true" == System.getProperty( "react4j.store_debug_data_as_state" );
    }

    boolean checkComponentStateInvariants()
    {
      return "true" == System.getProperty( "react4j.check_component_state_invariants" );
    }

    boolean shouldCheckInvariants()
    {
      return "true" == System.getProperty( "react4j.check_invariants" );
    }

    boolean shouldFreezeProps()
    {
      return "true" == System.getProperty( "react4j.freeze_props" );
    }
  }
}
