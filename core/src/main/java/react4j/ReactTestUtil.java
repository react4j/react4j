package react4j;

/**
 * Utility class for interacting with React4j in tests.
 */
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
    setViewEnableNames( false );
  }

  /**
   * Configure the `react4j.enable_view_names` setting.
   *
   * @param setting the setting.
   */
  public static void setViewEnableNames( final boolean setting )
  {
    ReactConfig.setEnableViewNames( setting );
  }

  /**
   * Set the `react4j.minimize_input_keys` setting to {@code true}.
   */
  public static void minimizeInputKeys()
  {
    ReactConfig.setMinimizeInputKeys( true );
  }

  /**
   * Set the `react4j.minimize_input_keys` setting to {@code false}.
   */
  public static void noMinimizeInputKeys()
  {
    ReactConfig.setMinimizeInputKeys( false );
  }

  /**
   * Set the `react4j.validate_input_values` setting to {@code true}.
   */
  public static void validateInputValues()
  {
    ReactConfig.setShouldValidateInputValues( true );
  }

  /**
   * Set the `react4j.validate_input_values` setting to {@code false}.
   */
  public static void noValidateInputValues()
  {
    ReactConfig.setShouldValidateInputValues( false );
  }

  /**
   * Set the `react4j.store_debug_data_as_state` setting to {@code true}.
   */
  public static void storeDebugDataAsState()
  {
    ReactConfig.setShouldStoreDebugDataAsState( true );
  }

  /**
   * Set the `react4j.store_debug_data_as_state` setting to {@code false}.
   */
  public static void noStoreDebugDataAsState()
  {
    ReactConfig.setShouldStoreDebugDataAsState( false );
  }

  /**
   * Set the `react4j.check_invariants` setting to {@code true}.
   */
  public static void checkInvariants()
  {
    ReactConfig.setCheckInvariants( true );
  }

  /**
   * Set the `react4j.check_invariants` setting to {@code false}.
   */
  public static void noCheckInvariants()
  {
    ReactConfig.setCheckInvariants( false );
  }
}
