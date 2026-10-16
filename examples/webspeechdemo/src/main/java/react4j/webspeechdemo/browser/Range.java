package react4j.webspeechdemo.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Range")
public final class Range {
    private Range() {}

    public native void setStart(Node node, int offset);

    public native void setEnd(Node node, int offset);

    public native DOMRect getBoundingClientRect();
}
