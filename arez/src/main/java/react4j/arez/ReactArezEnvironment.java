package react4j.arez;

import arez.Arez;
import arez.ArezContext;
import arez.Environment;
import arez.Function;
import arez.SafeFunction;
import javax.annotation.Nonnull;
import react4j.dom.ReactDOM;

/**
 * Utility class for configuring the Arez {@link Environment} suitable for React.
 * The only difference is that all changes that occur as a result of Arez reactions are
 * batched to minimize the number of changes propagated to react.
 */
public final class ReactArezEnvironment
{
  /**
   * The underlying instance of the environment.
   */
  private static final Environment INSTANCE = new ReactEnvironment();

  private ReactArezEnvironment()
  {
  }

  /**
   * Install the appropriate {@link Environment} into the current ArezContext.
   */
  public static void install()
  {
    install( Arez.context() );
  }

  /**
   * Install the appropriate {@link Environment} into the specified ArezContext.
   *
   * @param context the arez context to configure environment.
   */
  public static void install( @Nonnull final ArezContext context )
  {
    context.setEnvironment( INSTANCE );
  }

  @SuppressWarnings( "unchecked" )
  private static final class ReactEnvironment
    implements Environment
  {
    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T run( @Nonnull final SafeFunction<T> function )
    {
      final T[] result = (T[]) new Object[ 1 ];
      ReactDOM.batchedUpdates( () -> result[ 0 ] = function.call() );
      return result[ 0 ];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T run( @Nonnull final Function<T> function )
      throws Throwable
    {
      final T[] result = (T[]) new Object[ 1 ];
      ReactDOM.batchedUpdates( () -> result[ 0 ] = function.call() );
      return result[ 0 ];
    }
  }
}
