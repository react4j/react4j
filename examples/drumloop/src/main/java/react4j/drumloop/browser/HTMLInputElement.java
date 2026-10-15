package react4j.drumloop.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "HTMLInputElement")
public final class HTMLInputElement {
    @JsProperty(name = "value")
    public native String value();

    private HTMLInputElement() {}
}
