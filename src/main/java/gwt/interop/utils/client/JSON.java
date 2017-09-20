package gwt.interop.utils.client;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class JSON {
    public static native String stringify(Object o);
    public static native <O> O parse(String json);
}
