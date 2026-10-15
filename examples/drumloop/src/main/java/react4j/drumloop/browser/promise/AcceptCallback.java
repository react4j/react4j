package react4j.drumloop.browser.promise;

import jsinterop.annotations.JsFunction;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
@JsFunction
public interface AcceptCallback<T extends @Nullable Object> {
    void onInvoke(T value);
}
