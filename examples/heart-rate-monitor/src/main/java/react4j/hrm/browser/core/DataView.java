package react4j.hrm.browser.core;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "DataView")
public final class DataView {
    private DataView() {}

    public native byte getInt8(int byteOffset);
}
