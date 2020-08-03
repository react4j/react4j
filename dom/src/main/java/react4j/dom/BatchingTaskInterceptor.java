package react4j.dom;

import arez.SafeProcedure;
import arez.TaskInterceptor;
import javax.annotation.Nonnull;

/**
 * An arez TaskInterceptor that ensures that changes to reacts view are batched
 */
final class BatchingTaskInterceptor
  implements TaskInterceptor
{
  @Override
  public void executeTasks( @Nonnull final SafeProcedure executeAction )
  {
    ReactDOM.batchedUpdates( executeAction::call );
  }
}
