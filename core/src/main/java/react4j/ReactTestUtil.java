package react4j;

import java.lang.reflect.Field;
import javax.annotation.Nonnull;

/**
 * Utility class for interacting with React4j in tests.
 */
@GwtIncompatible
public final class ReactTestUtil
{
  private ReactTestUtil()
  {
  }

  /**
   * Reset the state of React config to either production or development state.
   *
   * @param productionMode true to set it to production mode configuration, false to set it to development mode config.
   */
  public static void resetConfig( final boolean productionMode )
  {
    if ( ReactConfig.isProductionEnvironment() )
    {
      throw new IllegalStateException( "Unable to reset config as React is in production mode" );
    }

    if ( productionMode )
    {
      disableComponentNames();
      noCheckInvariants();
      minimizePropKeys();
      noValidatePropValues();
      noStoreDebugDataAsState();
      noFreezeProps();
    }
    else
    {
      enableComponentNames();
      checkInvariants();
      noMinimizePropKeys();
      validatePropValues();
      storeDebugDataAsState();
      freezeProps();
    }
    Contexts.setContextProvider( new Contexts.DefaultContextProvider() );
    Contexts.clear();
  }

  /**
   * Set the `react4j.enable_component_names` setting to {@code true}.
   */
  public static void enableComponentNames()
  {
    setComponentEnableNames( true );
  }

  /**
   * Set the `react4j.enable_component_names` setting to {@code false}.
   */
  public static void disableComponentNames()
  {
    setComponentEnableNames( true );
  }

  /**
   * Configure the `react4j.enable_component_names` setting.
   *
   * @param setting the setting.
   */
  public static void setComponentEnableNames( final boolean setting )
  {
    setConstant( "ENABLE_COMPONENT_NAMES", setting );
  }

  /**
   * Set the `react4j.minimize_prop_keys` setting to {@code true}.
   */
  public static void minimizePropKeys()
  {
    setMinimizePropKeys( true );
  }

  /**
   * Set the `react4j.minimize_prop_keys` setting to {@code false}.
   */
  public static void noMinimizePropKeys()
  {
    setMinimizePropKeys( false );
  }

  /**
   * Configure the `react4j.minimize_prop_keys` setting.
   *
   * @param setting the setting.
   */
  private static void setMinimizePropKeys( final boolean setting )
  {
    setConstant( "SHOULD_MINIMIZE_PROP_KEYS", setting );
  }

  /**
   * Set the `react4j.validate_prop_values` setting to {@code true}.
   */
  public static void validatePropValues()
  {
    setValidatePropValues( true );
  }

  /**
   * Set the `react4j.validate_prop_values` setting to {@code false}.
   */
  public static void noValidatePropValues()
  {
    setValidatePropValues( false );
  }

  /**
   * Configure the `react4j.validate_prop_values` setting.
   *
   * @param setting the setting.
   */
  private static void setValidatePropValues( final boolean setting )
  {
    setConstant( "SHOULD_VALIDATE_PROP_VALUES", setting );
  }

  /**
   * Set the `react4j.store_debug_data_as_state` setting to {@code true}.
   */
  public static void storeDebugDataAsState()
  {
    setStoreDebugDataAsState( true );
  }

  /**
   * Set the `react4j.store_debug_data_as_state` setting to {@code false}.
   */
  public static void noStoreDebugDataAsState()
  {
    setStoreDebugDataAsState( false );
  }

  /**
   * Configure the `react4j.store_debug_data_as_state` setting.
   *
   * @param setting the setting.
   */
  private static void setStoreDebugDataAsState( final boolean setting )
  {
    setConstant( "SHOULD_STORE_DEBUG_DATA_AS_STATE", setting );
  }

  /**
   * Set the `react4j.freeze_props` setting to {@code true}.
   */
  public static void freezeProps()
  {
    setFreezeProps( true );
  }

  /**
   * Set the `react4j.freeze_props` setting to {@code false}.
   */
  public static void noFreezeProps()
  {
    setFreezeProps( false );
  }

  /**
   * Configure the `react4j.freeze_props` setting.
   *
   * @param setting the setting.
   */
  private static void setFreezeProps( final boolean setting )
  {
    setConstant( "SHOULD_FREEZE_PROPS", setting );
  }

  /**
   * Set the `react4j.check_invariants` setting to {@code true}.
   */
  public static void checkInvariants()
  {
    setCheckInvariants( true );
  }

  /**
   * Set the `react4j.check_invariants` setting to {@code false}.
   */
  public static void noCheckInvariants()
  {
    setCheckInvariants( false );
  }

  /**
   * Configure the `react4j.check_invariants` setting.
   *
   * @param setting the setting.
   */
  private static void setCheckInvariants( final boolean setting )
  {
    setConstant( "CHECK_INVARIANTS", setting );
  }

  /**
   * Set the specified field name on BrainCheckConfig.
   */
  @SuppressWarnings( "NonJREEmulationClassesInClientCode" )
  private static void setConstant( @Nonnull final String fieldName, final boolean value )
  {
    try
    {
      final Field field = ReactConfig.class.getDeclaredField( fieldName );
      field.setAccessible( true );
      field.set( null, value );
    }
    catch ( NoSuchFieldException | IllegalAccessException e )
    {
      throw new IllegalStateException( "Unable to change constant " + fieldName, e );
    }
  }
}
