package react.core;

import org.jetbrains.annotations.TestOnly;

/**
 * Location of all compile time configuration settings for framework.
 */
public final class ReactConfig
{
  private static final Provider c_provider = createProvider();

  private ReactConfig()
  {
  }

  public static boolean enableComponentNames()
  {
    return c_provider.enableComponentNames();
  }

  @TestOnly
  static Provider getProvider()
  {
    return c_provider;
  }

  private static Provider createProvider()
  {
    final String environment = System.getProperty( "react.environment", "production" );
    if ( !"production".equals( environment ) && !"development".equals( environment ) )
    {
      final String message = "System property 'react.environment' is set to invalid property " + environment;
      throw new IllegalStateException( message );
    }
    final boolean development = environment.equals( "development" );
    final boolean enableNames =
      "true".equals( System.getProperty( "react.enable_component_names", development ? "true" : "false" ) );

    return System.getProperty( "react.dynamic_provider", "false" ).equals( "true" ) ?
           new DynamicProvider( enableNames ) :
           new StaticProvider( enableNames );
  }

  /**
   * Abstraction used to provide configuration settings for React system.
   * This abstraction is used to allow converting configuration to compile time
   * constants during GWT and/or closure compiler phases and thus allow elimination of
   * code during production variants of the runtime.
   */
  private interface Provider
  {
    boolean enableComponentNames();
  }

  /**
   * A provider implementation that allows changing of values at runtime.
   * Only really used during testing.
   */
  @TestOnly
  static final class DynamicProvider
    implements Provider
  {
    private boolean _enableComponentNames;

    DynamicProvider( final boolean enableComponentNames )
    {
      _enableComponentNames = enableComponentNames;
    }

    void setEnableComponentNames( final boolean enableComponentNames )
    {
      _enableComponentNames = enableComponentNames;
    }

    @Override
    public boolean enableComponentNames()
    {
      return _enableComponentNames;
    }
  }

  /**
   * The normal provider implementation for statically defining properties.
   * Properties do not change at runtime and can be used by GWT and closure compiler
   * to set values at compile time and eliminate dead/unused code.
   */
  private static final class StaticProvider
    implements Provider
  {
    private final boolean _enableComponentNames;

    StaticProvider( final boolean enableComponentNames )
    {
      _enableComponentNames = enableComponentNames;
    }

    @Override
    public boolean enableComponentNames()
    {
      return _enableComponentNames;
    }
  }
}
