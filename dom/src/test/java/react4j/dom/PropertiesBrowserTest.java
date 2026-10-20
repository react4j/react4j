package react4j.dom;

import static react4j.webtesting.Checks.check;

import java.util.Arrays;
import java.util.Objects;
import jsinterop.annotations.JsMethod;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.webtesting.Browser;

@NullMarked
public final class PropertiesBrowserTest {
    private PropertiesBrowserTest() {}

    @JsMethod
    public static void run() {
        int count = 0;
        for (var overlay : overlays()) {
            int found = 0;
            for (var method : methods(overlay)) {
                if (!method.startsWith("m_") || !method.contains("__$devirt__")) {
                    continue;
                }
                var signature = method.split("__");
                var name = signature[0].substring(2);
                var types = Arrays.copyOfRange(signature, 3, signature.length - 1);
                var object = Browser.object();
                var args = new Object[types.length];
                for (int i = 0; i < types.length; i++) {
                    args[i] = sample(types[i]);
                }
                var property = name;
                Object expected = 0 == args.length ? Browser.bool(true) : args[0];
                switch (name) {
                    case "self" -> property = "";
                    case "className" -> expected = "first second";
                    case "dangerouslySetInnerHTML" -> expected = "sample";
                    case "key" -> expected = "int".equals(types[0]) ? "13" : "sample";
                    case "height", "width" -> {
                        if ("int".equals(types[0])) {
                            expected = "13";
                        }
                    }
                    case "from" -> property = "form";
                    case "preLoad" -> property = "preload";
                    case "placeHolder" -> property = "placeholder";
                    case "readonly" -> property = "readOnly";
                    case "cssFloat" -> property = "float";
                    case "webkitMask" -> property = "WebkitMask";
                    case "webkitMaskSize" -> property = "WebkitMaskSize";
                    case "data" -> {
                        args = new Object[] {"shared", "true"};
                        property = "data-shared";
                        expected = "true";
                    }
                    case "customProperty" -> {
                        args = new Object[] {"--accent", "#04c"};
                        property = "--accent";
                        expected = "#04c";
                    }
                    case "prop" -> {
                        args = new Object[] {"data-extra", Browser.number(17)};
                        property = "data-extra";
                        expected = args[1];
                    }
                    default -> {}
                }
                check(Browser.same(object, invoke(overlay, method, object, args)), "Fluent identity: " + method);
                if (!property.isEmpty()) {
                    var actual = Browser.property(object, property);
                    if ("dangerouslySetInnerHTML".equals(name)) {
                        actual = Browser.property(Objects.requireNonNull(actual, "HTML property: " + method), "__html");
                    }
                    check(Browser.same(expected, actual), "Native property " + property + ": " + method);
                }
                if (1 == types.length && "boolean".equals(types[0])) {
                    invoke(overlay, method, object, new Object[] {Browser.bool(false)});
                    check(
                            Browser.same(Browser.bool(false), Browser.property(object, property)),
                            "False update: " + method);
                }
                found++;
            }
            check(0 < found, "No fluent API methods discovered");
            count += found;
        }
        check(500 < count, "Incomplete property API exercise: " + count);
    }

    private static Object sample(String type) {
        if ("java_lang_String".equals(type)) {
            return "sample";
        }
        if ("boolean".equals(type)) {
            return Browser.bool(true);
        }
        if ("int".equals(type) || "double".equals(type) || "float".equals(type)) {
            return Browser.number(13);
        }
        if (type.startsWith("arrayOf_")) {
            return new String[] {"first", "second"};
        }
        if (type.endsWith("Handler") || type.endsWith("RefConsumer")) {
            return Browser.noop();
        }
        if ("jsinterop_base_Any".equals(type)) {
            return Browser.number(17);
        }
        if ("react4j_ReactNode".equals(type)) {
            return "child";
        }
        if ("react4j_dom_proptypes_html_CssProps".equals(type)) {
            return Browser.object();
        }
        throw new AssertionError("Unhandled fluent parameter " + type);
    }

    @JsMethod
    private static native Object[] overlays();

    @JsMethod
    private static native String[] methods(Object overlay);

    @JsMethod
    private static native @Nullable Object invoke(Object overlay, String method, Object value, Object[] args);
}
