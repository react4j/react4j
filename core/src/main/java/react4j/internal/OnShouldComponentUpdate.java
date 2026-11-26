package react4j.internal;

import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;

/**
 * Optional lifecycle interface for shouldComponentUpdate react lifecycle.
 */
public interface OnShouldComponentUpdate {
    boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> nextProps);
}
