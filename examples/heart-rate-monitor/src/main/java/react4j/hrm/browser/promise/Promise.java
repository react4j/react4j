package react4j.hrm.browser.promise;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Promise")
public final class Promise<T extends @Nullable Object> {
    private Promise() {}

    public native <V extends @Nullable Object> Promise<V> then(OnFulfilledCallback<? super T, ? extends V> onFulfilled);

    @JsMethod(name = "then")
    public native Promise<@Nullable Void> thenAccept(AcceptCallback<? super T> onFulfilled);

    @JsMethod(name = "catch")
    public native Promise<@Nullable Object> catch_(OnRejectedCallback onRejected);
}
