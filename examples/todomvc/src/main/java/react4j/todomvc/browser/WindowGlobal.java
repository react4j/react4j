package react4j.todomvc.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public final class WindowGlobal {
    private WindowGlobal() {}

    @JsProperty(name = "document")
    public static native Document document();

    @JsProperty(name = "location")
    public static native Location location();

    @JsProperty(name = "history")
    public static native History history();

    public static native void addEventListener(String type, EventListener listener, boolean capture);

    public static native void removeEventListener(String type, EventListener listener, boolean capture);
}
