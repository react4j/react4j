package react4j.internal;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;

/**
 * Native support infrastructure for getSnapshotBeforeUpdate react lifecycle.
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "?")
public interface OnGetSnapshotBeforeUpdate {
    @Nullable
    Object getSnapshotBeforeUpdate(
            JsPropertyMap<@Nullable Object> prevProps, JsPropertyMap<@Nullable Object> prevState);
}
