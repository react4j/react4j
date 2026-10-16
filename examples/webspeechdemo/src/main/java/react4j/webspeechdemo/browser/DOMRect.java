package react4j.webspeechdemo.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "DOMRect")
public final class DOMRect {
    public double width;
    public double height;

    private DOMRect() {}

    @JsProperty(name = "top")
    public native double top();

    @JsProperty(name = "left")
    public native double left();
}
