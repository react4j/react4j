package react4j.hrm.browser.core;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Event")
public final class Event {
    private Event() {}

    @JsProperty(name = "target")
    @Nullable
    public native Object target();
}
