package react4j.todomvc.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "History")
public final class History {
    private History() {}

    public native void pushState(@Nullable Object data, String unused, String url);
}
