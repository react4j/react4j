package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.Locale;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.ReactTestUtil;
import react4j.Renderable;
import react4j.dom.events.ReactEventHandler;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;

@NullMarked
public final class CleanClientElementsBrowserTest {
    private static final ViewConstructorFunction OUTPUT = Output::new;
    private static final ViewConstructorFunction TOGGLE = Toggle::new;
    private static final ViewConstructorFunction WRAPPER = Wrapper::new;
    private static final ViewConstructorFunction REF_OWNER = RefOwner::new;
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

    @JsMethod
    public static void basic(boolean production) {
        final var root = Browser.append("div");
        try {
            tag(mount(root, production, host("div")), "DIV");
            tag(mount(root, production, host("br")), "BR");
            final var parent = mount(root, production, host("div", host("br")));
            count(parent, 1);
            tag(child(parent, 0), "BR");

            for (final var value : new Object[] {"Hello", Browser.number(42), Browser.number(0)}) {
                mountRoot(root, production, Js.uncheckedCast(value));
                text(child(root, 0), Browser.string(value));
            }

            mountRoot(root, production, Js.uncheckedCast(new ReactElement[] {keyed("div", "1", "text1")}));
            tag(child(root, 0), "DIV");

            final var header = output(host("p", "header"));
            header.setKey("3");
            final var footer = output(new ReactElement[] {keyed("h2", "1", "footer"), keyed("h3", "2", "about")});
            footer.setKey("4");
            mountRoot(root, production, Js.uncheckedCast(new ReactElement[] {
                keyed("div", "1", "text1"), keyed("span", "2", "text2"), header, footer
            }));
            tags(root, "DIV", "SPAN", "P", "H2", "H3");

            mountRoot(root, production, Js.uncheckedCast(new Object[] {
                new ReactElement[] {keyed("div", "1", "text1")},
                keyed("span", "1", "text2"),
                new Object[] {new @Nullable Object[] {null, keyed("p", "1", null)}, false}
            }));
            tags(root, "DIV", "SPAN", "P");

            final var iterable = DomRootBrowserTest.iterable(
                    new ReactElement[] {keyed("div", "1", null), keyed("div", "2", null), keyed("div", "3", null)},
                    false);
            mountRoot(root, production, Js.uncheckedCast(iterable));
            tags(root, "DIV", "DIV", "DIV");

            equal("", mount(root, production, host("div", "")).getTextContent());
            for (final var value : new @Nullable Object[] {
                new Object[0],
                false,
                true,
                Js.undefined(),
                new @Nullable Object[] {new Object[][] {{false}}, Js.undefined()}
            }) {
                mountRoot(root, production, Js.uncheckedCast(value));
                count(root, 0);
            }
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    public static void nestedClickOnMount(boolean production) {
        final var root = Browser.append("div");
        try {
            final var trace = new StringBuilder();
            final ViewConstructorFunction view = ClickDuringMount::new;
            mountRoot(root, production, ReactElement.createViewElement(view).input("trace", trace));
            equal("first second ", trace.toString());
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static final class ClickDuringMount extends NativeView implements OnComponentDidMount {
        private Browser.@Nullable Element _first;
        private Browser.@Nullable Element _second;

        @JsConstructor
        private ClickDuringMount(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var trace =
                    (StringBuilder) Objects.requireNonNull(requireInputs().get("trace"));
            final ReactEventHandler first = event -> {
                trace.append("first ");
                Objects.requireNonNull(_second).click();
            };
            final ReactEventHandler second = event -> trace.append("second ");
            final Ref firstRef = node -> _first = Js.uncheckedCast(node);
            final Ref secondRef = node -> _second = Js.uncheckedCast(node);
            return children(
                    "div",
                    ReactElement.createHostElement("div", "a", firstRef, JsPropertyMap.of("onClick", first)),
                    ReactElement.createHostElement("div", "b", secondRef, JsPropertyMap.of("onClick", second)));
        }

        @Override
        public void componentDidMount() {
            Objects.requireNonNull(_first).click();
        }
    }

    @JsMethod
    public static void hostReplacementAndQueries() {
        final var root = Browser.append("div");
        try {
            for (final var name :
                    new String[] {"button", "form", "iframe", "img", "input", "option", "select", "textarea"}) {
                ReactDOM.render(host(name), root);
                final var element = root.getFirstElementChild();
                tag(element, name.toUpperCase(Locale.ROOT));
                check(1 == Js.asInt(Browser.property(element, "nodeType")), "A wrapped host is a DOM element");
            }
            final var bird = keyed("div", "theBird", null).input("className", "bird");
            final var dog = keyed("div", "theDog", null).input("className", "dog");
            ReactDOM.render(children("div", dog, bird), root);
            final var retired = Browser.query(root, ".dog");
            ReactDOM.render(children("div", dog.dup().input("className", "dogbeforedelete"), bird), root);
            check(retired == Browser.query(root, ".dogbeforedelete"), "Same key updates the mounted host");
            ReactDOM.render(children("div", bird), root);
            check(null == Browser.property(retired, "parentNode"), "The old host is detached");
            ReactDOM.render(children("div", dog, bird), root);
            final var active = Browser.query(root, ".dog");
            check(active != retired, "A remounted key allocates a new host");
            ReactDOM.render(children("div", dog.dup().input("className", "bigdog"), bird), root);
            check(active == Browser.query(root, ".bigdog"), "The attribute mutation reaches the replacement host");
            equal("bigdog", active.getAttribute("class"));

            ReactDOM.render(
                    output(output(children(
                            "section",
                            "orangepurple",
                            host("span", "orange").input("className", "red\nlarge"),
                            host("span", "purple").input("className", "other"),
                            host("hr"),
                            output(host("hr"))))),
                    root);
            check(0 == Browser.queryAll(root, ".missing").length, "Text nodes are excluded from host class matches");
            final var match = Browser.queryAll(root, ".red.large");
            check(1 == match.length, "A newline separates DOM class tokens");
            check(match[0] == Browser.query(root, ".large.red"), "Class order preserves node identity");
            check(0 == Browser.queryAll(root, ".red.other").length, "Partial matches are excluded");
            final var list = Browser.queryAll(root, "span");
            check(2 == list.length, "Both nested hosts are discoverable");
            equal("orange", list[0].getTextContent());
            equal("purple", list[1].getTextContent());
            check(2 == Browser.queryAll(root, "hr").length, "Composite boundaries do not hide matching tags");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    public static void emptyComponents(boolean production) {
        final var root = Browser.append("div");
        try {
            for (final var empty : new @Nullable Object[] {null, false}) {
                mountRoot(root, production, output(empty));
                count(root, 0);
            }
            failure(
                    root,
                    rawOutput(false, "Component", Js.undefined()),
                    "Component(...): Nothing was returned from render.",
                    152);
            final var trace = new StringBuilder();
            final Ref observe = host -> trace.append(null == host ? "-" : Browser.property(host, "tagName"))
                    .append(' ');
            for (final var name : new String[] {"div", "script"}) {
                mountRoot(root, production, toggle(null, name, observe));
                mountRoot(root, production, toggle(name, null, observe));
                equal(
                        "- " + name.toUpperCase(Locale.ROOT) + " " + name.toUpperCase(Locale.ROOT) + " - ",
                        trace.toString());
                trace.setLength(0);
            }

            mountRoot(
                    root,
                    production,
                    children("div", keyedToggle("a", observe), keyedToggle("b", observe), keyedToggle("c", observe)));
            equal("- - - DIV DIV DIV ", trace.toString());
            trace.setLength(0);

            mountRoot(root, production, toggle("div", "nested-empty", observe));
            mountRoot(root, production, toggle("nested-empty", "div", observe));
            equal("DIV - - DIV ", trace.toString());
            trace.setLength(0);

            final Ref note = node -> trace.append(null == node ? "unmount " : "mount ");
            final var child = ReactElement.createHostElement("span", null, note, JsPropertyMap.of());
            ReactDOM.render(output(child), root);
            tag(child(root, 0), "SPAN");
            ReactDOM.render(output(null), root);
            count(root, 0);
            ReactDOM.render(output(child), root);
            tag(child(root, 0), "SPAN");
            equal("mount unmount mount ", trace.toString());

            final ViewConstructorFunction parent = ForceOnChildMount::new;
            mountRoot(root, production, ReactElement.createViewElement(parent));
            equal("hello world", root.getTextContent());

            mountRoot(root, production, Js.uncheckedCast(null));
            equal("", root.getTextContent());
            mountRoot(root, production, output(null));
            check(null == Browser.property(root, "firstChild"), "An empty mount creates no placeholder");
            ReactDOM.render(output(null), root);
            check(null == Browser.property(root, "firstChild"), "An empty update creates no placeholder");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    public static void minimalism(boolean production) {
        final var root = Browser.append("div");
        try {
            for (final var text : new String[] {"A string child", "Interpolated String Child"}) {
                final var e = mount(root, production, output(output(host("div", text))));
                tag(e, "DIV");
                texts(e, text);
            }
            final var e = mount(root, production, output(output(host("div", host("ul", "list text")))));
            tag(e, "DIV");
            count(e, 1);
            final var ul = child(e, 0);
            tag(ul, "UL");
            texts(ul, "list text");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static ReactElement keyedToggle(String key, Ref observe) {
        final var element = toggle(null, "div", observe);
        element.setKey(key);
        return element;
    }

    private static ReactElement toggle(@Nullable String first, @Nullable String second, Ref observe) {
        return ReactElement.createViewElement(TOGGLE)
                .input("first", first)
                .input("second", second)
                .input("observe", observe);
    }

    private static final ViewConstructorFunction MOUNT_NOTIFIER = MountNotifier::new;

    private static final class MountNotifier extends NativeView implements OnComponentDidMount {
        @JsConstructor
        private MountNotifier(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return host("div", "hello world");
        }

        @Override
        public void componentDidMount() {
            Js.<Ref>uncheckedCast(Objects.requireNonNull(requireInputs().get("observe")))
                    .accept(null);
        }
    }

    private static final class ForceOnChildMount extends NativeView {
        @JsConstructor
        private ForceOnChildMount(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var observe = (Ref) ignored -> forceUpdate();
            final var before = output(null);
            before.setKey("1");
            final var during = ReactElement.createViewElement(MOUNT_NOTIFIER).input("observe", observe);
            during.setKey("0");
            final var after = output(null);
            after.setKey("2");
            return children("div", before, during, after);
        }
    }

    private static final class Toggle extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        private @Nullable Object _host;
        private final Ref _ref = node -> _host = node;

        @JsConstructor
        private Toggle(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            Js.<JsPropertyMap<Object>>uncheckedCast(this).set("state", JsPropertyMap.of("changed", false));
        }

        @Override
        public @Nullable ReactNode render() {
            final var key = Boolean.TRUE.equals(Objects.requireNonNull(state()).get("changed")) ? "second" : "first";
            final var type = requireInputs().get(key);
            if (null == type) {
                return null;
            }
            if ("nested-empty".equals(type)) {
                return output(output(null));
            }
            return ReactElement.createHostElement(Js.asString(type), null, _ref, JsPropertyMap.of());
        }

        @Override
        public void componentDidMount() {
            observe();
            setState(JsPropertyMap.of("changed", true));
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            observe();
        }

        private void observe() {
            Js.<Ref>uncheckedCast(Objects.requireNonNull(requireInputs().get("observe")))
                    .accept(_host);
        }
    }

    @JsMethod
    public static void specialTypes(boolean production) {
        final var root = Browser.append("div");
        final var invariants = React.shouldCheckInvariants();
        ReactTestUtil.noCheckInvariants();
        try {
            final var forwarded = JsPropertyMap.<@Nullable Object>of("current", null);
            final var host = mount(root, production, forward("Test", forwarded));
            tag(host, "DIV");
            equal("Test", host.getTextContent());
            check(host == forwarded.get("current"), "Forwarding resolves the committed host");

            final var profiled = mount(
                    root,
                    production,
                    typed(symbol("react.profiler"), null)
                            .input("id", "profiler")
                            .input("onRender", (Profile) (id, phase) -> {})
                            .input("children", host("div", "Test")));
            tag(profiled, "DIV");
            equal("Test", profiled.getTextContent());

            final var trace = new StringBuilder();
            final Yield yield = text -> trace.append(text).append(' ');
            final ViewConstructorFunction counter = constructable(MemoCounter::new);
            final var basic = memo(counter, null);
            equal(
                    "Count: 0",
                    mount(
                                    root,
                                    production,
                                    typed(basic, null)
                                            .input("count", Browser.number(0))
                                            .input("yield", yield))
                            .getTextContent());
            trace.setLength(0);

            final Forward forward = (inputs, ref) -> ReactElement.createViewElement(counter)
                    .input(
                            "count",
                            Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(ref)
                                    .get("current"))
                    .input("yield", inputs.get("yield"));
            final var wrapper = JsPropertyMap.of("$$typeof", symbol("react.forward_ref"), "render", forward);
            final var count = JsPropertyMap.of("current", Browser.number(0));
            mount(root, production, typed(memo(wrapper, null), count).input("yield", yield));
            equal("Count: 0 ", trace.toString());
            trace.setLength(0);

            for (final var logComparator : new boolean[] {false, true}) {
                final Compare compare = (previous, next) -> {
                    if (logComparator) {
                        trace.append("comparator ");
                    }
                    return false;
                };
                mount(
                        root,
                        production,
                        typed(memo(counter, compare), null)
                                .input("count", Browser.number(0))
                                .input("yield", yield));
                equal("Count: 0 ", trace.toString());
                trace.setLength(0);
            }
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
            if (invariants) {
                ReactTestUtil.checkInvariants();
            }
        }
    }

    @JsMethod
    public static void clientRefs(boolean production) {
        final var root = Browser.append("div");
        final var invariants = React.shouldCheckInvariants();
        ReactTestUtil.noCheckInvariants();
        try {
            final var calls = new int[1];
            final var received = new Object[1];
            final Ref callback = element -> {
                if (null != element) {
                    calls[0]++;
                    received[0] = element;
                }
            };
            final var node = mount(
                    root, production, ReactElement.createViewElement(REF_OWNER).input("refValue", callback));
            check(1 == calls[0], "Client mounting attaches exactly one callback ref");
            check(null != received[0], "The callback receives a host");
            check(node == received[0], "The callback receives the rendered DOM element");

            ReactDOM.unmountComponentAtNode(root);
            final var owner = Objects.requireNonNull(
                    ReactDOM.render(ReactElement.createViewElement(REF_OWNER).input("refValue", "myDiv"), root));
            final var refs = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(Browser.property(owner, "refs"));
            check(
                    root.getFirstElementChild() == refs.get("myDiv"),
                    "The component owns a ref to the root's first child");

            final var forwarded = JsPropertyMap.<@Nullable Object>of("current", null);
            mount(root, production, forward("hello", forwarded));
            final var current = Js.<Browser.Element>uncheckedCast(Objects.requireNonNull(forwarded.get("current")));
            equal("hello", current.getTextContent());
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
            if (invariants) {
                ReactTestUtil.checkInvariants();
            }
        }
    }

    @JsMethod
    public static void progress(boolean production) {
        final var root = Browser.append("div");
        try {
            final var initial = mount(root, production, host("progress").input("value", null));
            check(!initial.hasAttribute("value"), "A null value leaves progress indeterminate");
            equal(
                    "50",
                    mount(root, production, host("progress").input("value", Browser.number(50)))
                            .getAttribute("value"));
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    private static void tags(Browser.Element parent, String... names) {
        count(parent, names.length);
        for (var i = 0; i < names.length; i++) {
            tag(child(parent, i), names[i]);
        }
    }

    private static ReactElement keyed(String tag, String key, @Nullable Object value) {
        return ReactElement.createHostElement(tag, key, null, JsPropertyMap.of("children", value));
    }

    private static ReactElement typed(Object type, @Nullable Object ref) {
        final var element = ReactElement.createViewElement(OUTPUT);
        final var record = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(element);
        record.set("type", type);
        record.set("ref", ref);
        return element;
    }

    private static ReactElement forward(String label, Object ref) {
        final Forward render = (inputs, forwarded) -> ReactElement.createViewElement(REF_OWNER)
                .input("refValue", forwarded)
                .input("text", inputs.get("label"));
        return typed(JsPropertyMap.of("$$typeof", symbol("react.forward_ref"), "render", render), ref)
                .input("label", label);
    }

    private static Object memo(Object type, @Nullable Compare compare) {
        return JsPropertyMap.of("$$typeof", symbol("react.memo"), "type", type, "compare", compare);
    }

    @JsMethod
    private static native Object symbol(String name);

    @JsFunction
    private interface Ref {
        void accept(@Nullable Object value);
    }

    @JsFunction
    private interface Forward {
        ReactNode render(JsPropertyMap<@Nullable Object> inputs, @Nullable Object ref);
    }

    @JsFunction
    private interface Compare {
        boolean matches(JsPropertyMap<@Nullable Object> last, JsPropertyMap<@Nullable Object> next);
    }

    @JsFunction
    private interface Profile {
        void accept(String id, String phase);
    }

    @JsFunction
    private interface Yield {
        void accept(String message);
    }

    private static final class MemoCounter extends NativeView {
        @JsConstructor
        private MemoCounter(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            final var text = "Count: " + Browser.string(Objects.requireNonNull(inputs.get("count")));
            Js.<Yield>uncheckedCast(Objects.requireNonNull(inputs.get("yield"))).accept(text);
            return host("span", text);
        }
    }

    private static final class RefOwner extends NativeView {
        @JsConstructor
        private RefOwner(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            return ReactElement.createHostElement(
                    "div", null, inputs.get("refValue"), JsPropertyMap.of("children", inputs.get("text")));
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

    @JsMethod
    public static void mockedUndefined(boolean production) {
        final var root = Browser.append("div");
        try {
            final var element =
                    rawOutput(false, "MockedComponent", Js.undefined()).input("mockRender", true);
            if (production) {
                failure(root, element, "Nothing was returned from render.", 152);
                failure(root, element.dup(), "Nothing was returned from render.", 152);
            } else {
                ReactDOM.render(element, root);
                count(root, 0);
                ReactDOM.render(element.dup(), root);
                count(root, 0);
            }
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
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
        final ViewConstructorFunction type = constructable(RawOutput::new);
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
    private static native ViewConstructorFunction constructable(ViewConstructorFunction constructor);

    @JsMethod
    private static native void mockRender(NativeView instance);

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
            if (Boolean.TRUE.equals(inputs.get("mockRender"))) {
                mockRender(this);
            }
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
