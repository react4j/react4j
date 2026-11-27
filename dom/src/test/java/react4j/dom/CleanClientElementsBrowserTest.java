package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.Renderable;
import react4j.internal.NativeView;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;

@NullMarked
public final class CleanClientElementsBrowserTest {
    private static final ViewConstructorFunction OUTPUT = Output::new;
    private static final ViewConstructorFunction WRAPPER = Wrapper::new;
    private static final String SVG = "http://www.w3.org/2000/svg";
    private static final String MATH = "http://www.w3.org/1998/Math/MathML";
    private static final String XLINK = "http://www.w3.org/1999/xlink";
    private static final String IMAGE = "data:image/svg+xml,%3Csvg%20xmlns='http://www.w3.org/2000/svg'/%3E";

    private CleanClientElementsBrowserTest() {}

    @JsMethod
    public static void run(boolean production) {
        final var root = Browser.append("div");
        try {
            directAndArrayText(root, production);
            namespaces(root, production);
            markup(root, production);
            trees(root, production);
            specialCharacters(root, production);
            invalidChildren(root);
            invalidTypes(root);
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static void directAndArrayText(Browser.Element root, boolean production) {
        var e = mount(root, production, host("div", "Text"));
        tag(e, "DIV");
        texts(e, "Text");
        texts(mount(root, production, host("div", "  Text ")), "  Text ");
        texts(mount(root, production, host("div", "")));
        texts(mount(root, production, children("div", "", "", "")), "", "", "");
        texts(mount(root, production, children("div", " ", " ", " ")), " ", " ", " ");

        e = mount(root, production, children("div", "Text", host("span", "More Text")));
        count(e, 2);
        text(child(e, 0), "Text");
        final var span = child(e, 1);
        tag(span, "SPAN");
        texts(span, "More Text");

        mount(root, production, host("nonstandard"), "The tag <nonstandard> is unrecognized in this browser.");
        e = mount(root, production, host("nonstandard", "Text"));
        tag(e, "NONSTANDARD");
        texts(e, "Text");
        e = mount(root, production, host("custom-element", "Text"));
        tag(e, "CUSTOM-ELEMENT");
        texts(e, "Text");

        texts(mount(root, production, children("div", "", "foo")), "", "foo");
        texts(mount(root, production, children("div", "foo", "")), "foo", "");
        texts(mount(root, production, children("div", "foo", "bar")), "foo", "bar");
        texts(mount(root, production, children("div", "a", output("b"), "c")), "a", "b", "c");

        final var z = output(null);
        z.setKey("1");
        final var y = output(new Object[] {z, new Object[] {"c"}});
        y.setKey("1");
        final var x = output(new @Nullable Object[] {null, new Object[] {y}, false});
        x.setKey("1");
        e = mount(
                root,
                production,
                children("div", new Object[] {new Object[] {"a"}, "b"}, children("div", x, "d"), "e"));
        count(e, 4);
        text(child(e, 0), "a");
        text(child(e, 1), "b");
        texts(child(e, 2), "c", "d");
        text(child(e, 3), "e");

        equal("3", mount(root, production, host("div", Browser.number(3))).getTextContent());
        equal("0", mount(root, production, host("div", Browser.number(0))).getTextContent());
        texts(mount(root, production, children("div", "foo", Browser.number(40))), "foo", "40");

        for (final var value : new @Nullable Object[] {null, false, Js.undefined(), output(null)}) {
            texts(mount(root, production, host("div", value)));
        }
        texts(mount(root, production, children("div", null, "foo")), "foo");
        texts(mount(root, production, children("div", false, "foo")), "foo");
        texts(mount(root, production, children("div", false, null, "foo", null, false)), "foo");
        texts(mount(root, production, children("div", false, null, null, false)));
    }

    private static void namespaces(Browser.Element root, boolean production) {
        final var svg = mount(root, production, host("svg"));
        namespace(svg, "svg", SVG);
        final var viewBox = mount(root, production, host("svg").input("viewBox", "0 0 0 0"));
        namespace(viewBox, "svg", SVG);
        equal("0 0 0 0", viewBox.getAttribute("viewBox"));

        final var image =
                child(mount(root, production, children("svg", host("image").input("xlinkHref", IMAGE))), 0);
        namespace(image, "image", SVG);
        equal(IMAGE, attributeIn(image, XLINK, "href"));

        final var raw = child(
                mount(
                        root,
                        production,
                        children("svg", host("image").input("xlinkhref", IMAGE)),
                        "Invalid DOM property `xlinkhref`. Did you mean `xlinkHref`?"),
                0);
        check(null == attributeIn(raw, XLINK, "href"), "A legacy alias with incorrect casing has no XLink namespace");
        equal(IMAGE, raw.getAttribute("xlinkhref"));

        final var indexed = mount(root, production, host("svg").input("tabIndex", "1"));
        check(1 == Js.asInt(Browser.property(indexed, "tabIndex")), "tabIndex is reflected through the SVG property");
        final var lowercase = mount(
                root,
                production,
                host("svg").input("tabindex", "1"),
                "Invalid DOM property `tabindex`. Did you mean `tabIndex`?");
        check(1 == Js.asInt(Browser.property(lowercase, "tabIndex")), "The browser reflects the lowercased attribute");

        final var filter = mount(root, production, children("svg", children("filter", host("feMorphology"))));
        namespace(child(child(filter, 0), 0), "feMorphology", SVG);
        namespace(mount(root, production, host("math")), "math", MATH);
    }

    private static void markup(Browser.Element root, boolean production) {
        for (final var tag : new String[] {"img", "button"}) {
            final var e = mount(root, production, host(tag));
            tag(e, "img".equals(tag) ? "IMG" : "BUTTON");
            count(e, 0);
            check(null == Browser.property(e, "nextSibling"), "No further node accompanies the empty host");
        }
        for (final var value : new Object[] {Browser.number(0), false, "hello"}) {
            final var span = child(mount(root, production, children("div", raw("span", value))), 0);
            final var expected = Browser.string(value);
            texts(span, expected);
            equal(expected, span.getTextContent());
        }
        final var markup = "<span id='child'/>";
        final Stringifier stringify = () -> markup;
        for (final var value : new Object[] {markup, JsPropertyMap.of("toString", stringify)}) {
            final var e = mount(root, production, raw("div", value));
            count(e, 1);
            final var child = child(e, 0);
            tag(child, "SPAN");
            equal("child", child.getAttribute("id"));
            count(child, 0);
        }
        for (final var value : new @Nullable Object[] {null, Js.undefined()}) {
            count(mount(root, production, raw("div", value)), 0);
        }
        count(mount(root, production, children("noscript", host("div", "Enable JavaScript to run this app."))), 0);
        equal("Hello", mount(root, production, host("pre", "Hello")).getTextContent());
        for (final var tag : new String[] {"pre", "div"}) {
            equal("\nHello", mount(root, production, host(tag, "\nHello")).getTextContent());
        }
    }

    private static void trees(Browser.Element root, boolean production) {
        final var wrapped = mount(root, production, output(host("div", "foo")));
        texts(wrapped, "foo");

        var inner = mount(root, production, wrap(wrap(wrap(wrap(null)))));
        for (int i = 0; i < 3; i++) {
            tag(inner, "DIV");
            count(inner, 1);
            inner = child(inner, 0);
        }
        tag(inner, "DIV");
        count(inner, 0);

        final var row = mount(root, production, wrap(new ReactElement[] {
            wrap(new ReactElement[] {wrap(null), wrap(null)}), wrap(new ReactElement[] {wrap(null), wrap(null)})
        }));
        tag(row, "DIV");
        count(row, 2);
        for (int i = 0; i < 2; i++) {
            final var branch = child(row, i);
            tag(branch, "DIV");
            count(branch, 2);
            for (int j = 0; j < 2; j++) {
                final var leaf = child(branch, j);
                tag(leaf, "DIV");
                count(leaf, 0);
            }
        }

        final var single = host("div").input("id", "child");
        final var first = host("div").input("id", "child1");
        final var second = host("div").input("id", "child2");
        var e = mount(root, production, host("div", single).input("id", "parent"));
        equal("parent", e.getId());
        count(e, 1);
        childWithId(e, 0, "child");

        e = mount(root, production, children("div", first, second).input("id", "parent"));
        equal("parent", e.getId());
        count(e, 2);
        childWithId(e, 0, "child1");
        childWithId(e, 1, "child2");

        e = mount(root, production, children("div", first, " ", second).input("id", "parent"));
        equal("parent", e.getId());
        count(e, 3);
        childWithId(e, 0, "child1");
        text(child(e, 1), " ");
        childWithId(e, 2, "child2");

        e = mount(root, production, children("div", "  ", single, "   ").input("id", "parent"));
        equal("parent", e.getId());
        count(e, 3);
        text(child(e, 0), "  ");
        childWithId(e, 1, "child");
        text(child(e, 2), "   ");

        mountRoot(root, production, output(new Object[] {
            "a", "b", new Object[] {Js.undefined()}, new Object[] {new Object[] {false, "c"}}
        }));
        texts(root, "a", "b", "c");
    }

    private static void specialCharacters(Browser.Element root, boolean production) {
        texts(mount(root, production, host("div", "<span>Text&quot;</span>")), "<span>Text&quot;</span>");
        texts(
                mount(root, production, children("div", "<span>Text1&quot;</span>", "<span>Text2&quot;</span>")),
                "<span>Text1&quot;</span>",
                "<span>Text2&quot;</span>");

        final var special = "foo\rbar\r\nbaz\nqux\0";
        texts(mount(root, production, host("div", special)), special);
        texts(mount(root, production, children("div", "foo\rbar", "\r\nbaz\nqux\0")), "foo\rbar", "\r\nbaz\nqux\0");
        final var anchor = mount(root, production, host("a").input("title", special));
        equal(special, Browser.string(Browser.property(anchor, "title")));
    }

    private static void invalidChildren(Browser.Element root) {
        final var undefinedMessage = "UndefinedComponent(...): Nothing was returned from render. This usually means a"
                + " return statement is missing. Or, to render nothing, return null.";
        failure(root, rawOutput(true, "UndefinedComponent", Js.undefined()), undefinedMessage, 152);
        failure(root, rawOutput(false, "UndefinedComponent", Js.undefined()), undefinedMessage, 152);

        final var invalid = JsPropertyMap.of("x", Browser.number(123));
        final var objectMessage = "Objects are not valid as a React child (found: object with keys {x}).";
        failure(root, rawOutput(true, "ObjectComponent", invalid), objectMessage, 31);
        failure(root, rawOutput(false, "ObjectComponent", invalid), objectMessage, 31);
        failure(root, Js.uncheckedCast(invalid), objectMessage, 31);
    }

    private static void invalidTypes(Browser.Element root) {
        final var message = "Element type is invalid: expected a string (for built-in components) or a"
                + " class/function (for composite components) but got: ";
        for (final var type : new @Nullable Object[] {JsPropertyMap.of(), null, Js.undefined()}) {
            final var element = ReactElement.createViewElement(OUTPUT);
            Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(element).set("type", type);
            final var typeName = Js.isTripleEqual(null, type) ? "null" : Js.typeof(type);
            failure(root, element, message + typeName, 130);
        }
    }

    private static ReactElement rawOutput(boolean function, String name, @Nullable Object value) {
        final ViewConstructorFunction type = RawOutput::new;
        final var descriptor = Js.<JsPropertyMap<Object>>uncheckedCast(type);
        descriptor.set("displayName", name);
        descriptor.set("componentClass", type);
        descriptor.set("rawRenderResult", true);
        descriptor.set("functionComponent", function);
        return ReactElement.createViewElement(type).input("value", value);
    }

    private static void failure(Browser.Element root, Renderable value, String message, int stockCode) {
        ReactDOM.unmountComponentAtNode(root);
        EventsBrowserTest.withoutReportedErrors(() -> OriginalHostCasesBrowserTest.captureWarnings(
                () -> DomRootBrowserTest.expectInvariant(message, stockCode, () -> ReactDOM.render(value, root))));
    }

    private static void childWithId(Browser.Element parent, int index, String expected) {
        final var node = child(parent, index);
        equal(expected, node.getId());
        count(node, 0);
    }

    private static void namespace(Browser.Element node, String tag, String namespace) {
        count(node, 0);
        tag(node, tag);
        equal(namespace, Browser.string(Browser.property(node, "namespaceURI")));
    }

    private static void texts(Browser.Element parent, String... expected) {
        count(parent, expected.length);
        for (int i = 0; i < expected.length; i++) {
            text(child(parent, i), expected[i]);
        }
    }

    private static void text(Object node, String expected) {
        check(3 == Js.asInt(Browser.property(node, "nodeType")), "Expected a DOM Text node");
        equal(expected, Browser.string(Browser.property(node, "nodeValue")));
    }

    private static void count(Browser.Element element, int expected) {
        check(expected == childCount(element), "Unexpected DOM child count. Expected " + expected);
    }

    private static void tag(Browser.Element element, String expected) {
        equal(expected, Browser.string(Browser.property(element, "tagName")));
    }

    private static Browser.Element mount(
            Browser.Element root, boolean production, ReactElement input, String... warnings) {
        mountRoot(root, production, input, warnings);
        return Objects.requireNonNull(root.getFirstElementChild());
    }

    private static void mountRoot(Browser.Element root, boolean production, Renderable input, String... expected) {
        ReactDOM.unmountComponentAtNode(root);
        final var warnings = OriginalHostCasesBrowserTest.captureWarnings(() -> ReactDOM.render(input, root));
        check(
                (production ? 0 : expected.length) == warnings.length,
                "Unexpected diagnostics " + String.join(", ", warnings));
        if (!production) {
            for (int i = 0; i < expected.length; i++) {
                check(warnings[i].contains(expected[i]), warnings[i]);
            }
        }
    }

    private static ReactElement host(String tag) {
        return ReactElement.createHostElement(tag, null, null, JsPropertyMap.of());
    }

    private static ReactElement host(String tag, @Nullable Object child) {
        return host(tag).input("children", child);
    }

    private static ReactElement children(String tag, @Nullable Object... children) {
        return host(tag, children);
    }

    private static ReactElement raw(String tag, @Nullable Object markup) {
        return host(tag).input("dangerouslySetInnerHTML", JsPropertyMap.of("__html", markup));
    }

    private static ReactElement output(@Nullable Object value) {
        return ReactElement.createViewElement(OUTPUT).input("value", value);
    }

    private static ReactElement wrap(@Nullable Object children) {
        return ReactElement.createViewElement(WRAPPER).input("children", children);
    }

    @JsMethod
    private static native Browser.Element child(Browser.Element node, int index);

    @JsMethod
    private static native int childCount(Browser.Element element);

    @JsMethod
    private static native @Nullable String attributeIn(Browser.Element node, String namespace, String name);

    @JsFunction
    private interface Stringifier {
        String stringify();
    }

    private static final class Output extends NativeView {
        @JsConstructor
        private Output(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            final var value = requireInputs().get("value");
            return null == value ? null : Js.uncheckedCast(value);
        }
    }

    private static final class RawOutput extends NativeView {
        @JsConstructor
        private RawOutput(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            return Js.uncheckedCast(requireInputs().get("value"));
        }
    }

    private static final class Wrapper extends NativeView {
        @JsConstructor
        private Wrapper(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return host("div", requireInputs().get("children"));
        }
    }
}
