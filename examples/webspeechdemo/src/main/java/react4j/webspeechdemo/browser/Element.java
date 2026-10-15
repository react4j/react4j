package react4j.webspeechdemo.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Element")
public final class Element {
    private Element() {}

    @JsProperty(name = "firstChild")
    @Nullable
    public native Node firstChild();

    @JsProperty(name = "clientHeight")
    public native int clientHeight();

    @JsProperty(name = "clientWidth")
    public native int clientWidth();

    @JsProperty(name = "scrollHeight")
    public native int scrollHeight();

    @JsProperty(name = "scrollWidth")
    public native int scrollWidth();
}
