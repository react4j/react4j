package react4j.hrm.browser.promise;

import jsinterop.annotations.JsFunction;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
@JsFunction
public interface OnFulfilledCallback<T extends @Nullable Object, V extends @Nullable Object> {
    Promise<V> onFulfilled(T value);
}
