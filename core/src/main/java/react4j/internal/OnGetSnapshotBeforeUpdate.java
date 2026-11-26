package react4j.internal;

import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;

/**
 * Optional lifecycle interface for getSnapshotBeforeUpdate react lifecycle.
 */
public interface OnGetSnapshotBeforeUpdate {
    @Nullable
    Object getSnapshotBeforeUpdate(
            JsPropertyMap<@Nullable Object> prevProps, JsPropertyMap<@Nullable Object> prevState);
}
