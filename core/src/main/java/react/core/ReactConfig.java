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

  /**
   * Return true if components should have human readable names specified.
   * Useful if you want to interact via DevTools or other tool chains.
   *
   * @return to enable human readable names for components.
   */
  public static boolean enableComponentNames()
  {
    return c_provider.enableComponentNames();
  }

  /**
   * Return true if we should check that the user interacts with React component in a way compatible with the state.
   * i.e. setInitialState should only be invoked at the start, setState should not be invoked while componentWillUpdate
   * is running etc.
   *
   * @return true to enable invariant checking about how we interact with native react component.
   */
  public static boolean checkComponentStateInvariants()
  {
    return c_provider.checkComponentStateInvariants();
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
    final boolean checkComponentStateInvariants =
      "true".equals( System.getProperty( "react.check_component_state_invariants", development ? "true" : "false" ) );

    return System.getProperty( "react.dynamic_provider", "false" ).equals( "true" ) ?
           new DynamicProvider( enableNames, checkComponentStateInvariants ) :
           new StaticProvider( enableNames, checkComponentStateInvariants );
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

    boolean checkComponentStateInvariants();
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
    private boolean _checkComponentStateInvariants;

    DynamicProvider( final boolean enableComponentNames, final boolean checkComponentStateInvariants )
    {
      _enableComponentNames = enableComponentNames;
      _checkComponentStateInvariants = checkComponentStateInvariants;
    }

    void setEnableComponentNames( final boolean enableComponentNames )
    {
      _enableComponentNames = enableComponentNames;
    }

    void setCheckComponentStateInvariants( final boolean checkComponentStateInvariants )
    {
      _checkComponentStateInvariants = checkComponentStateInvariants;
    }

    @Override
    public boolean enableComponentNames()
    {
      return _enableComponentNames;
    }

    @Override
    public boolean checkComponentStateInvariants()
    {
      return _checkComponentStateInvariants;
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
    private final boolean _checkComponentStateInvariants;

    StaticProvider( final boolean enableComponentNames, final boolean checkComponentStateInvariants )
    {
      _enableComponentNames = enableComponentNames;
      _checkComponentStateInvariants = checkComponentStateInvariants;
    }

    @Override
    public boolean enableComponentNames()
    {
      return _enableComponentNames;
    }

    @Override
    public boolean checkComponentStateInvariants()
    {
      return _checkComponentStateInvariants;
    }
  }
}
