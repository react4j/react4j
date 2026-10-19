package react4j.webtesting;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class Browser {
    private Browser() {}

    @JsMethod
    public static native Element append(String tag);

    @JsMethod
    public static native BrowserPromise delay(int milliseconds);

    @JsMethod
    public static native String json(@Nullable Object value);

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Element")
    public static class Element {
        protected Element() {}

        @JsProperty
        public native String getTextContent();

        public native void remove();
    }
}
