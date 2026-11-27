package react4j.internal;

import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;

/**
 * Optional lifecycle interface for componentDidUpdate react lifecycle.
 */
public interface OnComponentDidUpdate {
    void componentDidUpdate(JsPropertyMap<@Nullable Object> prevProps);
}
