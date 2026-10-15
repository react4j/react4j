package react4j;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ReactErrorInfo {
    @SuppressWarnings({"NullAway.Init", "unused"})
    public String componentStack;
}
