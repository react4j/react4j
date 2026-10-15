package react4j.internal;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;

/**
 * Native support infrastructure for shouldComponentUpdate react lifecycle.
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "?")
public interface OnShouldComponentUpdate {
    boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> nextProps);
}
