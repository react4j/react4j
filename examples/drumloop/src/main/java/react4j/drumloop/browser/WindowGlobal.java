package react4j.drumloop.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import react4j.drumloop.browser.promise.Promise;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public final class WindowGlobal {
    private WindowGlobal() {}

    @JsProperty(name = "document")
    public static native Document document();

    public static native Promise<Response> fetch(String input);
}
