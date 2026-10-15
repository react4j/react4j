package react4j.internal;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.JsError;
import react4j.ReactErrorInfo;

/**
 * Native support infrastructure for componentDidCatch react lifecycle.
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "?")
public interface OnComponentDidCatch {
    void componentDidCatch(JsError error, ReactErrorInfo info);
}
