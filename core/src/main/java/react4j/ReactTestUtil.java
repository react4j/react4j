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
      disableViewNames();
      noCheckInvariants();
      minimizeInputKeys();
      noValidateInputValues();
      noStoreDebugDataAsState();
    }
    else
    {
      enableViewNames();
      checkInvariants();
      noMinimizeInputKeys();
      validateInputValues();
      storeDebugDataAsState();
    }
    Contexts.setContextProvider( new Contexts.DefaultContextProvider() );
    Contexts.clear();
  }

  /**
   * Set the `react4j.enable_view_names` setting to {@code true}.
   */
  public static void enableViewNames()
  {
    setViewEnableNames( true );
  }

  /**
   * Set the `react4j.enable_view_names` setting to {@code false}.
   */
  public static void disableViewNames()
  {
    setViewEnableNames( true );
  }

  /**
   * Configure the `react4j.enable_view_names` setting.
   *
   * @param setting the setting.
   */
  public static void setViewEnableNames( final boolean setting )
  {
    setConstant( "ENABLE_VIEW_NAMES", setting );
  }

  /**
   * Set the `react4j.minimize_input_keys` setting to {@code true}.
   */
  public static void minimizeInputKeys()
  {
    setMinimizeInputKeys( true );
  }

  /**
   * Set the `react4j.minimize_input_keys` setting to {@code false}.
   */
  public static void noMinimizeInputKeys()
  {
    setMinimizeInputKeys( false );
  }

  /**
   * Configure the `react4j.minimize_input_keys` setting.
   *
   * @param setting the setting.
   */
  private static void setMinimizeInputKeys( final boolean setting )
  {
    setConstant( "MINIMIZE_INPUT_KEYS", setting );
  }

  /**
   * Set the `react4j.validate_input_values` setting to {@code true}.
   */
  public static void validateInputValues()
  {
    setValidateInputValues( true );
  }

  /**
   * Set the `react4j.validate_input_values` setting to {@code false}.
   */
  public static void noValidateInputValues()
  {
    setValidateInputValues( false );
  }

  /**
   * Configure the `react4j.validate_input_values` setting.
   *
   * @param setting the setting.
   */
  private static void setValidateInputValues( final boolean setting )
  {
    setConstant( "SHOULD_VALIDATE_INPUT_VALUES", setting );
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
