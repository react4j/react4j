package react4j.webspeechdemo.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "HTMLTextAreaElement")
public final class HTMLTextAreaElement {
    @JsProperty(name = "value")
    public native String value();

    private HTMLTextAreaElement() {}
}
