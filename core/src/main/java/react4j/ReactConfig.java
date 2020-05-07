package react4j;

import grim.annotations.OmitType;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckConfig;

/**
 * Location of all compile time configuration settings for framework.
 */
@SuppressWarnings( "FieldMayBeFinal" )
@OmitType
final class ReactConfig
{
  @Nonnull
  private static final ConfigProvider PROVIDER = new ConfigProvider();
  private static final boolean PRODUCTION_ENVIRONMENT = PROVIDER.isProductionEnvironment();
  private static boolean ENABLE_COMPONENT_NAMES = PROVIDER.enableComponentNames();
  private static boolean SHOULD_MINIMIZE_PROP_KEYS = PROVIDER.shouldMinimizePropKeys();
  private static boolean SHOULD_VALIDATE_PROP_VALUES = PROVIDER.shouldValidatePropValues();
  private static boolean SHOULD_STORE_DEBUG_DATA_AS_STATE = PROVIDER.shouldStoreDebugDataAsState();
  private static boolean SHOULD_FREEZE_PROPS = PROVIDER.shouldFreezeProps();
  private static boolean CHECK_INVARIANTS = PROVIDER.shouldCheckInvariants();

  private ReactConfig()
  {
  }

  static boolean isDevelopmentEnvironment()
  {
    return !isProductionEnvironment();
  }

  static boolean isProductionEnvironment()
  {
    return PRODUCTION_ENVIRONMENT;
  }

  /**
   * Return true if components should have human readable names specified.
   * Useful if you want to interact via DevTools or other tool chains.
   *
   * @return true to enable human readable names for components.
   */
  static boolean enableComponentNames()
  {
    return ENABLE_COMPONENT_NAMES;
  }

  /**
   * Return true if the prop keys should be minimized.
   * This will significantly reduce the size of the compiled output but will make inspecting the props
   * in DevTools difficult if not impossible.
   *
   * @return true to minimize prop keys.
   */
  static boolean shouldMinimizePropKeys()
  {
    return SHOULD_MINIMIZE_PROP_KEYS;
  }

  /**
   * Return true if the prop value should be validated when initially set or when changed.
   *
   * @return true to validate prop values.
   */
  static boolean shouldValidatePropValues()
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
  static boolean shouldStoreDebugDataAsState()
  {
    return SHOULD_STORE_DEBUG_DATA_AS_STATE;
  }

  /**
   * Return true if invariants will be checked.
   *
   * @return true if invariants will be checked.
   */
  static boolean shouldCheckInvariants()
  {
    return CHECK_INVARIANTS && BrainCheckConfig.checkInvariants();
  }

  /**
   * Return true if props should be frozen before being passed to react.
   *
   * @return true if props should be frozen before being passed to react.
   */
  static boolean shouldFreezeProps()
  {
    return SHOULD_FREEZE_PROPS;
  }

  private static final class ConfigProvider
    extends AbstractConfigProvider
  {
    @GwtIncompatible
    @Override
    boolean isProductionEnvironment()
    {
      return "production".equals( System.getProperty( "react4j.environment", "production" ) );
    }

    @GwtIncompatible
    @Override
    boolean enableComponentNames()
    {
      return "true".equals( System.getProperty( "react4j.enable_component_names",
                                                PRODUCTION_ENVIRONMENT ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldMinimizePropKeys()
    {
      return "true".equals( System.getProperty( "react4j.minimize_prop_keys", PRODUCTION_ENVIRONMENT ? "true" : "false" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldValidatePropValues()
    {
      return "true".equals( System.getProperty( "react4j.validate_prop_values", PRODUCTION_ENVIRONMENT ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldStoreDebugDataAsState()
    {
      return "true".equals( System.getProperty( "react4j.store_debug_data_as_state",
                                                PRODUCTION_ENVIRONMENT ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldCheckInvariants()
    {
      return "true".equals( System.getProperty( "react4j.check_invariants", PRODUCTION_ENVIRONMENT ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldFreezeProps()
    {
      return "true".equals( System.getProperty( "react4j.freeze_props", PRODUCTION_ENVIRONMENT ? "false" : "true" ) );
    }
  }

  @SuppressWarnings( { "unused", "StringEquality" } )
  private static abstract class AbstractConfigProvider
  {
    boolean isProductionEnvironment()
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

    boolean shouldStoreDebugDataAsState()
    {
      return "true" == System.getProperty( "react4j.store_debug_data_as_state" );
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
