package react4j.dom;

import arez.SafeProcedure;
import arez.TaskInterceptor;

/**
 * An arez TaskInterceptor that ensures that changes to reacts view are batched
 */
final class BatchingTaskInterceptor implements TaskInterceptor {
    @Override
    public void executeTasks(final SafeProcedure executeAction) {
        ReactDOM.batchedUpdates(executeAction::call);
    }
}
