package react4j.webspeechdemo.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "HTMLSelectElement")
public final class HTMLSelectElement {
    @JsProperty(name = "value")
    public native String value();

    private HTMLSelectElement() {}
}
