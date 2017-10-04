package react4j.arez;

/**
 * Location of all compile time configuration settings for arez part of framework.
 */
final class ReactArezConfig
{
  private ReactArezConfig()
  {
  }

  /**
   * Return true if arez should store dependencies on state of component.
   * Useful if you want to observe the dependencies via DevTools or other tool chains.
   * It is somewhat resource intensive so should not be enabled in production.
   *
   * @return true if arez should store dependencies on state of component.
   */
  static boolean shouldStoreArezDataAsState()
  {
    return isDevelopmentMode() &&
           "true".equals( System.getProperty( "react4j.arez.store_arez_data_as_state", "true" ) );
  }

  /**
   * Return true if react-arez is compiled in development mode.
   *
   * @return true if react-arez is compiled in development mode.
   */
  private static boolean isDevelopmentMode()
  {
    final String environment =
      System.getProperty( "react4j.arez.environment", isReactDevelopmentEnvironment() ? "development" : "production" );
    if ( !"production".equals( environment ) && !"development".equals( environment ) )
    {
      final String message = "System property 'react4j.arez.environment' is set to invalid value " + environment;
      throw new IllegalStateException( message );
    }
    return environment.equals( "development" );
  }

  /**
   * Return true if react is compiled in development mode.
   *
   * @return true if react is compiled in development mode.
   */
  private static boolean isReactDevelopmentEnvironment()
  {
    final String environment = System.getProperty( "react4j.environment", "production" );
    if ( !"production".equals( environment ) && !"development".equals( environment ) )
    {
      final String message = "System property 'react4j.environment' is set to invalid value " + environment;
      throw new IllegalStateException( message );
    }
    return environment.equals( "development" );
  }
}
