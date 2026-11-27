package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Locale;
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
import react4j.internal.OnComponentWillUnmount;
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
            styleEquals(
                    mountStyle(root, production, JsPropertyMap.of("color", "red", "width", "30px")),
                    "color",
                    "red",
                    "width",
                    "30px");
            final var lengths = JsPropertyMap.<@Nullable Object>of("left", 0, "margin", 16, "opacity", 0.5);
            lengths.set("padding", "4px");
            styleEquals(
                    mountStyle(root, production, lengths),
                    "left",
                    "0px",
                    "margin",
                    "16px",
                    "opacity",
                    "0.5",
                    "padding",
                    "4px");
            styleEquals(mountStyle(root, production, JsPropertyMap.of("--foo", 5)), "--foo", "5");
            styleEquals(
                    mountStyle(root, production, JsPropertyMap.of("--foo-primary", "red", "backgroundColor", "red")),
                    "--foo-primary",
                    "red",
                    "background-color",
                    "red");
            for (final var unset : new @Nullable Object[] {Js.undefined(), null}) {
                styleEquals(
                        mountStyle(root, production, JsPropertyMap.of("color", unset, "width", "30px")),
                        "color",
                        "",
                        "width",
                        "30px");
            }
            final var fresh = mountStyle(root, production, JsPropertyMap.of("color", null, "width", null));
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
            final var writes = styleWrites(() -> mountStyle(root, production, prefixed));
            equal(
                    "line-clamp=10,-webkit-line-clamp=10,-moz-flex-grow=10,-ms-flex-grow=10,-ms-grid-row=10,"
                            + "-ms-grid-row-end=10,-ms-grid-row-span=10,-ms-grid-row-start=10,-ms-grid-column=10,"
                            + "-ms-grid-column-end=10,-ms-grid-column-span=10,-ms-grid-column-start=10",
                    String.join(",", writes));
        } finally {
            close(root);
        }
    }

    private static Browser.Element mountStyle(Browser.Element root, boolean production, Object styles) {
        ReactDOM.unmountComponentAtNode(root);
        expectWarnings(production, () -> ReactDOM.render(style("div", styles), root));
        return Browser.query(root, "div");
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
    private static native String[] styleWrites(Action action);

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
                final var warnings = captureWarnings(() -> ReactDOM.render(vector.input(), root));
                // An earlier fixture can already have consumed a warning for the same property.
                check(
                        warnings.length <= (!production && vector.upstreamWarning() ? 1 : 0),
                        "Unexpected attribute diagnostics " + String.join(", ", warnings));
                for (final var warning : warnings) {
                    check(
                            warning.contains("Invalid value for prop")
                                    || warning.contains("non-boolean attribute")
                                    || warning.contains("Invalid DOM property")
                                    || warning.contains("reserved for future use")
                                    || warning.contains("Unknown event handler property")
                                    || warning.contains("spell it as lowercase"),
                            warning);
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

    @JsMethod
    public static void unnamedShadyDom(boolean production) {
        final var root = Browser.append("div");
        try {
            withShadyRoot(() -> {
                expectWarnings(
                        production,
                        () -> ReactDOM.render(host("div").input("is", "custom-shady-div"), root),
                        "A component is using shady DOM. Using shady DOM with React can cause things to break subtly.");
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
    public static void reservedPropsAndMarkup() {
        final var root = Browser.append("div");
        try {
            for (final boolean enabled : new boolean[] {true, false}) {
                ReactDOM.render(
                        children("my-component", ReactNode.of(enabled ? "foo" : "bar"))
                                .input("suppressContentEditableWarning", enabled)
                                .input("suppressHydrationWarning", enabled),
                        root);
                final var custom = Browser.query(root, "my-component");
                attribute(custom, "children", false);
                attribute(custom, "suppressContentEditableWarning", false);
                attribute(custom, "suppressHydrationWarning", false);
            }
            for (final var value : new String[] {"hi", "bye"}) {
                ReactDOM.render(rawHtml("my-component", value), root);
                attribute(Browser.query(root, "my-component"), "dangerouslySetInnerHTML", false);
            }
            for (final var vector : new @Nullable Object[][] {
                {"textContent", "textContent"}, {Browser.number(0), "0"}, {false, "false"},
                {"", ""}, {null, ""}, {Js.undefined(), ""}
            }) {
                ReactDOM.render(rawHtml("div", vector[0]), root);
                equal(Objects.requireNonNull((String) vector[1]), root.getTextContent());
            }
            ReactDOM.unmountComponentAtNode(root);
            for (final var html : new String[] {
                "\n  \t  <span>  \n  testContent  \t  </span>  \n  \t",
                "\n  \t  <span>  \n  testContent1  \t  </span>  \n  \t",
                "\n  \t  <div>  \n  testContent2  \t  </div>  \n  \t"
            }) {
                ReactDOM.render(rawHtml("div", html), root);
                property(Browser.query(root, "div"), "innerHTML", html);
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void invalidHostInputs(boolean production) {
        final var root = Browser.append("div");
        final var voidMessage =
                "input is a void element tag and must neither have `children` nor use `dangerouslySetInnerHTML`.";
        final var htmlMessage = "`props.dangerouslySetInnerHTML` must be in the form `{__html: ...}`.";
        final var styleMessage = "The `style` prop expects a mapping from style properties to values, not a string.";
        final var exclusive = "Can only set one of `children` or `props.dangerouslySetInnerHTML`.";
        try {
            for (final var invalid :
                    new ReactElement[] {host("input").input("children", "children"), rawHtml("input", "content")}) {
                final var failure = rejected(() -> ReactDOM.render(invalid, root), voidMessage);
                check(production || failure.contains("in input"), "The diagnostic identifies the input");
                ReactDOM.render(host("input"), root);
                rejected(() -> ReactDOM.render(invalid, root), voidMessage);
            }
            final ViewConstructorFunction invalidInput = inputs -> new NativeView(inputs) {
                @Override
                public ReactNode render() {
                    return host("input").input("children", "moo");
                }
            };
            Objects.requireNonNull(Js.asPropertyMap(invalidInput)).set("displayName", "X");
            final var inputFailure =
                    rejected(() -> ReactDOM.render(ReactElement.createViewElement(invalidInput), root), voidMessage);
            check(
                    production || inputFailure.contains("in input") && inputFailure.contains("in X"),
                    "The diagnostic identifies the owner and its input");
            rejected(
                    () -> ReactDOM.render(host("div").input("children", "").input("dangerouslySetInnerHTML", ""), root),
                    exclusive);
            ReactDOM.render(host("div"), root);
            rejected(() -> ReactDOM.render(rawHtml("div", "").input("children", ""), root), exclusive);
            rejected(
                    () -> ReactDOM.render(host("div").input("dangerouslySetInnerHTML", "<span>Hi Jim!</span>"), root),
                    htmlMessage);
            rejected(
                    () -> ReactDOM.render(
                            host("div").input("dangerouslySetInnerHTML", JsPropertyMap.of("foo", "bar")), root),
                    htmlMessage);
            ReactDOM.render(rawHtml("div", null), root);
            equal("", root.getTextContent());
            ReactDOM.unmountComponentAtNode(root);
            rejected(() -> ReactDOM.render(style("div", "display: none"), root), styleMessage);
            ReactDOM.render(host("div"), root);
            rejected(() -> ReactDOM.render(style("div", Browser.number(1)), root), styleMessage);
            final ViewConstructorFunction animal = StyleComponent::new;
            Objects.requireNonNull(Js.asPropertyMap(animal)).set("displayName", "Animal");
            final var styleFailure = rejected(
                    () -> ReactDOM.render(ReactElement.createViewElement(animal).input("css", Browser.number(1)), root),
                    styleMessage);
            check(
                    production || styleFailure.contains("in div") && styleFailure.contains("in Animal"),
                    "The style diagnostic names the owner and host");
        } finally {
            close(root);
        }
    }

    private static String rejected(Action render, String message) {
        final var failure = new @Nullable String[1];
        captureWarnings(() -> {
            try {
                render.run();
            } catch (final Throwable error) {
                failure[0] = error.getMessage();
            }
        });
        final var actual = Objects.requireNonNull(failure[0], "Invalid host inputs must throw");
        check(actual.contains(message), "Expected " + message + ", observed " + actual);
        return actual;
    }

    @JsMethod
    public static void invalidNames(boolean production) {
        final var root = Browser.append("div");
        try {
            for (final var tag : new String[] {"div", "custom-element"}) {
                for (final var phase : new String[] {"mount", "update"}) {
                    final var whitespace = tag + "-" + phase + " invalid";
                    final var markup = "<" + tag + "-" + phase + ">";
                    expectWarnings(
                            production,
                            () -> {
                                for (int i = 0; i < 3; i++) {
                                    for (final var name : new String[] {whitespace, markup}) {
                                        ReactDOM.unmountComponentAtNode(root);
                                        if ("update".equals(phase)) {
                                            ReactDOM.render(host(tag), root);
                                        }
                                        ReactDOM.render(host(tag).input(name, "ignored"), root);
                                        final var mounted = Objects.requireNonNull(root.getFirstElementChild());
                                        final var attributes =
                                                Objects.requireNonNull(Browser.property(mounted, "attributes"));
                                        check(
                                                0 == Js.asInt(Browser.property(attributes, "length")),
                                                "Rejecting an invalid name leaves no attributes");
                                    }
                                }
                            },
                            "Invalid attribute name: `" + whitespace + "`",
                            "Invalid attribute name: `" + markup + "`");
                }
            }
            for (final var tag : new String[] {"tag with spaces", "main<element>"}) {
                ReactDOM.unmountComponentAtNode(root);
                rejected(() -> ReactDOM.render(host(tag), root), "not a valid name");
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void nativeElementEvents() {
        final var root = Browser.append("div");
        try {
            final var trace = new StringBuilder();
            final ReactEventHandler onLoad =
                    event -> trace.append(event.getType()).append(' ');
            final ReactEventHandler onError =
                    event -> trace.append(event.getType()).append(' ');
            ReactDOM.render(
                    children("video", host("source").input("type", "video/mp4").input("onError", onError)), root);
            event(Browser.query(root, "source"), "error");
            equal("error ", trace.toString());

            trace.setLength(0);
            ReactDOM.render(
                    children("svg", host("image").input("onLoad", onLoad).input("onError", onError)), root);
            final var image = Browser.query(root, "image");
            event(image, "error");
            event(image, "load");
            equal("error load ", trace.toString());

            trace.setLength(0);
            ReactDOM.render(
                    host("link").input("href", "data:text/plain,example").input("onLoad", onLoad), root);
            event(Browser.query(root, "link"), "load");
            equal("load ", trace.toString());
            trace.setLength(0);
            ReactDOM.render(
                    host("link").input("href", "data:text/plain,example").input("onError", onError), root);
            event(Browser.query(root, "link"), "error");
            equal("error ", trace.toString());
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void hostCreation(boolean production) {
        final var root = Browser.append("div");
        try {
            expectWarnings(production, () -> ReactDOM.render(host("bar"), root), "The tag <bar> is unrecognized");
            expectWarnings(production, () -> ReactDOM.render(host("foo"), root), "The tag <foo> is unrecognized");
            expectWarnings(production, () -> ReactDOM.render(host("foo"), root));
            expectWarnings(production, () -> ReactDOM.render(host("time"), root));
            expectWarnings(
                    production,
                    () -> ReactDOM.render(host("hasOwnProperty"), root),
                    "<hasOwnProperty /> is using incorrect casing.",
                    "The tag <hasOwnProperty> is unrecognized");
            final var options =
                    observeCreateOptions(() -> ReactDOM.render(host("div").input("is", "custom-div"), root));
            equal("custom-div", options);

            expectWarnings(
                    production,
                    () -> ReactDOM.render(children("menu", host("menuitem")), root),
                    "The tag <menuitem> is unrecognized");
            ReactDOM.unmountComponentAtNode(root);
            rejected(
                    () -> ReactDOM.render(children("menu", host("menuitem").input("children", "children")), root),
                    "menuitem is a void element tag");
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void siblingUpdatesAndUnmount() {
        final var root = Browser.append("div");
        try {
            ReactDOM.render(children("div", pair("one", "A", "B"), pair("two", "C", "D")), root);
            equal("ABCD", root.getTextContent());
            ReactDOM.render(children("div", pair("one", "B", "A"), pair("two", "D", "C")), root);
            equal("BADC", root.getTextContent());

            final var unmounts = new int[1];
            ReactDOM.render(
                    children(
                            "div",
                            ReactElement.createViewElement(UnmountHost::new)
                                    .input("check", (Action) () -> unmounts[0]++)),
                    root);
            ReactDOM.unmountComponentAtNode(root);
            check(1 == unmounts[0], "The child can see its host during unmount");
        } finally {
            close(root);
        }
    }

    private static ReactElement pair(String key, String first, String second) {
        return ReactElement.createHostElement("div", key, null, JsPropertyMap.of("children", new ReactElement[] {
            keyedText(first, first), keyedText(second, second)
        }));
    }

    private static final class UnmountHost extends NativeView implements OnComponentWillUnmount {
        private @Nullable Object _host;

        @JsConstructor
        private UnmountHost(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return ReactElement.createHostElement("span", null, (Ref) node -> _host = node, JsPropertyMap.of());
        }

        @Override
        public void componentWillUnmount() {
            final var node = Objects.requireNonNull(_host);
            equal("SPAN", Browser.string(Browser.property(node, "nodeName")));
            Js.<Action>uncheckedCast(Objects.requireNonNull(requireInputs().get("check")))
                    .run();
        }
    }

    @JsMethod
    public static void nestedRootEventOrder() {
        final var root = Browser.append("div");
        try {
            final var trace = new StringBuilder();
            final ReactEventHandler outerCapture = event -> trace.append("outer capture ");
            final ReactEventHandler outerBubble = event -> trace.append("outer bubble ");
            final ReactEventHandler innerCapture = event -> trace.append("inner capture ");
            final ReactEventHandler innerBubble = event -> trace.append("inner bubble ");
            ReactDOM.render(host("div").input("onClickCapture", outerCapture).input("onClick", outerBubble), root);
            final var inner = Objects.requireNonNull(root.getFirstElementChild());
            try {
                ReactDOM.render(
                        host("button").input("onClickCapture", innerCapture).input("onClick", innerBubble), inner);
                documentClickOrder(
                        inner,
                        () -> trace.append("document capture "),
                        () -> trace.append("document bubble "),
                        Objects.requireNonNull(inner.getFirstElementChild())::click);
                equal(
                        "document capture inner capture inner bubble outer capture outer bubble document bubble ",
                        trace.toString());
            } finally {
                ReactDOM.unmountComponentAtNode(inner);
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    private static native void documentClickOrder(Browser.Element root, Action capture, Action bubble, Action click);

    @JsMethod
    private static native void event(Browser.Element element, String type);

    @JsMethod
    private static native String observeCreateOptions(Action action);

    @JsMethod
    public static void mixedTextKeyDiagnostics(boolean production) {
        final var root = Browser.append("div");
        try {
            final var unkeyed = JsPropertyMap.<@Nullable Object>of("validated", false);
            final var nestedUnkeyed = JsPropertyMap.<@Nullable Object>of("validated", false);
            final var leaf = host("div");
            final var nestedLeaf = host("div");
            Js.<JsPropertyMap<Object>>uncheckedCast(leaf).set("_store", unkeyed);
            Js.<JsPropertyMap<Object>>uncheckedCast(nestedLeaf).set("_store", nestedUnkeyed);
            final var input = host("div").input("children", new Object[] {
                "a", leaf, keyed("div", "nest").input("children", new Object[] {"b", nestedLeaf, "c"}), "d"
            });
            final var warning = "Each child in an array or iterator should have a unique \"key\" prop.";
            expectWarnings(production, () -> ReactDOM.render(input, root), warning, warning);
            equal("abcd", root.getTextContent());
            expectWarnings(production, () -> ReactDOM.render(input.dup(), root));
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void nestingFrames(boolean production) {
        final var root = Browser.append("div");
        final var paragraph = Browser.append("p");
        try {
            final var first = keyed("tr", "first");
            final var second = keyed("tr", "second");
            expectWarnings(
                    production,
                    () -> ReactDOM.render(children("div", first, second), root),
                    "<tr> cannot appear as a child of <div>.\n    in tr\n    in div");
            expectWarnings(
                    production,
                    () -> ReactDOM.render(children("span", host("p")), paragraph),
                    "<p> cannot appear as a descendant of <p>.\n    in p\n    in span");

            final var rowWithText = named("Row", host("tr").input("children", "x"));
            final var tableWithText = named("Foo", children("table", rowWithText, ReactNode.of(" ")));
            expectWarnings(
                    production,
                    () -> ReactDOM.render(tableWithText, root),
                    "Add a <tbody> to your code to match the DOM tree generated by the browser."
                            + "\n    in tr\n    in Row\n    in table\n    in Foo",
                    "Text nodes cannot appear as a child of <tr>.\n    in tr\n    in Row\n    in table\n    in Foo",
                    "whitespace between tags on each line of your source code.\n    in table\n    in Foo");

            final var row = named("FancyRow", named("Row", host("tr")));
            final var byTable = named("FancyTable", named("Table", children("table", row)));
            final var simple = children("table", row);
            final var direct = named("FancyTable", named("Table", children("table", host("tr"))));
            final var link = named("Link", children("a", children("div", named("Link", host("a")))));
            final String suffix = "\n    in tr\n    in Row\n    in FancyRow\n    in table";
            final String customTable = "\n    in Table\n    in FancyTable";
            expectWarnings(
                    production,
                    () -> ReactDOM.render(named("App1", named("Viz1", simple)), root),
                    suffix + "\n    in Viz1");
            expectWarnings(
                    production,
                    () -> ReactDOM.render(named("App2", named("Viz2", byTable)), root),
                    suffix + customTable + "\n    in Viz2");
            expectWarnings(production, () -> ReactDOM.render(byTable, root), suffix + customTable);
            expectWarnings(production, () -> ReactDOM.render(simple, root), suffix);
            expectWarnings(production, () -> ReactDOM.render(direct, root), "\n    in tr\n    in table" + customTable);
            expectWarnings(
                    production,
                    () -> ReactDOM.render(link, root),
                    "\n    in a\n    in Link\n    in div\n    in a\n    in Link");
        } finally {
            close(root);
            close(paragraph);
        }
    }

    @JsMethod
    public static void propertyWarningFrames(boolean production) {
        final var root = Browser.append("div");
        final ReactEventHandler handler = event -> {};
        try {
            warnedAttribute(root, production, "input", "tabindex", "1", "Did you mean `tabIndex`?\n    in input");
            warnedAttribute(root, production, "input", "oninput", "1", "Did you mean `onInput`?\n    in input");
            warnedAttribute(root, production, "input", "onKeydown", "1", "Did you mean `onKeyDown`?\n    in input");
            for (final var key : new String[] {"onFocusIn", "onFocusOut", "onfocusin", "onfocusout"}) {
                warnedAttribute(
                        root,
                        production,
                        "div",
                        key,
                        handler,
                        "React uses onFocus and onBlur instead of onFocusIn and onFocusOut.");
            }
            warnedAttribute(root, production, "div", "cLaSs", "paladin", "Did you mean `className`?\n    in div");
            warnedAttribute(root, production, "input", "onclick", "1", "Did you mean `onClick`?\n    in input");

            ReactDOM.render(host("div").input("className", "paladin"), root);
            warnedAttribute(root, production, "div", "ClAsS", "paladin", "Did you mean `className`?\n    in div");

            final var exact = children(
                    "div",
                    ReactElement.createHostElement("span", "1", null, JsPropertyMap.of("ClaSS", "target")),
                    ReactElement.createHostElement("div", "2", null, JsPropertyMap.of("onClick", handler)),
                    ReactElement.createHostElement("strong", "3", null, JsPropertyMap.of("onCLICK", handler)));
            expectWarnings(
                    production,
                    () -> ReactDOM.render(exact, root),
                    "Did you mean `className`?\n    in span",
                    "Did you mean `onClick`?\n    in strong");

            final var first = named("Child1", host("span").input("clasS", "child"));
            final var third = named("Child3", host("strong").input("ONclick", handler));
            first.setKey("1");
            third.setKey("3");
            expectWarnings(
                    production,
                    () -> ReactDOM.render(named("Parent", children("div", first, third)), root),
                    "Did you mean `className`?\n    in span",
                    "Did you mean `onClick`?\n    in strong");

            warnedAttribute(root, production, "label", "for", "field", "Did you mean `htmlFor`?\n    in label");
            warnedAttribute(root, production, "input", "autofocus", true, "Did you mean `autoFocus`?\n    in input");
            expectWarnings(
                    production,
                    () -> ReactDOM.render(children("div", host("div")).input("contentEditable", true), root),
                    "A component is `contentEditable` and contains `children`");
        } finally {
            close(root);
        }
    }

    private static ReactElement keyed(String tag, String key) {
        return ReactElement.createHostElement(tag, key, null, JsPropertyMap.of());
    }

    private static ReactElement named(String name, ReactNode node) {
        final ViewConstructorFunction constructor = inputs -> new NativeView(inputs) {
            @Override
            public ReactNode render() {
                return node;
            }
        };
        Objects.requireNonNull(Js.asPropertyMap(constructor)).set("displayName", name);
        return ReactElement.createViewElement(constructor);
    }

    @JsMethod
    public static void attributeDiagnostics(boolean production) {
        final var root = Browser.append("div");
        final ReactEventHandler handler = event -> {};
        try {
            warnedAttribute(root, production, "div", "foo", handler, "Invalid value for prop `foo` on <div>");
            expectWarnings(
                    production,
                    () -> ReactDOM.render(host("div").input("foogroup", handler).input("baz", handler), root),
                    "Invalid values for props `foogroup`, `baz` on <div>");
            for (final var vector : new Object[][] {
                {"onUnknown", "ignored"}, {"onunknown", "ignored"}, {"on-unknown", "ignored"},
                {"onUnknownFunction", handler}, {"onunknownfunction", handler}, {"on-unknown-function", handler}
            }) {
                final var key = (String) vector[0];
                warnedAttribute(
                        root,
                        production,
                        "div",
                        key,
                        vector[1],
                        "Unknown event handler property `" + key + "`. It will be ignored.");
                final var div = Browser.query(root, "div");
                attribute(div, key, false);
                property(div, key, Js.undefined());
            }

            warnedAttribute(
                    root,
                    production,
                    "div",
                    "CHILDREN",
                    "5",
                    "Invalid DOM property `CHILDREN`. Did you mean `children`?");
            attribute(Browser.query(root, "div"), "CHILDREN", "5");
            warnedAttribute(
                    root,
                    production,
                    "div",
                    "innerhtml",
                    "<span>Hi Jim!</span>",
                    "Directly setting property `innerHTML` is not permitted.");
            warnedAttribute(
                    root,
                    production,
                    "div",
                    "aria",
                    "hello",
                    "The `aria` attribute is reserved for future use in React.");
            warnedAttribute(
                    root, production, "button", "is", handler, "Received a `function` for a string attribute `is`.");
            ReactDOM.render(host("button").input("is", "test").input("cowabunga", "chevynova"), root);
            attribute(Browser.query(root, "button"), "cowabunga", true);

            for (final var name : new String[] {"class", "cLASS"}) {
                warnedAttribute(
                        root,
                        production,
                        "div",
                        name,
                        "test",
                        "Invalid DOM property `" + name + "`. Did you mean `className`?");
                property(Browser.query(root, "div"), "className", "test");
                ReactDOM.render(host("div").input("is", "custom-element").input(name, "test"), root);
                attribute(Browser.query(root, "div"), "class", "test");
            }
            expectWarnings(
                    production,
                    () -> ReactDOM.render(children("svg", host("text").input("arabic-form", "isolated")), root),
                    "Invalid DOM property `arabic-form`. Did you mean `arabicForm`?");
            attribute(Browser.query(root, "text"), "arabic-form", true);

            for (final var name : new String[] {"whatever", "unknownflag", "stringflag"}) {
                warnedAttribute(
                        root,
                        production,
                        "div",
                        name,
                        true,
                        "Received `true` for a non-boolean attribute `" + name + "`.");
                attribute(Browser.query(root, "div"), name, false);
            }
            warnedAttribute(
                    root, production, "div", "callbackprop", handler, "Invalid value for prop `callbackprop` on <div>");
            attribute(Browser.query(root, "div"), "callbackprop", false);
            ReactDOM.render(host("div").input("mutableprop", Browser.number(0)), root);
            warnedAttribute(
                    root, production, "div", "mutableprop", handler, "Invalid value for prop `mutableprop` on <div>");
            attribute(Browser.query(root, "div"), "mutableprop", false);
            warnedAttribute(
                    root,
                    production,
                    "div",
                    "invalidnumber",
                    Browser.number(Double.NaN),
                    "Received NaN for the `invalidnumber` attribute.");
            attribute(Browser.query(root, "div"), "invalidnumber", "NaN");
            for (final var attribute : new String[] {"data-fooBar", "fooBar"}) {
                final var lower = attribute.toLowerCase(Locale.ROOT);
                warnedAttribute(root, production, "div", attribute, "true", "spell it as lowercase `" + lower + "`");
                attribute(Browser.query(root, "div"), lower, "true");
            }
            warnedAttribute(root, production, "div", "SiZe", "30", "Invalid DOM property `SiZe`. Did you mean `size`?");
            attribute(Browser.query(root, "div"), "size", "30");

            warnedAttribute(
                    root,
                    production,
                    "div",
                    "hidden",
                    "false",
                    "Received the string `false` for the boolean attribute `hidden`.");
            attribute(Browser.query(root, "div"), "hidden", "");
            warnedAttribute(
                    root,
                    production,
                    "div",
                    "disabled",
                    "true",
                    "Received the string `true` for the boolean attribute `disabled`.");
            attribute(Browser.query(root, "div"), "disabled", "");
        } finally {
            close(root);
        }
    }

    private static void warnedAttribute(
            Browser.Element root, boolean production, String tag, String name, @Nullable Object value, String message) {
        expectWarnings(production, () -> ReactDOM.render(host(tag).input(name, value), root), message);
    }

    @JsMethod
    public static void attributeObjectsAndBooleans(boolean production) {
        final var root = Browser.append("div");
        try {
            final var object = JsPropertyMap.of("object", "value");
            ReactDOM.render(host("form").input("acceptCharset", object), root);
            attribute(Browser.query(root, "form"), "accept-charset", "[object Object]");
            ReactDOM.render(host("svg").input("arabicForm", object), root);
            attribute(Browser.query(root, "svg"), "arabic-form", "[object Object]");
            ReactDOM.render(host("div").input("unknown", object), root);
            final var div = Browser.query(root, "div");
            attribute(div, "unknown", "[object Object]");
            final Stringifier stringify = () -> "about:blank#hello";
            final var ownMethod = JsPropertyMap.of("toString", stringify);
            ReactDOM.render(host("img").input("src", ownMethod), root);
            property(Browser.query(root, "img"), "src", "about:blank#hello");
            ReactDOM.render(host("svg").input("arabicForm", ownMethod), root);
            attribute(Browser.query(root, "svg"), "arabic-form", "about:blank#hello");
            ReactDOM.render(host("div").input("unknown", ownMethod), root);
            attribute(Browser.query(root, "div"), "unknown", "about:blank#hello");
            ReactDOM.render(host("img").input("src", inherits(ownMethod)), root);
            property(Browser.query(root, "img"), "src", "about:blank#hello");

            expectWarnings(
                    production,
                    () -> ReactDOM.render(children("svg", host("font-face").input("x-height", "2")), root),
                    "Invalid DOM property `x-height`. Did you mean `xHeight`");
            attribute(Browser.query(root, "font-face"), "x-height", true);
            expectWarnings(
                    production,
                    () -> ReactDOM.render(children("svg", host("font-face").input("fontflag", true)), root),
                    "Received `true` for a non-boolean attribute `fontflag`");
            attribute(Browser.query(root, "font-face"), "fontflag", false);

            ReactDOM.render(host("div").input("checked", true), root);
            final var checked = Browser.query(root, "div");
            final var checkedAgain = host("div").input("checked", true);
            final var unchecked = host("div").input("checked", false);
            final var inputs = new ReactElement[] {checkedAgain, host("div"), unchecked, checkedAgain};
            final int[] expectedWrites = {0, 1, 1, 1};
            for (int i = 0; i < inputs.length; i++) {
                final var next = inputs[i];
                check(
                        expectedWrites[i] == propertyWrites(checked, "checked", () -> ReactDOM.render(next, root)),
                        "Only changed boolean inputs write the host property");
            }
        } finally {
            close(root);
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.create")
    private static native Object inherits(Object prototype);

    @JsMethod
    private static native int propertyWrites(Browser.Element element, String name, Action render);

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
