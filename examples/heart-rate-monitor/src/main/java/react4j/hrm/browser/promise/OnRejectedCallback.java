package react4j.hrm.browser.promise;

import jsinterop.annotations.JsFunction;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
@JsFunction
public interface OnRejectedCallback {
    @Nullable
    Object onRejected(@Nullable Object reason);
}
