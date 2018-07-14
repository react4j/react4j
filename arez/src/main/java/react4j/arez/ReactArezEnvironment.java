package react4j.arez;

import arez.Arez;
import arez.ArezContext;
import arez.ReactionEnvironment;
import arez.SafeProcedure;
import javax.annotation.Nonnull;
import react4j.dom.ReactDOM;

/**
 * Utility class for configuring the Arez {@link ReactionEnvironment} suitable for React.
 * The only difference is that all changes that occur as a result of Arez reactions are
 * batched to minimize the number of changes propagated to react.
 */
public final class ReactArezEnvironment
{
  /**
   * The underlying instance of the environment.
   */
  private static final ReactionEnvironment INSTANCE = new ReactReactionEnvironment();

  private ReactArezEnvironment()
  {
  }

  /**
   * Install the appropriate {@link ReactionEnvironment} into the current ArezContext.
   */
  public static void install()
  {
    install( Arez.context() );
  }

  /**
   * Install the appropriate {@link ReactionEnvironment} into the specified ArezContext.
   *
   * @param context the arez context to configure environment.
   */
  public static void install( @Nonnull final ArezContext context )
  {
    context.setEnvironment( INSTANCE );
  }

  private static final class ReactReactionEnvironment
    implements ReactionEnvironment
  {
    /**
     * {@inheritDoc}
     */
    @Override
    public void run( @Nonnull final SafeProcedure action )
    {
      ReactDOM.batchedUpdates( action::call );
    }
  }
}
