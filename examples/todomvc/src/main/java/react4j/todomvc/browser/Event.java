package react4j.todomvc.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Event")
public final class Event {
    private Event() {}

    public native void preventDefault();
}
