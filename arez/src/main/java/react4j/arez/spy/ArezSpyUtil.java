package react4j.arez.spy;

import org.realityforge.arez.Arez;
import org.realityforge.arez.ArezContext;

/**
 * Utility class for interacting with spy capabilities.
 */
public final class ArezSpyUtil
{
  private static final ReactArezConsoleSpyEventProcessor PROCESSOR =
    Arez.context().areSpiesEnabled() ? new ReactArezConsoleSpyEventProcessor() : null;
  private static boolean c_loggingEnabled;

  /**
   * Return true if spy event logging is enabled.
   */
  public static boolean isSpyEventLoggingEnabled()
  {
    return c_loggingEnabled;
  }

  /**
   * Enable console logging of all spy events.
   * This is a noop if spies are not enabled or logging has already been enabled.
   */
  public static void enableSpyEventLogging()
  {
    final ArezContext context = Arez.context();
    if ( context.areSpiesEnabled() && !isSpyEventLoggingEnabled() )
    {
      context.getSpy().addSpyEventHandler( PROCESSOR );
      c_loggingEnabled = true;
    }
  }

  /**
   * Disable console logging of all spy events.
   * This is a noop if spies are not enabled or logging is not enabled.
   */
  public static void disableSpyEventLogging()
  {
    final ArezContext context = Arez.context();
    if ( context.areSpiesEnabled() && isSpyEventLoggingEnabled() )
    {
      context.getSpy().removeSpyEventHandler( PROCESSOR );
      c_loggingEnabled = false;
    }
  }

  private ArezSpyUtil()
  {
  }
}
