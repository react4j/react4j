package react4j.hrm.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public final class WindowGlobal {
    private WindowGlobal() {}

    @JsProperty(name = "document")
    public static native Document document();

    @JsProperty(name = "navigator")
    public static native Navigator navigator();
}
