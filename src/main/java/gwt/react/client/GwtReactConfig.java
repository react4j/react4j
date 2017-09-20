package gwt.react.client;

import org.jetbrains.annotations.TestOnly;

/**
 * Location of all compile time configuration settings for framework.
 */
public final class GwtReactConfig
{
  private static final Provider c_provider = createProvider();

  private GwtReactConfig()
  {
  }

  public static boolean verboseErrorMessages()
  {
    return c_provider.verboseErrorMessages();
  }

  public static boolean checkInvariants()
  {
    return c_provider.checkInvariants();
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
    final boolean verboseErrorMessages =
      "true".equals( System.getProperty( "react.verbose_error_messages", development ? "true" : "false" ) );
    final boolean checkInvariants =
      "true".equals( System.getProperty( "react.check_invariants", development ? "true" : "false" ) );
    final boolean enableNames =
      "true".equals( System.getProperty( "react.enable_component_names", development ? "true" : "false" ) );

    return System.getProperty( "react.dynamic_provider", "false" ).equals( "true" ) ?
           new DynamicProvider( verboseErrorMessages, checkInvariants, enableNames ) :
           new StaticProvider( verboseErrorMessages, checkInvariants, enableNames );
  }

  /**
   * Abstraction used to provide configuration settings for React system.
   * This abstraction is used to allow converting configuration to compile time
   * constants during GWT and/or closure compiler phases and thus allow elimination of
   * code during production variants of the runtime.
   */
  private interface Provider
  {
    boolean verboseErrorMessages();

    boolean checkInvariants();

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
    private boolean _verboseErrorMessages;
    private boolean _checkInvariants;
    private boolean enableComponentNames;

    DynamicProvider( final boolean verboseErrorMessages,
                     final boolean checkInvariants,
                     final boolean enableComponentNames )
    {
      _verboseErrorMessages = verboseErrorMessages;
      _checkInvariants = checkInvariants;
      this.enableComponentNames = enableComponentNames;
    }

    void setVerboseErrorMessages( final boolean verboseErrorMessages )
    {
      _verboseErrorMessages = verboseErrorMessages;
    }

    void setCheckInvariants( final boolean checkInvariants )
    {
      _checkInvariants = checkInvariants;
    }

    void setEnableComponentNames( final boolean enableComponentNames )
    {
      this.enableComponentNames = enableComponentNames;
    }

    @Override
    public boolean verboseErrorMessages()
    {
      return _verboseErrorMessages;
    }

    @Override
    public boolean checkInvariants()
    {
      return _checkInvariants;
    }

    @Override
    public boolean enableComponentNames()
    {
      return enableComponentNames;
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
    private final boolean _verboseErrorMessages;
    private final boolean _checkInvariants;
    private final boolean _enableComponentNames;

    StaticProvider( final boolean verboseErrorMessages,
                    final boolean checkInvariants,
                    final boolean enableComponentNames )
    {
      _verboseErrorMessages = verboseErrorMessages;
      _checkInvariants = checkInvariants;
      _enableComponentNames = enableComponentNames;
    }

    @Override
    public boolean verboseErrorMessages()
    {
      return _verboseErrorMessages;
    }

    @Override
    public boolean checkInvariants()
    {
      return _checkInvariants;
    }

    @Override
    public boolean enableComponentNames()
    {
      return _enableComponentNames;
    }
  }
}
