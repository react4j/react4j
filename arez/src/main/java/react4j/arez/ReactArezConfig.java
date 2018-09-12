package react4j.arez;

import arez.Arez;

/**
 * Location of all compile time configuration settings for arez part of framework.
 */
final class ReactArezConfig
{
  private static final ConfigProvider PROVIDER = new ConfigProvider();
  private static final boolean STORE_AREZ_DATA_AS_STATE = PROVIDER.shouldStoreArezDataAsState();

  private ReactArezConfig()
  {
  }

  /**
   * Return true if arez should store dependencies on state of component.
   * Useful if you want to observe the dependencies via DevTools or other tool chains.
   * It is somewhat resource intensive so should not be enabled in production.
   * It requires arez spies to be enabled and thus will return false if {@link Arez#areSpiesEnabled()} returns false.
   *
   * @return true if arez should store dependencies on state of component.
   */
  static boolean shouldStoreArezDataAsState()
  {
    return STORE_AREZ_DATA_AS_STATE && Arez.areSpiesEnabled();
  }

  private static final class ConfigProvider
    extends AbstractConfigProvider
  {
    @GwtIncompatible
    @Override
    boolean shouldStoreArezDataAsState()
    {
      return System.getProperty( "react4j.arez.store_arez_data_as_state", "true" ).equals( "true" );
    }
  }

  @SuppressWarnings( "unused" )
  private static abstract class AbstractConfigProvider
  {
    boolean shouldStoreArezDataAsState()
    {
      return "true" == System.getProperty( "react4j.arez.store_arez_data_as_state" );
    }
  }
}
