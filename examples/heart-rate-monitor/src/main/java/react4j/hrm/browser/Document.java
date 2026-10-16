package react4j.hrm.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Document")
public final class Document {
    private Document() {}

    @Nullable
    public native Element getElementById(String elementId);
}
