package react4j.webtesting;

import static react4j.webtesting.Checks.check;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class Browser {
    private Browser() {}

    @JsMethod
    public static native Frame blankFrame(int width, int height);

    @JsMethod
    private static native BrowserPromise fetch(String url);

    @JsMethod
    private static native BrowserPromise write(Frame frame, String html);

    @JsMethod
    public static native void setValue(Element element, String value);

    @JsMethod
    public static native void mouse(Element element, String type, boolean shift);

    @JsMethod
    public static native void key(Element element, int code);

    @JsMethod
    public static native void blurTo(Element element, Element target);

    @JsMethod
    public static native Element[] queryAll(Object root, String selector);

    @JsMethod
    public static native String pseudoStyle(Element element, String pseudo, String property);

    @JsMethod
    public static native BrowserPromise reload(Frame frame);

    @JsMethod
    public static native String[] observeErrors(Frame frame);

    @JsMethod
    public static native String[] styleNames(Element element);

    @JsMethod
    public static native Element appendIn(Page page, String tag);

    @JsMethod
    public static native void cssText(Element element, String text);

    public static BrowserPromise loadApplication(Frame frame, String root, String setup) {
        return fetch(root + "index.html")
                .then(value -> {
                    Response response = Js.uncheckedCast(value);
                    check(response.isOk(), "Load application: " + root);
                    return response.text();
                })
                .then(html -> write(
                        frame,
                        Browser.string(html)
                                .replace(
                                        "<head>",
                                        "<head><base href=\"" + absoluteUrl(root) + "\"><script>" + setup
                                                + "</script>")));
    }

    @JsMethod
    public static native Element append(String tag);

    @JsMethod
    public static native Element appendHead(String tag);

    @JsMethod
    public static native Element query(Object root, String selector);

    @JsMethod
    public static native boolean exists(Object root, String selector);

    @JsMethod
    public static native Element byId(String id);

    @JsMethod
    public static native boolean hasFocus();

    @JsMethod
    public static native BrowserPromise fetchText(String url);

    @JsMethod
    public static native BrowserPromise fetchJson(String url);

    @JsMethod
    public static native Frame frame(String url);

    @JsMethod
    public static native BrowserPromise loaded(Frame frame);

    @JsMethod
    public static native String absoluteUrl(String path);

    @JsMethod
    public static native String[] resources(Page page);

    @JsMethod
    public static native BrowserPromise loadFonts(Page page, String font);

    @JsMethod
    public static native BrowserPromise fontsReady(Page page);

    @JsMethod
    public static native Animation[] animations(Element element);

    @JsMethod
    public static native Rule[] rules(Element style);

    @JsMethod
    public static native Rule[] childRules(Rule rule);

    @JsMethod
    public static native String string(@Nullable Object value);

    @JsMethod
    public static native boolean matchesRegex(String pattern, String value);

    @JsMethod
    public static native String @Nullable [] match(String pattern, String value);

    @JsMethod
    public static native BrowserPromise delay(int milliseconds);

    @JsMethod
    public static native int viewportWidth();

    @JsMethod
    public static native int viewportHeight();

    @JsMethod
    public static native void showPopover(Element element);

    @JsMethod
    public static native void hidePopover(Element element);

    @JsMethod
    public static native String json(@Nullable Object value);

    @JsMethod
    public static native BrowserPromise until(Condition condition, int timeout, int interval, String message);

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
        private Element() {}

        @JsProperty
        public native String getTextContent();

        @JsProperty
        public native void setTextContent(String value);

        @JsProperty
        public native String getId();

        @JsProperty
        public native String getClassName();

        @JsProperty
        public native void setClassName(String value);

        @JsProperty
        public native String getSrc();

        @JsProperty
        public native int getNaturalWidth();

        @JsProperty
        public native int getClientWidth();

        public native BrowserPromise decode();

        public native void focus();

        public native void blur();

        public native void click();

        public native boolean contains(Element element);

        @JsProperty
        public native Element getParentElement();

        @JsProperty
        public native Element getNextElementSibling();

        @JsProperty
        public native Element getPreviousElementSibling();

        @JsProperty
        public native boolean isComplete();

        @JsProperty
        public native boolean isDisabled();

        @JsProperty
        public native String getValue();

        @JsProperty
        public native String getPlaceholder();

        public native Rect getBoundingClientRect();

        public native boolean matches(String selector);

        @JsProperty
        public native Element getFirstElementChild();

        public native @Nullable String getAttribute(String name);

        public native boolean hasAttribute(String name);

        public native void setAttribute(String name, String value);

        public native void remove();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "HTMLIFrameElement")
    public static final class Frame extends Element {
        private Frame() {}

        @JsProperty
        public native void setWidth(String width);

        @JsProperty
        public native void setHeight(String height);

        @JsProperty
        public native Page getContentDocument();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Document")
    public static final class Page {
        private Page() {}

        @JsProperty
        public native Element getDocumentElement();

        @JsProperty
        public native Element getActiveElement();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "DOMRect")
    public static final class Rect {
        private Rect() {}

        @JsProperty
        public native double getWidth();

        @JsProperty
        public native double getLeft();

        @JsProperty
        public native double getRight();

        @JsProperty
        public native double getTop();

        @JsProperty
        public native double getBottom();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Response")
    private static final class Response {
        private Response() {}

        @JsProperty
        public native boolean isOk();

        public native BrowserPromise text();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Animation")
    public static final class Animation {
        private Animation() {}

        public native void pause();

        @JsProperty
        public native BrowserPromise getReady();

        @JsProperty
        public native void setCurrentTime(double value);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "CSSRule")
    public static final class Rule {
        private Rule() {}

        @JsProperty
        public native String getCssText();

        @JsProperty
        public native String getName();

        public native void deleteRule(int index);

        public native int insertRule(String text, int index);
    }
}
