package react4j.webtesting;

import jsinterop.annotations.JsFunction;
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

    @JsMethod
    public static native BrowserPromise until(Condition condition, int timeout, String message);

    @JsMethod
    public static native BrowserPromise callback(Callback action);

    @JsMethod
    public static native Object object();

    @JsMethod
    public static native Object number(double value);

    @JsMethod
    public static native Object bool(boolean value);

    @JsMethod
    public static native Object noop();

    @JsMethod
    public static native @Nullable Object property(Object object, String name);

    @JsMethod
    public static native boolean same(@Nullable Object first, @Nullable Object second);

    @JsMethod
    public static native String computed(Element element, String property);

    @JsMethod
    public static native String inlineStyle(Element element, String property);

    @JsFunction
    public interface Condition {
        boolean test();
    }

    @JsFunction
    public interface Callback {
        void run(BrowserPromise.Action done);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Element")
    public static class Element {
        protected Element() {}

        @JsProperty
        public native String getTextContent();

        @JsProperty
        public native Element getFirstElementChild();

        public native @Nullable String getAttribute(String name);

        public native boolean hasAttribute(String name);

        public native void remove();
    }
}
