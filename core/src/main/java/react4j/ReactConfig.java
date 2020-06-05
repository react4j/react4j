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
  private static boolean ENABLE_VIEW_NAMES = PROVIDER.enableViewNames();
  private static boolean MINIMIZE_INPUT_KEYS = PROVIDER.shouldMinimizeInputKeys();
  private static boolean SHOULD_VALIDATE_INPUT_VALUES = PROVIDER.shouldValidateInputValues();
  private static boolean SHOULD_STORE_DEBUG_DATA_AS_STATE = PROVIDER.shouldStoreDebugDataAsState();
  private static boolean SHOULD_FREEZE_INPUTS = PROVIDER.shouldFreezeInputs();
  private static boolean CHECK_INVARIANTS = PROVIDER.shouldCheckInvariants();

  private ReactConfig()
  {
  }

  static boolean isProductionEnvironment()
  {
    return PRODUCTION_ENVIRONMENT;
  }

  /**
   * Return true if views should have human readable names specified.
   * Useful if you want to interact via DevTools or other tool chains.
   *
   * @return true to enable human readable names for views.
   */
  static boolean enableViewNames()
  {
    return ENABLE_VIEW_NAMES;
  }

  /**
   * Return true if the input keys should be minimized.
   * This will significantly reduce the size of the compiled output but will make inspecting the inputs
   * in DevTools difficult if not impossible.
   *
   * @return true to minimize input keys.
   */
  static boolean shouldMinimizeInputKeys()
  {
    return MINIMIZE_INPUT_KEYS;
  }

  /**
   * Return true if the input value should be validated when initially set or when changed.
   *
   * @return true to validate input values.
   */
  static boolean shouldValidateInputValues()
  {
    return SHOULD_VALIDATE_INPUT_VALUES;
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
   * Return true if inputs should be frozen before being passed to react.
   *
   * @return true if inputs should be frozen before being passed to react.
   */
  static boolean shouldFreezeInputs()
  {
    return SHOULD_FREEZE_INPUTS;
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
    boolean enableViewNames()
    {
      return "true".equals( System.getProperty( "react4j.enable_view_names",
                                                PRODUCTION_ENVIRONMENT ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldMinimizeInputKeys()
    {
      return "true".equals( System.getProperty( "react4j.minimize_input_keys", PRODUCTION_ENVIRONMENT ? "true" : "false" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldValidateInputValues()
    {
      return "true".equals( System.getProperty( "react4j.validate_input_values", PRODUCTION_ENVIRONMENT ? "false" : "true" ) );
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
    boolean shouldFreezeInputs()
    {
      return "true".equals( System.getProperty( "react4j.freeze_inputs", PRODUCTION_ENVIRONMENT ? "false" : "true" ) );
    }
  }

  @SuppressWarnings( { "unused", "StringEquality" } )
  private static abstract class AbstractConfigProvider
  {
    boolean isProductionEnvironment()
    {
      return "production" == System.getProperty( "react4j.environment" );
    }

    boolean enableViewNames()
    {
      return "true" == System.getProperty( "react4j.enable_view_names" );
    }

    boolean shouldMinimizeInputKeys()
    {
      return "true" == System.getProperty( "react4j.minimize_input_keys" );
    }

    boolean shouldValidateInputValues()
    {
      return "true" == System.getProperty( "react4j.validate_input_values" );
    }

    boolean shouldStoreDebugDataAsState()
    {
      return "true" == System.getProperty( "react4j.store_debug_data_as_state" );
    }

    boolean shouldCheckInvariants()
    {
      return "true" == System.getProperty( "react4j.check_invariants" );
    }

    boolean shouldFreezeInputs()
    {
      return "true" == System.getProperty( "react4j.freeze_inputs" );
    }
  }
}
