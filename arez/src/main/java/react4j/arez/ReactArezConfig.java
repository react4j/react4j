package react4j.arez;

/**
 * Location of all compile time configuration settings for arez part of framework.
 */
final class ReactArezConfig
{
  private static final boolean STORE_AREZ_DATA_AS_STATE =
    "true".equals( System.getProperty( "react4j.arez.store_arez_data_as_state", "true" ) );

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
    return STORE_AREZ_DATA_AS_STATE;
  }
}
