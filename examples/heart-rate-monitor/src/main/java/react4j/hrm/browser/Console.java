package react4j.hrm.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "console")
public final class Console {
    private Console() {}

    public static native void log(String message);
}
