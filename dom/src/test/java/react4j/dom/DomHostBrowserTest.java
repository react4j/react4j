package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.Renderable;
import react4j.dom.events.ReactEventHandler;
import react4j.internal.NativeView;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;

@NullMarked
public final class DomHostBrowserTest {
    private static final String SVG = "http://www.w3.org/2000/svg";
    private static final String HTML = "http://www.w3.org/1999/xhtml";
    private static final String XLINK = "http://www.w3.org/1999/xlink";
    private static final String IMAGE = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==";

    private DomHostBrowserTest() {}

    @JsMethod
    public static void styles(boolean production) {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(style("div", JsPropertyMap.of("backgroundColor", "#000", "display", "none")), root);
            final var div = Browser.query(root, "div");
            check(div.hasAttribute("style"), "Styles create a style attribute");
            final var setup = JsPropertyMap.<@Nullable Object>of("display", "block", "left", "1px", "top", 2);
            setup.set("fontFamily", "Arial");
            ReactDOM.render(style("div", setup), root);
            styleEquals(div, "display", "block", "left", "1px", "top", "2px", "font-family", "Arial");
            final var reset = JsPropertyMap.<@Nullable Object>of("display", "", "left", null, "top", false);
            reset.set("fontFamily", true);
            ReactDOM.render(style("div", reset), root);
            styleEquals(div, "display", "", "left", "", "top", "", "font-family", "");

            final var proxy = styleProxy();
            ReactDOM.render(style("div", proxy), root);
            for (final var change :
                    new Object[][] {{"display", "block"}, {"fontFamily", "Helvetica"}, {"lineHeight", 0.5}}) {
                proxy.set((String) change[0], change[1]);
                ReactDOM.render(style("div", proxy), root);
                styleEquals(div, "display", "none", "font-family", "Arial", "line-height", "1.2");
            }
            ReactDOM.render(style("div", Js.undefined()), root);
            styleEquals(div, "display", "", "font-family", "", "line-height", "");
            final var frozen = JsPropertyMap.of("border", "1px solid black");
            ReactDOM.render(style("div", frozen), root);
            check(production == setStyleProperty(frozen), "Development freezes the style object");

            ReactDOM.render(style("div", null), root);
            for (int run = 0; run < 2; run++) {
                ReactDOM.render(style("div", JsPropertyMap.of("display", "block")), root);
                styleEquals(div, "display", "block");
                ReactDOM.render(style("div", null), root);
                styleEquals(div, "display", "");
            }
            ReactDOM.render(style("div", JsPropertyMap.of("display", "none", "color", "red")), root);
            ReactDOM.render(style("div", JsPropertyMap.of("color", "green")), root);
            styleEquals(div, "display", "", "color", "green");
            ReactDOM.render(style("div", JsPropertyMap.of("display", "none", "color", "red")), root);
            ReactDOM.render(host("div"), root);
            styleEquals(div, "display", "", "color", "");
            ReactDOM.render(style("div", JsPropertyMap.of("color", "red")), root);
            styleEquals(div, "color", "red");
            expectWarnings(production, () -> ReactDOM.render(style("div", JsPropertyMap.of("margin", "0")), root));
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void cleanStyles(boolean production) {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(style("div", JsPropertyMap.of("color", "red", "width", "30px")), root);
            final var div = Browser.query(root, "div");
            styleEquals(div, "color", "red", "width", "30px");
            final var lengths = JsPropertyMap.<@Nullable Object>of("left", 0, "margin", 16, "opacity", 0.5);
            lengths.set("padding", "4px");
            ReactDOM.render(style("div", lengths), root);
            styleEquals(div, "left", "0px", "margin", "16px", "opacity", "0.5", "padding", "4px");
            expectWarnings(production, () -> ReactDOM.render(style("div", JsPropertyMap.of("--foo", 5)), root));
            styleEquals(div, "--foo", "5");
            expectWarnings(
                    production,
                    () -> ReactDOM.render(
                            style("div", JsPropertyMap.of("--foo-primary", "red", "backgroundColor", "red")), root));
            styleEquals(div, "--foo-primary", "red", "background-color", "red");
            for (final var unset : new @Nullable Object[] {Js.undefined(), null}) {
                ReactDOM.render(style("div", JsPropertyMap.of("color", unset, "width", "30px")), root);
                styleEquals(div, "color", "", "width", "30px");
            }
            ReactDOM.unmountComponentAtNode(root);
            ReactDOM.render(style("div", JsPropertyMap.of("color", null, "width", null)), root);
            final var fresh = Browser.query(root, "div");
            styleEquals(fresh, "color", "", "width", "");
            attribute(fresh, "style", false);

            final var prefixed = JsPropertyMap.<@Nullable Object>of();
            for (final var name : new String[] {
                "lineClamp",
                "WebkitLineClamp",
                "MozFlexGrow",
                "msFlexGrow",
                "msGridRow",
                "msGridRowEnd",
                "msGridRowSpan",
                "msGridRowStart",
                "msGridColumn",
                "msGridColumnEnd",
                "msGridColumnSpan",
                "msGridColumnStart"
            }) {
                prefixed.set(name, 10);
            }
            final var writes = styleWrites(fresh, () -> ReactDOM.render(style("div", prefixed), root));
            check(12 == writes.length, "All prefixed CSS values reach the DOM style setter");
            for (final var write : writes) {
                check(write.endsWith("=10"), "Numeric style must not gain units: " + write);
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void styleWarnings(boolean production) {
        final var root = Browser.append("div");
        try {
            final ViewConstructorFunction constructor = StyleComponent::new;
            Objects.requireNonNull(Js.asPropertyMap(constructor)).set("displayName", "Comp");
            expectWarnings(
                    production,
                    () -> ReactDOM.render(
                            ReactElement.createViewElement(constructor)
                                    .input("css", JsPropertyMap.of("background-color", "crimson")),
                            root),
                    "Unsupported style property background-color. Did you mean backgroundColor?");
            ReactDOM.unmountComponentAtNode(root);
            ReactDOM.render(ReactElement.createViewElement(constructor), root);
            final var transform = "translate3d(0, 0, 0)";
            expectWarnings(
                    production,
                    () -> ReactDOM.render(
                            ReactElement.createViewElement(constructor)
                                    .input(
                                            "css",
                                            JsPropertyMap.of(
                                                    "-ms-transform", transform, "-webkit-transform", transform)),
                            root),
                    "Unsupported style property -ms-transform. Did you mean msTransform?",
                    "Unsupported style property -webkit-transform. Did you mean WebkitTransform?");
            expectWarnings(
                    production,
                    () -> ReactDOM.render(
                            ReactElement.createViewElement(constructor)
                                    .input(
                                            "css",
                                            JsPropertyMap.of(
                                                    "msTransform",
                                                    transform,
                                                    "oTransform",
                                                    transform,
                                                    "webkitTransform",
                                                    transform)),
                            root),
                    "Unsupported vendor-prefixed style property oTransform. Did you mean OTransform?",
                    "Unsupported vendor-prefixed style property webkitTransform. Did you mean WebkitTransform?");
            final var semicolons = JsPropertyMap.<@Nullable Object>of(
                    "fontFamily", "Helvetica, arial", "backgroundImage", "url(foo;bar)", "backgroundColor", "blue;");
            semicolons.set("color", "red;   ");
            expectWarnings(
                    production,
                    () -> ReactDOM.render(
                            ReactElement.createViewElement(constructor).input("css", semicolons), root),
                    "Try \"backgroundColor: blue\" instead.",
                    "Try \"color: red\" instead.");
            expectWarnings(
                    production,
                    () -> ReactDOM.render(
                            ReactElement.createViewElement(constructor)
                                    .input("css", JsPropertyMap.of("fontSize", Browser.number(Double.NaN))),
                            root),
                    "`NaN` is an invalid value for the `fontSize` css style property.");
            expectWarnings(
                    production,
                    () -> ReactDOM.render(
                            ReactElement.createViewElement(constructor)
                                    .input(
                                            "css",
                                            JsPropertyMap.of("fontSize", Browser.number(Double.POSITIVE_INFINITY))),
                            root),
                    "`Infinity` is an invalid value for the `fontSize` css style property.");
        } finally {
            close(root);
        }
    }

    private static final class StyleComponent extends NativeView {
        @JsConstructor
        private StyleComponent(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return style("div", requireInputs().get("css"));
        }
    }

    private static ReactElement style(String tag, @Nullable Object style) {
        return host(tag).input("style", style);
    }

    private static void styleEquals(Browser.Element element, String... values) {
        for (int index = 0; index < values.length; index += 2) {
            equal(values[index + 1], styleProperty(element, values[index]));
        }
    }

    @JsMethod
    private static native JsPropertyMap<@Nullable Object> styleProxy();

    @JsMethod
    private static native boolean setStyleProperty(Object style);

    @JsMethod
    private static native String styleProperty(Browser.Element element, String name);

    @JsMethod
    private static native String[] styleWrites(Browser.Element element, Action action);

    @JsMethod
    public static void cleanAttributeMounts(boolean production) {
        final var root = Browser.append("div");
        final ReactEventHandler attributeFunction = event -> {};
        try {
            for (final var vector : new AttributeCase[] {
                new AttributeCase(
                        host("div").input("width", Browser.number(30)), element -> attribute(element, "width", "30")),
                new AttributeCase(host("div").input("width", "30"), element -> attribute(element, "width", "30")),
                new AttributeCase(true, host("a").input("href", true), element -> attribute(element, "href", false)),
                new AttributeCase(true, host("a").input("href", false), element -> attribute(element, "href", false)),
                new AttributeCase(host("div").input("width", null), element -> attribute(element, "width", false)),
                new AttributeCase(
                        true,
                        host("div").input("width", attributeFunction),
                        element -> attribute(element, "width", false)),
                new AttributeCase(
                        true, host("div").input("width", symbol("foo")), element -> attribute(element, "width", false)),
                new AttributeCase(host("div").input("hidden", true), element -> attribute(element, "hidden", "")),
                new AttributeCase(host("div").input("hidden", false), element -> attribute(element, "hidden", null)),
                new AttributeCase(host("div").input("hidden", "hidden"), element -> attribute(element, "hidden", "")),
                new AttributeCase(host("div").input("hidden", ""), element -> attribute(element, "hidden", null)),
                new AttributeCase(host("div").input("hidden", "foo"), element -> attribute(element, "hidden", "")),
                new AttributeCase(
                        host("div").input("hidden", new Object[] {"foo", "bar"}),
                        element -> attribute(element, "hidden", "")),
                new AttributeCase(
                        host("div").input("hidden", JsPropertyMap.of("foo", "bar")),
                        element -> attribute(element, "hidden", "")),
                new AttributeCase(
                        host("div").input("hidden", Browser.number(10)), element -> attribute(element, "hidden", "")),
                new AttributeCase(
                        host("div").input("hidden", Browser.number(0)), element -> attribute(element, "hidden", null)),
                new AttributeCase(host("div").input("hidden", null), element -> attribute(element, "hidden", false)),
                new AttributeCase(
                        true,
                        host("div").input("hidden", attributeFunction),
                        element -> attribute(element, "hidden", false)),
                new AttributeCase(
                        true,
                        host("div").input("hidden", symbol("foo")),
                        element -> attribute(element, "hidden", false)),
                new AttributeCase(host("a").input("download", true), element -> attribute(element, "download", "")),
                new AttributeCase(host("a").input("download", false), element -> attribute(element, "download", null)),
                new AttributeCase(
                        host("a").input("download", "myfile"), element -> attribute(element, "download", "myfile")),
                new AttributeCase(
                        host("a").input("download", "false"), element -> attribute(element, "download", "false")),
                new AttributeCase(
                        host("a").input("download", "true"), element -> attribute(element, "download", "true")),
                new AttributeCase(
                        host("a").input("download", Browser.number(0)), element -> attribute(element, "download", "0")),
                new AttributeCase(
                        host("div").input("download", null), element -> attribute(element, "download", false)),
                new AttributeCase(
                        host("div").input("download", Js.undefined()),
                        element -> attribute(element, "download", false)),
                new AttributeCase(
                        true,
                        host("div").input("download", attributeFunction),
                        element -> attribute(element, "download", false)),
                new AttributeCase(
                        true,
                        host("div").input("download", symbol("foo")),
                        element -> attribute(element, "download", false)),
                new AttributeCase(
                        host("div").input("className", "myClassName"),
                        element -> attribute(element, "class", "myClassName")),
                new AttributeCase(host("div").input("className", ""), element -> attribute(element, "class", "")),
                new AttributeCase(
                        true, host("div").input("className", true), element -> attribute(element, "class", false)),
                new AttributeCase(
                        true, host("div").input("className", false), element -> attribute(element, "class", false)),
                new AttributeCase(
                        host("div").input("className", null), element -> attribute(element, "className", false)),
                new AttributeCase(true, host("div").input("classname", "test"), element -> {
                    attribute(element, "class", false);
                    attribute(element, "classname", true);
                }),
                new AttributeCase(
                        true, host("div").input("class", "test"), element -> property(element, "className", "test")),
                new AttributeCase(
                        true, host("div").input("cLASs", "test"), element -> property(element, "className", "test")),
                new AttributeCase(host("div").input("htmlFor", "myFor"), element -> attribute(element, "for", "myFor")),
                new AttributeCase(true, host("div").input("htmlfor", "myFor"), element -> {
                    attribute(element, "for", false);
                    attribute(element, "htmlfor", "myFor");
                }),
                new AttributeCase(host("div").input("htmlFor", ""), element -> attribute(element, "for", "")),
                new AttributeCase(
                        true, host("div").input("htmlFor", true), element -> attribute(element, "for", false)),
                new AttributeCase(
                        true, host("div").input("htmlFor", false), element -> attribute(element, "for", false)),
                new AttributeCase(host("div").input("htmlFor", null), element -> attribute(element, "htmlFor", false)),
                new AttributeCase(
                        host("input").input("size", Browser.number(2)), element -> attribute(element, "size", "2")),
                new AttributeCase(
                        host("ol").input("start", Browser.number(0)), element -> attribute(element, "start", "0")),
                new AttributeCase(
                        host("input").input("size", Browser.number(0)), element -> attribute(element, "size", false)),
                new AttributeCase(
                        true,
                        host("ol").input("start", attributeFunction),
                        element -> attribute(element, "start", false)),
                new AttributeCase(
                        true, host("ol").input("start", symbol("foo")), element -> attribute(element, "start", false)),
                new AttributeCase(
                        true,
                        host("input").input("size", attributeFunction),
                        element -> attribute(element, "size", false)),
                new AttributeCase(
                        true, host("input").input("size", symbol("foo")), element -> attribute(element, "size", false)),
                new AttributeCase(
                        ReactElement.createHostElement("div", "foo", null, JsPropertyMap.of()),
                        element -> attribute(element, "key", null)),
                new AttributeCase(
                        host("div").input("dangerouslySetInnerHTML", JsPropertyMap.of("__html", "<foo />")),
                        element -> attribute(element, "dangerouslySetInnerHTML", null)),
                new AttributeCase(
                        host("div").input("suppressContentEditableWarning", true),
                        element -> attribute(element, "suppressContentEditableWarning", null)),
                new AttributeCase(
                        host("span").input("suppressHydrationWarning", true),
                        element -> attribute(element, "suppressHydrationWarning", null)),
                new AttributeCase(
                        host("div").input("aria-label", "hello"), element -> attribute(element, "aria-label", "hello")),
                new AttributeCase(
                        host("div").input("aria-label", false), element -> attribute(element, "aria-label", "false")),
                new AttributeCase(
                        host("div").input("aria-label", null), element -> attribute(element, "aria-label", false)),
                new AttributeCase(
                        true, host("div").input("aria", "hello"), element -> attribute(element, "aria", "hello")),
                new AttributeCase(true, host("meta").input("httpequiv", "refresh"), element -> {
                    attribute(element, "http-equiv", false);
                    attribute(element, "httpequiv", "refresh");
                }),
                new AttributeCase(true, children("svg", host("text").input("textlength", "10")), element -> {
                    attribute(Js.uncheckedCast(child(element, 0)), "textlength", "10");
                    attribute(Js.uncheckedCast(child(element, 0)), "textLength", false);
                }),
                new AttributeCase(true, children("svg", host("text").input("strokedasharray", "10 10")), element -> {
                    attribute(Js.uncheckedCast(child(element, 0)), "stroke-dasharray", false);
                    attribute(Js.uncheckedCast(child(element, 0)), "strokedasharray", "10 10");
                }),
                new AttributeCase(
                        true,
                        children("svg", host("text").input("stroke-dasharray", "10 10")),
                        element -> attribute(Js.uncheckedCast(child(element, 0)), "stroke-dasharray", "10 10")),
                new AttributeCase(host("div").input("foo", "bar"), element -> attribute(element, "foo", "bar")),
                new AttributeCase(
                        host("div").input("data-foo", "bar"), element -> attribute(element, "data-foo", "bar")),
                new AttributeCase(
                        true, host("div").input("CHILDREN", "5"), element -> attribute(element, "CHILDREN", "5")),
                new AttributeCase(
                        host("object").input("data", "hello"), element -> attribute(element, "data", "hello")),
                new AttributeCase(
                        host("div").input("data-foo", null), element -> attribute(element, "data-foo", false)),
                new AttributeCase(
                        true,
                        host("div").input("data-fooBar", "true"),
                        element -> attribute(element, "data-foobar", "true")),
                new AttributeCase(
                        host("div").input("data-foobar", true), element -> attribute(element, "data-foobar", "true")),
                new AttributeCase(
                        host("div").input("data-foobar", false), element -> attribute(element, "data-foobar", "false")),
                new AttributeCase(
                        true,
                        host("div").input("data-fooBar", null),
                        element -> attribute(element, "data-foobar", false)),
                new AttributeCase(
                        children("svg", host("font-face").input("accentHeight", Browser.number(10))), element -> {
                            attribute(Js.uncheckedCast(child(element, 0)), "accentHeight", false);
                            attribute(Js.uncheckedCast(child(element, 0)), "accent-height", "10");
                        }),
                new AttributeCase(
                        true, host("div").input("fooBar", "test"), element -> attribute(element, "foobar", "test")),
                new AttributeCase(host("div").input("onClick", attributeFunction), element -> {
                    attribute(element, "onClick", null);
                    attribute(element, "onClick", null);
                    attribute(element, "click", null);
                }),
                new AttributeCase(
                        true,
                        host("div").input("onunknownevent", "alert(\"hack\")"),
                        element -> attribute(element, "onunknownevent", null)),
                new AttributeCase(
                        host("div").input("on", "tap:do-something"),
                        element -> attribute(element, "on", "tap:do-something")),
                new AttributeCase(
                        host("div").input("is", "custom-element").input("class", "test"),
                        element -> attribute(element, "class", "test")),
                new AttributeCase(
                        host("div").input("is", "custom-element").input("className", "test"),
                        element -> attribute(element, "className", "test")),
                new AttributeCase(
                        host("div").input("is", "custom-element").input("htmlFor", "test"),
                        element -> attribute(element, "htmlFor", "test")),
                new AttributeCase(
                        host("div").input("is", "custom-element").input("for", "test"),
                        element -> attribute(element, "for", "test")),
                new AttributeCase(
                        host("custom-element").input("foo", "bar"), element -> attribute(element, "foo", "bar")),
                new AttributeCase(
                        host("custom-element").input("onunknown", "bar"),
                        element -> attribute(element, "onunknown", "bar")),
                new AttributeCase(
                        host("custom-element").input("foo", true), element -> attribute(element, "foo", "true")),
                new AttributeCase(
                        host("custom-element").input("foo", false), element -> attribute(element, "foo", "false")),
                new AttributeCase(
                        host("custom-element").input("foo", null), element -> attribute(element, "foo", false)),
                new AttributeCase(
                        host("div").input("is", "custom-element").input("foo", "bar"),
                        element -> attribute(element, "foo", "bar")),
                new AttributeCase(
                        host("div").input("is", "custom-element").input("foo", null),
                        element -> attribute(element, "foo", false)),
            }) {
                if (vector.upstreamWarning()) {
                    ReactDOM.render(vector.input(), root);
                } else {
                    expectWarnings(production, () -> ReactDOM.render(vector.input(), root));
                }
                vector.verify().check(Js.uncheckedCast(child(root, 0)));
                ReactDOM.unmountComponentAtNode(root);
            }
            final Ref ref = element -> {};
            expectWarnings(
                    production,
                    () -> ReactDOM.render(ReactElement.createHostElement("div", null, ref, JsPropertyMap.of()), root));
            attribute(Browser.query(root, "div"), "ref", (String) null);
            ReactDOM.unmountComponentAtNode(root);
            expectWarnings(production, () -> ReactDOM.render(host("div").input("children", "foo"), root));
            attribute(Browser.query(root, "div"), "children", (String) null);
            expectWarnings(
                    production,
                    () -> ReactDOM.render(host("nonstandard"), root),
                    "The tag <nonstandard> is unrecognized in this browser.");
            ReactDOM.unmountComponentAtNode(root);
            expectWarnings(production, () -> ReactDOM.render(host("nonstandard").input("foo", "bar"), root));
            attribute(Browser.query(root, "nonstandard"), "foo", "bar");
        } finally {
            close(root);
        }
    }

    private record AttributeCase(boolean upstreamWarning, ReactElement input, AttributeAssertion verify) {
        private AttributeCase(ReactElement input, AttributeAssertion verify) {
            this(false, input, verify);
        }
    }

    private interface AttributeAssertion {
        void check(Browser.Element element);
    }

    @JsFunction
    private interface Ref {
        void accept(Browser.@Nullable Element element);
    }

    private static void attribute(Browser.Element element, String name, boolean present) {
        check(present == element.hasAttribute(name), name + " presence expected " + present);
    }

    @JsMethod
    public static void unknownAttributes(boolean production) {
        final var root = Browser.append("div");
        try {
            final Stringifier stringify = () -> "lol";
            final Stringifier function = () -> "not rendered";
            for (final var vector : new @Nullable Object[][] {
                {"unknown", null, null},
                {"unknown", Js.undefined(), null},
                {"unknown", "a string", "a string"},
                {"unknown", Browser.number(0), "0"},
                {"unknown", Browser.number(-1), "-1"},
                {"unknown", Browser.number(42), "42"},
                {"unknown", Browser.number(9000.99), "9000.99"},
                {"unknown", JsPropertyMap.of("hello", "world"), "[object Object]"},
                {"unknown", JsPropertyMap.of("toString", stringify), "lol"},
            }) {
                updateUnknown(root, Objects.requireNonNull((String) vector[0]), vector[1], (String) vector[2]);
            }
            ReactDOM.render(host("div").input("unknown", "something"), root);
            final var div = Browser.query(root, "div");
            attribute(div, "unknown", "something");
            ReactDOM.render(host("div"), root);
            attribute(div, "unknown", null);
            expectWarnings(
                    production,
                    () -> updateUnknown(root, "unknown", true, null),
                    "Received `true` for a non-boolean attribute `unknown`");
            expectWarnings(production, () -> updateUnknown(root, "unknown", false, null));
            expectWarnings(
                    production,
                    () -> updateUnknown(root, "unknownnan", Browser.number(Double.NaN), "NaN"),
                    "Received NaN for the `unknownnan` attribute");
            expectWarnings(
                    production,
                    () -> updateUnknown(root, "unknownsymbol", symbol("foo"), null),
                    "Invalid value for prop `unknownsymbol` on <div>");
            expectWarnings(
                    production,
                    () -> updateUnknown(root, "unknownfunction", function, null),
                    "Invalid value for prop `unknownfunction` on <div>");
            expectWarnings(
                    production,
                    () -> ReactDOM.render(host("div").input("helloWorld", "something"), root),
                    "spell it as lowercase `helloworld` instead");
            attribute(Browser.query(root, "div"), "helloworld", "something");
        } finally {
            close(root);
        }
    }

    private static void updateUnknown(
            Browser.Element root, String key, @Nullable Object value, @Nullable String expected) {
        ReactDOM.render(host("div").input(key, "something"), root);
        final var div = Browser.query(root, "div");
        attribute(div, key, "something");
        ReactDOM.render(host("div").input(key, value), root);
        attribute(div, key, expected);
    }

    @JsMethod
    public static void propertyOperations(boolean production) {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(host("div").input("title", "Tip!").input("role", "#"), root);
            final var div = Browser.query(root, "div");
            property(div, "title", "Tip!");
            attribute(div, "role", "#");
            // Current Chromium reflects role through the ARIAMixin property as well.
            property(div, "role", "#");

            ReactDOM.render(children("svg", host("image").input("xlinkHref", "about:blank")), root);
            equal("about:blank", attributeNS(Browser.query(root, "image"), XLINK, "href"));
            for (final var disabled : new @Nullable Object[] {"disabled", true, false, null, Js.undefined()}) {
                ReactDOM.render(host("div").input("disabled", true), root);
                ReactDOM.render(host("div").input("disabled", disabled), root);
                attribute(Browser.query(root, "div"), "disabled", Js.isTruthy(disabled) ? "" : null);
            }

            final Stringifier cssClass = () -> "css-class";
            ReactDOM.render(host("div").input("className", JsPropertyMap.of("toString", cssClass)), root);
            attribute(Browser.query(root, "div"), "class", "css-class");
            final ReactEventHandler ignore = event -> {};
            ReactDOM.render(host("input").input("value", "").input("onChange", ignore), root);
            var input = Browser.query(root, "input");
            attribute(input, "value", "");
            equal("", input.getValue());
            ReactDOM.render(
                    children(
                            "select",
                            host("option").input("value", "").input("children", "empty"),
                            host("option").input("children", "filled")),
                    root);
            final var options = Browser.queryAll(root, "option");
            equal("", options[0].getValue());
            equal("filled", options[1].getValue());
            ReactDOM.render(host("iframe").input("allowFullScreen", false), root);
            attribute(Browser.query(root, "iframe"), "allowfullscreen", false);

            ReactDOM.render(
                    host("div")
                            .input("allowFullScreen", false)
                            .input("data-foo", "bar")
                            .input("className", "selected")
                            .input("hidden", true)
                            .input("title", "foo"),
                    root);
            final var properties = Browser.query(root, "div");
            attribute(properties, "allowfullscreen", null);
            attribute(properties, "data-foo", "bar");
            property(properties, "className", "selected");
            attribute(properties, "hidden", "");
            attribute(properties, "title", "foo");
            ReactDOM.render(
                    host("div").input("data-foo", null).input("className", null).input("hidden", false), root);
            attribute(properties, "data-foo", null);
            property(properties, "className", "");
            attribute(properties, "class", null);
            attribute(properties, "hidden", null);
            attribute(properties, "title", null);

            ReactDOM.render(host("progress"), root);
            final var progress = Browser.query(root, "progress");
            check(
                    2
                            == attributeWrites(progress, "value", () -> {
                                ReactDOM.render(host("progress").input("value", Browser.number(30)), root);
                                ReactDOM.render(host("progress").input("value", "30"), root);
                            }),
                    "Number and string updates each set the raw value attribute");
            ReactDOM.render(host("progress").input("value", null), root);
            attribute(progress, "value", false);

            ReactDOM.render(
                    host("input").input("type", "text").input("value", "foo").input("onChange", ignore), root);
            input = Browser.query(root, "input");
            attribute(input, "value", "foo");
            equal("foo", input.getValue());
            expectWarnings(
                    production,
                    () -> ReactDOM.render(host("input").input("type", "text").input("onChange", ignore), root),
                    "controlled input of type text to be uncontrolled");
            attribute(input, "value", "foo");
            equal("foo", input.getValue());
            ReactDOM.render(host("my-icon").input("size", "5px"), root);
            attribute(Browser.query(root, "my-icon"), "size", "5px");
        } finally {
            close(root);
        }
    }

    private static void attribute(Browser.Element element, String name, @Nullable String expected) {
        check(Objects.equals(expected, element.getAttribute(name)), name + " expected " + expected);
    }

    @JsMethod
    private static native int attributeWrites(Browser.Element element, String attributeName, Action action);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Symbol")
    private static native Object symbol(String description);

    @JsFunction
    private interface Stringifier {
        String stringify();
    }

    @JsMethod
    public static void staleContainers(boolean production) {
        final String message = "the React-rendered content of this container was removed without using React";
        for (final var outside : new String[] {"<div>MEOW.</div>", ""}) {
            final var root = Browser.append("div");
            ReactDOM.render(host("div").input("children", "foo"), root);
            final var original = Browser.query(root, "div");
            try {
                ReactDOM.render(host("div").input("children", "bar"), root);
                property(root, "innerHTML", "<div>bar</div>");
                replaceMarkup(root, outside);
                expectWarnings(production, () -> ReactDOM.render(host("div").input("children", "baz"), root), message);
                property(root, "innerHTML", outside);
            } finally {
                if (!root.contains(original)) {
                    append(root, original);
                }
                close(root);
            }
        }

        final var root = Browser.append("div");
        try {
            ReactDOM.render(keyedText("1", "foo"), root);
            ReactDOM.render(keyedText("1", "bar"), root);
            property(root, "innerHTML", "<div>bar</div>");
            replaceMarkup(root, "<div>MEOW.</div>");
            final var diagnostics = captureWarnings(() -> {
                boolean failed = false;
                try {
                    ReactDOM.render(keyedText("2", "baz"), root);
                } catch (final Throwable error) {
                    failed = true;
                }
                check(failed, "Replacing a detached host triggers a DOM removal error");
            });
            if (production) {
                check(
                        1 == diagnostics.length && diagnostics[0].contains("NotFoundError"),
                        "Reports the native removal error");
            } else {
                check(
                        2 == diagnostics.length && diagnostics[0].contains(message),
                        "The stale-root diagnostic precedes the removal error");
                check(
                        diagnostics[1].contains("The above error occurred in the <div> component"),
                        "Reports the host that failed to commit");
            }
        } finally {
            close(root);
        }
    }

    private static ReactElement keyedText(String key, String content) {
        return ReactElement.createHostElement("div", key, null, JsPropertyMap.of("children", content));
    }

    @JsMethod
    public static void portalTeardown() {
        final var root = Browser.append("div");
        final var target = Browser.append("div");
        try {
            ReactDOM.render(
                    children("div", ReactDOM.createPortal(host("div").input("children", "portal"), target)), root);
            property(target, "innerHTML", "<div>portal</div>");
            property(root, "innerHTML", "<div></div>");
            ReactDOM.unmountComponentAtNode(root);
            property(target, "innerHTML", "");
            property(root, "innerHTML", "");
        } finally {
            close(root);
            target.remove();
        }
    }

    @JsMethod
    public static void shadyDom(boolean production) {
        final var root = Browser.append("div");
        try {
            final ViewConstructorFunction constructor = ShadyComponent::new;
            Objects.requireNonNull(Js.asPropertyMap(constructor)).set("displayName", "ShadyComponent");
            final var view = ReactElement.createViewElement(constructor);
            withShadyRoot(() -> {
                expectWarnings(
                        production,
                        () -> ReactDOM.render(view, root),
                        "ShadyComponent is using shady DOM. Using shady DOM with React can cause things to break"
                                + " subtly.");
                expectWarnings(production, () -> ReactDOM.render(host("div").input("is", "custom-shady-div2"), root));
            });
        } finally {
            close(root);
        }
    }

    private static final class ShadyComponent extends NativeView {
        @JsConstructor
        private ShadyComponent(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return host("polymer-component");
        }
    }

    private static void expectWarnings(boolean production, Action action, String... expected) {
        final var actual = captureWarnings(action);
        check((production ? 0 : expected.length) == actual.length, "Unexpected warnings " + String.join(", ", actual));
        if (!production) {
            for (int index = 0; index < expected.length; index++) {
                check(
                        actual[index].contains(expected[index]),
                        "Expected diagnostic " + expected[index] + ", got " + actual[index]);
            }
        }
    }

    @JsMethod
    private static native String[] captureWarnings(Action action);

    @JsMethod
    private static native void replaceMarkup(Browser.Element root, String markup);

    @JsMethod
    private static native void withShadyRoot(Action action);

    @JsMethod
    public static void attributeMutations() {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(host("div").input("id", ""), root);
            final var div = Browser.query(root, "div");
            for (final var change : new MutationCase[] {
                new MutationCase("", 0, 0),
                new MutationCase("foo", 1, 0),
                new MutationCase("foo", 0, 0),
                new MutationCase(null, 0, 1),
                new MutationCase("", 1, 0),
                new MutationCase(null, 0, 1)
            }) {
                final var next = host("div");
                if (null != change.value()) {
                    next.input("id", change.value());
                }
                final var changes = countAttributeMutations(div, () -> ReactDOM.render(next, root));
                check(change.writes() == changes[0], "Expected attribute writes: " + change.writes());
                check(change.removals() == changes[1], "Expected attribute removals: " + change.removals());
            }

            ReactDOM.render(host("div").input("value", ""), root);
            final var changes = new @Nullable Object[] {"foo", "foo", Js.undefined(), null, "", Js.undefined()};
            final int[] writes = {1, 0, 0, 0, 1, 0};
            for (int i = 0; i < changes.length; i++) {
                final var next = host("div").input("value", changes[i]);
                check(
                        writes[i] == attributeWrites(div, "value", () -> ReactDOM.render(next, root)),
                        "Expected value attribute writes: " + writes[i]);
            }

            ReactDOM.render(host("div"), root);
            check(
                    0
                            == attributeWrites(div, "dir", () -> {
                                ReactDOM.render(host("div").input("dir", null), root);
                                ReactDOM.render(host("div").input("dir", Js.undefined()), root);
                                ReactDOM.render(host("div"), root);
                            }),
                    "Omitted direction is unchanged");
            check(
                    1
                            == attributeWrites(
                                    div,
                                    "dir",
                                    () -> ReactDOM.render(host("div").input("dir", "ltr"), root)),
                    "Setting direction writes once");
        } finally {
            close(root);
        }
    }

    private record MutationCase(@Nullable String value, int writes, int removals) {}

    @JsMethod
    private static native int[] countAttributeMutations(Browser.Element element, Action action);

    @JsMethod
    public static void attributeUpdates() {
        final var root = Browser.append("div");
        try {
            for (final var value : new @Nullable String[] {"foo", "bar", null}) {
                ReactDOM.render(host("div").input("className", value), root);
                property(Browser.query(root, "div"), "className", null == value ? "" : value);
            }
            ReactDOM.render(host("div").input("height", "17"), root);
            final var div = Browser.query(root, "div");
            attribute(div, "height", true);
            ReactDOM.render(host("div"), root);
            attribute(div, "height", false);
            ReactDOM.render(host("div").input("className", "monkey"), root);
            property(div, "className", "monkey");
            ReactDOM.render(host("div"), root);
            property(div, "className", "");

            final var nullSrc = host("img").input("src", null).input("data-foo", Js.undefined());
            final var nullData = host("img").input("src", Js.undefined()).input("data-foo", null);
            for (final var inputs : new ReactElement[] {nullSrc, nullData, nullSrc, host("img"), nullData}) {
                ReactDOM.render(inputs, root);
                final var img = Browser.query(root, "img");
                attribute(img, "src", false);
                attribute(img, "data-foo", false);
            }

            for (final var variant :
                    new String[][] {{"form", "acceptCharset", "accept-charset"}, {"text", "arabicForm", "arabic-form"}
                    }) {
                for (final var value :
                        new @Nullable Object[] {"foo", "boo", null, "foo", Js.undefined(), "foo", null}) {
                    final var tag = host(variant[0]).input(variant[1], value);
                    ReactDOM.render("text".equals(variant[0]) ? children("svg", tag) : tag, root);
                    final var element = Browser.query(root, variant[0]);
                    attribute(element, variant[2], Js.isTruthy(value) ? (String) value : null);
                    attribute(element, variant[1], false);
                }
            }
            ReactDOM.render(host("some-tag").input("foo", "bar"), root);
            ReactDOM.render(host("some-tag").input("bar", "buzz"), root);
            final var custom = Browser.query(root, "some-tag");
            attribute(custom, "foo", false);
            attribute(custom, "bar", "buzz");
            for (final var value : new @Nullable String[] {"foo", "boo", null}) {
                ReactDOM.render(host("some-tag").input("arabicForm", value), root);
                attribute(custom, "arabicForm", value);
                attribute(custom, "arabic-form", false);
            }
            ReactDOM.render(host("some-tag").input("acceptCharset", "buzz"), root);
            attribute(custom, "acceptCharset", "buzz");
            attribute(custom, "accept-charset", false);
            ReactDOM.render(
                    children(
                            "div",
                            host("some-tag").input("slot", "first"),
                            host("div").input("slot", "second")),
                    root);
            final var parent = Browser.query(root, "div");
            attribute(Js.uncheckedCast(child(parent, 0)), "slot", "first");
            attribute(Js.uncheckedCast(child(parent, 1)), "slot", "second");

            ReactDOM.render(host("div").input("is", "custom-element").input("class", "foo"), root);
            ReactDOM.render(host("div").input("is", "custom-element").input("class", "bar"), root);
            attribute(parent, "class", "bar");
            for (final var value : new @Nullable Object[] {true, false, null, true}) {
                ReactDOM.render(host("some-custom-element").input("foo", value), root);
                attribute(Browser.query(root, "some-custom-element"), "foo", null == value ? null : value.toString());
            }
            for (final var value : new @Nullable String[] {"bar", "buzz", null, "bar"}) {
                ReactDOM.render(host("some-custom-element").input("onx", value), root);
                attribute(Browser.query(root, "some-custom-element"), "onx", value);
            }
            ReactDOM.render(host("my-component"), root);
            ReactDOM.render(host("my-component").input("myattr", "myval"), root);
            attribute(Browser.query(root, "my-component"), "myattr", "myval");

            ReactDOM.render(host("div").input("whatever", "30"), root);
            final var ordinary = Browser.query(root, "div");
            attribute(ordinary, "whatever", "30");
            ReactDOM.render(host("div").input("whatever", null), root);
            attribute(ordinary, "whatever", false);
            ReactDOM.render(host("div").input("whatever", Browser.number(3)), root);
            attribute(ordinary, "whatever", "3");
            ReactDOM.render(host("div").input("whatever", JsPropertyMap.of()), root);
            attribute(ordinary, "whatever", "[object Object]");
            ReactDOM.render(host("div").input("ajaxify", "ajaxy"), root);
            attribute(ordinary, "ajaxify", "ajaxy");
            for (final boolean value : new boolean[] {true, false, true}) {
                ReactDOM.render(host("div").input("spellCheck", value), root);
                attribute(ordinary, "spellCheck", Boolean.toString(value));
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void innerHtml() {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(rawHtml("div", "<h1>Hello</h1>"), root);
            final var div = Browser.query(root, "div");
            property(div, "innerHTML", "<h1>Hello</h1>");
            ReactDOM.render(host("div"), root);
            replaceMarkup(div, "bonjour");
            property(div, "innerHTML", "bonjour");
            ReactDOM.render(host("div"), root);
            property(div, "innerHTML", "bonjour");
            for (final var empty : new @Nullable Object[] {null, Js.undefined(), false}) {
                ReactDOM.render(host("div").input("children", "bonjour"), root);
                property(div, "innerHTML", "bonjour");
                ReactDOM.render(host("div").input("children", empty), root);
                property(div, "innerHTML", "");
            }
            ReactDOM.render(rawHtml("div", ":)"), root);
            property(div, "innerHTML", ":)");
            ReactDOM.render(host("div"), root);
            property(div, "innerHTML", "");
            ReactDOM.render(host("div").input("children", "hello"), root);
            property(div, "innerHTML", "hello");
            ReactDOM.render(rawHtml("div", "goodbye"), root);
            property(div, "innerHTML", "goodbye");
            ReactDOM.render(rawHtml("div", "bonjour"), root);
            property(div, "innerHTML", "bonjour");
            ReactDOM.render(host("div").input("children", "adieu"), root);
            property(div, "innerHTML", "adieu");
            final var html = children("div", rawHtml("div", "bonjour"));
            final var span = children("div", children("div", children("span", ReactNode.of("adieu"))));
            ReactDOM.render(html, root);
            property(root, "textContent", "bonjour");
            ReactDOM.render(span, root);
            property(root, "textContent", "adieu");
            ReactDOM.render(html, root);
            property(root, "textContent", "bonjour");
        } finally {
            close(root);
        }
        withoutSvgInnerHtml(() -> {
            final var svg = elementNS(SVG, "svg");
            try {
                ReactDOM.render(rawHtml("g", "<circle></circle>"), svg);
                final var circle = Browser.query(svg, "circle");
                namespace(circle, SVG, "circle");
                ReactDOM.unmountComponentAtNode(svg);
                ReactDOM.render(rawHtml("g", "<rect></rect>"), svg);
                final var rect = Browser.query(svg, "rect");
                namespace(rect, SVG, "rect");
                ReactDOM.render(rawHtml("g", "<circle></circle>"), svg);
                namespace(Browser.query(svg, "circle"), SVG, "circle");
                check(!svg.contains(rect), "Updating SVG markup removes the original element");
            } finally {
                ReactDOM.unmountComponentAtNode(svg);
            }
        });
    }

    @JsMethod
    public static void textNodes() {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(children("div", host("span"), ReactNode.of("foo"), ReactNode.of("bar")), root);
            final var parent = Browser.query(root, "div");
            final var first = child(parent, 1);
            final var second = child(parent, 2);
            property(first, "data", "foo");
            property(second, "data", "bar");
            ReactDOM.render(children("div", host("span"), ReactNode.of("baz"), ReactNode.of("qux")), root);
            check(Browser.same(first, child(parent, 1)), "First text node is reused");
            check(Browser.same(second, child(parent, 2)), "Second text node is reused");
            property(first, "data", "baz");
            property(second, "data", "qux");

            ReactDOM.render(children("div", ReactNode.of("foo"), host("div"), ReactNode.of("bar")), root);
            final var middle = child(parent, 1);
            ReactDOM.render(children("div", null, host("div"), null), root);
            check(1 == childCount(parent), "Hiding text removes both text nodes");
            check(Browser.same(middle, child(parent, 0)), "Middle element is reused");
            ReactDOM.render(children("div", ReactNode.of("foo"), host("div"), ReactNode.of("bar")), root);
            check(3 == childCount(parent), "Restoring text inserts both nodes");
            property(child(parent, 0), "data", "foo");
            check(Browser.same(middle, child(parent, 1)), "Middle element survives after restoring text");
            property(child(parent, 2), "data", "bar");
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void svgNamespaces() {
        final var root = Browser.append("div");
        try {
            final var group = children(
                            "g",
                            children(
                                    "svg",
                                    children("foreignObject", children("svg", host("svg"), image()), host("div"))),
                            image(),
                            children("foreignObject", host("div")))
                    .input("strokeWidth", "5");
            ReactDOM.render(
                    children("div", children("svg", group), children("p", children("svg", image())), host("div")),
                    root);
            for (final var node : Browser.queryAll(root, "div")) {
                namespace(node, HTML, "DIV");
            }
            namespace(Browser.query(root, "p"), HTML, "P");
            svgGroup(root, 5, 3, 2);
            ReactDOM.unmountComponentAtNode(root);

            ReactDOM.render(host("svg"), root);
            final var initial = Browser.query(root, "svg");
            ReactDOM.render(children("svg", group), root);
            check(Browser.same(initial, Browser.query(root, "svg")), "The update preserves the SVG root");
            svgGroup(root, 4, 2, 2);

            final var outer = elementNS(SVG, "svg");
            final var nested = elementNS(SVG, "g");
            final var foreign = elementNS(SVG, "foreignObject");
            append(outer, nested);
            append(outer, foreign);
            ReactDOM.render(host("image"), nested);
            namespace(Browser.query(nested, "image"), SVG, "image");
            ReactDOM.render(host("div"), foreign);
            namespace(Browser.query(foreign, "div"), HTML, "DIV");
            ReactDOM.unmountComponentAtNode(nested);
            ReactDOM.unmountComponentAtNode(foreign);
        } finally {
            close(root);
        }
    }

    private static void svgGroup(Browser.Element root, int svgCount, int imageCount, int htmlCount) {
        final var svgs = Browser.queryAll(root, "svg");
        check(svgCount == svgs.length, "Expected SVG count " + svgCount);
        for (final var svg : svgs) {
            namespace(svg, SVG, "svg");
        }
        final var g = Browser.query(root, "g");
        namespace(g, SVG, "g");
        equal("5", g.getAttribute("stroke-width"));
        final var images = Browser.queryAll(root, "image");
        check(imageCount == images.length, "Expected image count " + imageCount);
        for (final var image : images) {
            namespace(image, SVG, "image");
            equal(IMAGE, attributeNS(image, XLINK, "href"));
        }
        final var foreign = Browser.queryAll(root, "foreignObject");
        check(2 == foreign.length, "Two boundaries switch to HTML");
        for (final var outer : foreign) {
            namespace(outer, SVG, "foreignObject");
        }
        final var html = Browser.queryAll(root, "foreignObject > div");
        check(htmlCount == html.length, "Expected HTML children of foreignObject");
        for (final var node : html) {
            namespace(node, HTML, "DIV");
        }
    }

    private static ReactElement image() {
        return host("image").input("xlinkHref", IMAGE);
    }

    private static void namespace(Browser.Element node, String uri, String tag) {
        property(node, "namespaceURI", uri);
        property(node, "tagName", tag);
    }

    private static void property(Object node, String name, @Nullable Object value) {
        check(Browser.same(value, Browser.property(node, name)), name + " expected " + value);
    }

    private static ReactElement host(String tag) {
        return ReactElement.createHostElement(tag, null, null, JsPropertyMap.of());
    }

    private static ReactElement rawHtml(String tag, @Nullable Object html) {
        return host(tag).input("dangerouslySetInnerHTML", JsPropertyMap.of("__html", html));
    }

    private static ReactElement children(String tag, @Nullable Renderable... content) {
        return host(tag).input("children", ReactNode.of(content));
    }

    private static void close(Browser.Element root) {
        ReactDOM.unmountComponentAtNode(root);
        root.remove();
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "document.createElementNS")
    private static native Browser.Element elementNS(String namespace, String tag);

    @JsMethod
    private static native @Nullable String attributeNS(Browser.Element element, String namespace, String name);

    @JsMethod
    private static native void append(Browser.Element parent, Browser.Element child);

    @JsMethod
    private static native Object child(Browser.Element parent, int index);

    @JsMethod
    private static native int childCount(Browser.Element parent);

    @JsMethod
    private static native void withoutSvgInnerHtml(Action action);

    @JsFunction
    private interface Action {
        void run();
    }
}
