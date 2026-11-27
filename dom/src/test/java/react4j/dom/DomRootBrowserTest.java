package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.JsError;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.ReactTestUtil;
import react4j.Renderable;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class DomRootBrowserTest {
    private static final ViewConstructorFunction OUTPUT = Output::new;
    private static final ViewConstructorFunction PORTAL_TRACE = PortalTrace::new;
    private static final ViewConstructorFunction REFS = Refs::new;
    private static final ViewConstructorFunction FLIP = Flip::new;
    private static final ViewConstructorFunction KEY_OWNER = KeyOwner::new;
    private static final ViewConstructorFunction STATE_ROW = StateRow::new;
    private static final ViewConstructorFunction NAMESPACE_BOUNDARY = NamespaceBoundary::new;
    private static final ViewConstructorFunction BAILOUT = BailoutLetter::new;
    private static final String HTML = "http://www.w3.org/1999/xhtml";
    private static final String SVG = "http://www.w3.org/2000/svg";
    private static final String MATH = "http://www.w3.org/1998/Math/MathML";

    private DomRootBrowserTest() {}

    @JsMethod
    public static void valuesAndCallbacks() {
        final var root = Browser.append("div");
        try {
            for (final var value : new ReactNode[] {ReactNode.of("foo"), ReactNode.of(10)}) {
                final var expected = Browser.string(value);
                ReactDOM.render(output(host("div").input("children", value)), root);
                equal(expected, root.getTextContent());
                ReactDOM.render(output(value), root);
                equal(expected, root.getTextContent());
            }
            final var host = host("div").input("children", "Foo");
            final var called = new boolean[1];
            ReactDOM.unmountComponentAtNode(root);
            ReactDOM.render(host, root, () -> called[0] = true);
            check(called[0], "The mount callback ran");
            called[0] = false;
            ReactDOM.render(host.dup(), root, () -> called[0] = true);
            check(called[0], "The update callback ran");

            final var view = output(host);
            called[0] = false;
            ReactDOM.render(view, root, () -> called[0] = true);
            check(called[0], "Mounting the view invoked its callback");
            called[0] = false;
            ReactDOM.batchedUpdates(() -> ReactDOM.render(view, root, () -> called[0] = true));
            check(called[0], "Rendering the same element in a batch invoked its callback");

            final var text = ReactNode.of("foo");
            ReactDOM.render(output(text), root);
            final var node = Objects.requireNonNull(Browser.property(root, "firstChild"));
            check(3 == Js.asInt(Browser.property(node, "nodeType")), "A string output uses a DOM text node");
            equal("foo", Browser.string(Browser.property(node, "nodeValue")));
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void fragmentFirstHosts() {
        final var root = Browser.append("div");
        final boolean invariants = React.shouldCheckInvariants();
        ReactTestUtil.noCheckInvariants();
        try {
            final var div = ReactElement.createHostElement("div", "a", null, JsPropertyMap.of());
            final var span = ReactElement.createHostElement("span", "b", null, JsPropertyMap.of());
            final var nested = output(div);
            nested.setKey("nested");
            final var skip = ReactElement.createViewElement(OUTPUT);
            skip.setKey("empty");
            for (final var children :
                    new ReactNode[] {ReactNode.of(div, span), ReactNode.of(nested, span), ReactNode.of(skip, div, span)
                    }) {
                ReactDOM.render(output(children), root);
                check(2 == childCount(root), "A fragment flattens its two materialized children");
                equal("DIV", Browser.string(Browser.property(childNode(root, 0), "tagName")));
                ReactDOM.unmountComponentAtNode(root);
            }

            final var first = ReactDOM.render(output(ReactNode.of(div)), root);
            equal("DIV", Browser.string(Browser.property(childNode(root, 0), "tagName")));
            final var expanded = ReactDOM.render(output(ReactNode.of(span, div)), root);
            check(Browser.same(first, expanded), "Expanding the fragment reuses the component instance");
            equal("SPAN", Browser.string(Browser.property(childNode(root, 0), "tagName")));
        } finally {
            close(root);
            if (invariants) {
                ReactTestUtil.checkInvariants();
            }
        }
    }

    @JsMethod
    public static void emptyFragmentsAndRoots() {
        final var root = Browser.append("div");
        try {
            final var empty = output(ReactElement.createFragment(null));
            final var direct = output(host("div"));
            final var nonEmpty = output(ReactElement.createFragment(null, direct));
            for (final var element : new ReactElement[] {empty, nonEmpty, empty, direct, empty}) {
                ReactDOM.render(element, root);
                if (empty == element) {
                    check(null == Browser.property(root, "firstChild"), "Empty fragment leaves no host node");
                } else {
                    final var child = Objects.requireNonNull(root.getFirstElementChild());
                    equal("DIV", Browser.string(Browser.property(child, "tagName")));
                }
            }
            ReactDOM.render(host("div").input("children", "foo"), root);
            markup(root, "<div>foo</div>");
            ReactDOM.render(ReactNode.of(), root);
            markup(root, "");
            ReactDOM.render(host("div").input("children", "bar"), root);
            markup(root, "<div>bar</div>");
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void destroyRoots(boolean production) {
        final var roots = Browser.append("main");
        final var first = Browser.append("div");
        final var second = Browser.append("div");
        append(roots, first);
        append(roots, second);
        try {
            ReactDOM.render(host("div").input("className", "firstReactDiv"), first);
            ReactDOM.render(host("div").input("className", "secondReactDiv"), second);
            equal("firstReactDiv", Browser.string(Browser.property(Browser.query(first, "div"), "className")));
            equal("secondReactDiv", Browser.string(Browser.property(Browser.query(second, "div"), "className")));
            check(ReactDOM.unmountComponentAtNode(first), "The first root unmounts");
            check(null == Browser.property(first, "firstChild"), "The first container is empty");
            check(ReactDOM.unmountComponentAtNode(second), "The second root unmounts");
            check(null == Browser.property(second, "firstChild"), "The second container is empty");

            ReactDOM.render(tree("div", tree("div", host("div"))), first);
            final var root = Browser.query(first, "div");
            final var nested = Browser.query(root, "div");
            warning(
                    production,
                    root,
                    "You may have accidentally passed in a React root node instead of its container.");
            warning(
                    production,
                    nested,
                    "Instead, have the parent component update its state and rerender in order to remove this"
                            + " component.");
        } finally {
            ReactDOM.unmountComponentAtNode(first);
            ReactDOM.unmountComponentAtNode(second);
            roots.remove();
        }
    }

    @JsMethod
    public static void mountPoints(boolean production) {
        final var root = Browser.append("div");
        final var frame = Browser.append("iframe");
        final boolean invariants = React.shouldCheckInvariants();
        ReactTestUtil.noCheckInvariants();
        try {
            expectInvariant(
                    "unmountComponentAtNode(...): Target container is not a DOM element.",
                    40,
                    () -> ReactDOM.unmountComponentAtNode(Browser.queryAll(root, "div")));
            setMarkup(root, "<b>hellooo</b>");
            check(!ReactDOM.unmountComponentAtNode(root), "Unmanaged markup does not own a React root");
            equal("hellooo", root.getTextContent());
            ReactDOM.render(text("b", "hellooo"), root);
            equal("hellooo", root.getTextContent());
            check(ReactDOM.unmountComponentAtNode(root), "The managed root can be unmounted");
            equal("", root.getTextContent());

            final var first = ReactDOM.render(host("div"), root);
            equal("DIV", Browser.string(Browser.property(Objects.requireNonNull(first), "nodeName")));
            check(first == ReactDOM.render(host("div"), root), "A second render reuses the original host");
            final var span = ReactDOM.render(host("span"), root);
            equal("SPAN", Browser.string(Browser.property(Objects.requireNonNull(span), "nodeName")));

            ReactDOM.unmountComponentAtNode(root);
            final var receivers = new ArrayList<String>();
            final var callback = callbackReceiver(
                    node -> receivers.add(Browser.string(Browser.property(Objects.requireNonNull(node), "nodeName"))));
            ReactDOM.render(host("div"), root, callback);
            ReactDOM.render(host("div"), root, callback);
            ReactDOM.render(host("span"), root, callback);
            ReactDOM.batchedUpdates(() -> ReactDOM.render(host("span"), root, callback));
            ReactDOM.batchedUpdates(() -> ReactDOM.render(host("article"), root, callback));
            check(receivers.equals(List.of("DIV", "DIV", "SPAN", "SPAN", "ARTICLE")), receivers.toString());

            ReactDOM.unmountComponentAtNode(root);
            setMarkup(root, "<div></div>");
            final var unmanaged =
                    OriginalHostCasesBrowserTest.captureWarnings(() -> ReactDOM.render(host("div"), root));
            check(0 == unmanaged.length, "Mounting over an unowned container does not warn");

            final var body = frameBody(frame);
            final var bodyWarnings =
                    OriginalHostCasesBrowserTest.captureWarnings(() -> ReactDOM.render(host("div"), body));
            diagnostic(production, bodyWarnings, "Rendering components directly into document.body is discouraged");
            ReactDOM.unmountComponentAtNode(body);

            ReactDOM.unmountComponentAtNode(root);
            ReactDOM.render(output(tree("div", host("div"))), root);
            final var managed = Browser.query(root, "div");
            final var originalChild = managed.getFirstElementChild();
            try {
                final var warnings =
                        OriginalHostCasesBrowserTest.captureWarnings(() -> ReactDOM.render(host("span"), managed));
                diagnostic(production, warnings, "Replacing React-rendered children with a new root component");
            } finally {
                ReactDOM.unmountComponentAtNode(managed);
                append(managed, originalChild);
            }
        } finally {
            close(root);
            frame.remove();
            if (invariants) {
                ReactTestUtil.checkInvariants();
            }
        }
    }

    @JsMethod
    public static void commentAndFragmentContainers() {
        final var root = Browser.append("div");
        try {
            setMarkup(root, "A<!-- react-mount-point-unstable -->B");
            final var marker = childNode(root, 1);
            check(8 == Js.asInt(Browser.property(marker, "nodeType")), "The mount point is a comment node");
            for (final var keys : new String[] {"aeiou", "yea", ""}) {
                final var children = new ReactElement[keys.length()];
                for (int i = 0; i < children.length; i++) {
                    final var key = keys.substring(i, i + 1);
                    final var character = output(ReactNode.of(key));
                    character.setKey(key);
                    children[i] = character;
                }
                ReactDOM.render(ReactNode.of(children), marker);
                markup(root, "A" + keys + "<!-- react-mount-point-unstable -->B");
            }
            ReactDOM.unmountComponentAtNode(marker);

            setMarkup(root, "");
            final var fragment = fragment();
            ReactDOM.render(text("div", "foo"), fragment);
            try {
                markup(root, "");
                appendNode(root, fragment);
                markup(root, "<div>foo</div>");
            } finally {
                appendNode(fragment, childNode(root, 0));
                ReactDOM.unmountComponentAtNode(fragment);
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void renderDocument() {
        final var container = detached();
        final var document = detachedDocument();
        try {
            container.setTextContent("potato");
            final var textWarnings = OriginalHostCasesBrowserTest.captureWarnings(
                    () -> ReactDOM.render(text("div", "parsnip"), container));
            equal("parsnip", container.getTextContent());
            check(0 == textWarnings.length, "Replacing unmanaged text does not warn");
            final var app =
                    output(tree("html", tree("head", text("title", "Hello World")), text("body", "Hello world")));
            final var warnings = OriginalHostCasesBrowserTest.captureWarnings(() -> ReactDOM.render(app, document));
            markup(Browser.query(document, "body"), "Hello world");
            check(0 == warnings.length, "Mounting a fresh document does not warn");
        } finally {
            ReactDOM.unmountComponentAtNode(container);
        }
    }

    @JsMethod
    public static void iframeTextDocument() {
        final var frame = Browser.append("iframe");
        final var body = frameBody(frame);
        setMarkup(body, "<div></div>");
        final var container = Browser.query(body, "div");
        try {
            final var output = "Hello world";
            final int calls = appended(container, () -> ReactDOM.render(ReactNode.of(output), container));
            final var text = childNode(container, 0);
            equal(output, Browser.string(Browser.property(text, "textContent")));
            final var owner = Browser.property(text, "ownerDocument");
            check(owner != Browser.property(frame, "ownerDocument"), "The text is outside the main document");
            check(owner == Browser.property(body, "ownerDocument"), "The text belongs to the iframe document");
            check(1 == calls, "The renderer appends exactly one text node");
        } finally {
            ReactDOM.unmountComponentAtNode(container);
            frame.remove();
        }
    }

    @JsMethod
    public static void destroyRefs() {
        final var root = Browser.append("div");
        final boolean invariants = React.shouldCheckInvariants();
        ReactTestUtil.noCheckInvariants();
        try {
            for (final var mode : new String[] {"parent", "child", "removeRef"}) {
                final var owner = Objects.requireNonNull(ReactDOM.render(ReactElement.createViewElement(REFS), root));
                final var refs = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(Browser.property(owner, "refs"));
                final var inner = Objects.requireNonNull(refs.get("theInnerDiv"));
                check(1 == Js.asInt(Browser.property(inner, "nodeType")), "The host string ref resolves to an element");
                check(refCount(refs) == 2, "The owner holds its child host and class refs");
                if ("parent".equals(mode)) {
                    ReactDOM.unmountComponentAtNode(root);
                } else {
                    ReactDOM.render(ReactElement.createViewElement(REFS).input("mode", mode), root);
                }
                check(refCount(refs) == 0, "Removing " + mode + " clears both refs");
                ReactDOM.unmountComponentAtNode(root);
            }
        } finally {
            close(root);
            if (invariants) {
                ReactTestUtil.checkInvariants();
            }
        }
    }

    private static int refCount(JsPropertyMap<@Nullable Object> refs) {
        final var count = new int[1];
        refs.forEach(key -> {
            if (Js.isTruthy(refs.get(key))) {
                count[0]++;
            }
        });
        return count[0];
    }

    @JsMethod
    public static void childIdentity() {
        final var root = Browser.append("div");
        final var apple = keyed("apple", "");
        final var banana = keyed("banana", "");
        try {
            ReactDOM.render(output(tree("div", apple, banana)), root);
            final var parent = Browser.query(root, "div");
            final var first = childNode(parent, 0);
            final var second = childNode(parent, 1);
            ReactDOM.render(output(tree("div", banana, apple)), root);
            check(first == childNode(parent, 1), "The first keyed host moves to the second position");
            check(second == childNode(parent, 0), "The second keyed host moves to the first position");

            final var span = host("span");
            final var before = output(tree("a", span));
            before.setKey("wrap1");
            ReactDOM.render(before, root);
            final var oldSpan = Browser.query(root, "span");
            final var after = output(tree("a", span));
            after.setKey("wrap2");
            ReactDOM.render(after, root);
            check(oldSpan != Browser.query(root, "span"), "Changing the owner key remounts its descendants");

            final var one = host("span");
            final var two = host("span");
            final var three = host("span");
            ReactDOM.render(output(tree("div", three, one, two)), root);
            markup(root, "<div><span></span><span></span><span></span></div>");
            ReactDOM.render(output(tree("div", output(tree("div", three, one, two)))), root);
            markup(root, "<div><div><span></span><span></span><span></span></div></div>");
            ReactDOM.render(output(tree("div", ReactNode.of(host("div"), ReactNode.of("second")), span)), root);
            markup(root, "<div><div></div>second<span></span></div>");

            ReactDOM.render(
                    tree("div", span, ReactElement.createHostElement("span", "0", null, JsPropertyMap.of())), root);
            markup(root, "<div><span></span><span></span></div>");
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void keyRefsAndUpdates() {
        final boolean invariants = React.shouldCheckInvariants();
        ReactTestUtil.noCheckInvariants();
        final var attached = Browser.append("div");
        final var detached = detached();
        try {
            for (final var root : new Browser.Element[] {detached, attached}) {
                final var key = "<'WEIRD/&\\key'>";
                final var owner = Objects.requireNonNull(ReactDOM.render(
                        ReactElement.createViewElement(KEY_OWNER).input("keyValue", key), root));
                final var refs = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(Browser.property(owner, "refs"));
                check(null != refs.get("span"), "A key containing punctuation still resolves its ref");
                markup(root, "<div><span></span></div>");
                ReactDOM.unmountComponentAtNode(root);
            }
            final var inertMarkup = "\"<span data-unexpected=\"key\">";
            final var owner = Objects.requireNonNull(ReactDOM.render(
                    ReactElement.createViewElement(KEY_OWNER).input("keyValue", inertMarkup), attached));
            final var refs = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(Browser.property(owner, "refs"));
            check(null != refs.get("span"), "The unusual key still resolves its ref");
            markup(attached, "<div><span></span></div>");
            check(!Browser.exists(attached, "[data-unexpected]"), "A markup-shaped key does not create DOM attributes");
            ReactDOM.unmountComponentAtNode(attached);

            final var first = ReactElement.createHostElement("span", "A", null, JsPropertyMap.of());
            final var second = ReactElement.createHostElement("span", "B", null, JsPropertyMap.of());
            final var view =
                    ReactElement.createViewElement(FLIP).input("first", first).input("second", second);
            final var instance = Js.<Flip>uncheckedCast(Objects.requireNonNull(ReactDOM.render(view, attached)));
            final var parent = Browser.query(attached, "div");
            final var beforeA = childNode(parent, 0);
            final var beforeB = childNode(parent, 1);
            instance.setState(JsPropertyMap.of("flipped", true));
            check(beforeA == childNode(parent, 1), "The first element retains identity across a class state update");
            check(beforeB == childNode(parent, 0), "The second element retains identity across a class state update");
        } finally {
            close(attached);
            close(detached);
            if (invariants) {
                ReactTestUtil.checkInvariants();
            }
        }
    }

    private static void diagnostic(boolean production, String[] warnings, String message) {
        check((production ? 0 : 1) == warnings.length, "Expected diagnostic " + message + " in development");
        if (!production) {
            check(warnings[0].contains(message), warnings[0]);
        }
    }

    static void expectInvariant(String message, int stockCode, Runnable action) {
        try {
            action.run();
        } catch (Throwable error) {
            final var actual = error.getMessage();
            check(null != actual && (actual.contains(message) || actual.contains("Minified React error #" + stockCode + ";")),
                    "Expected " + message + ", observed " + actual);
            return;
        }
        throw new AssertionError("Expected failure " + message);
    }

    @JsFunction
    private interface Receiver {
        void accept(@Nullable Object node);
    }

    @JsMethod
    private static native ReactDOM.RenderCallbackFn callbackReceiver(Receiver receiver);

    @JsMethod
    private static native Browser.Element frameBody(Browser.Element frame);

    @JsMethod
    private static native void setMarkup(Browser.Element element, String markup);

    @JsMethod
    private static native Object childNode(Browser.Element element, int index);

    @JsMethod
    private static native Object fragment();

    @JsMethod
    private static native Object detachedDocument();

    @JsMethod
    private static native void appendNode(Object parent, Object child);

    @JsMethod
    private static native Browser.Element detached();

    @JsMethod
    public static void cleanFragments() {
        final var root = Browser.append("div");
        try {
            checkTags(root, ReactElement.createFragment(null, text("div", "text1")), "DIV");
            checkTags(
                    root,
                    ReactElement.createFragment(
                            null,
                            text("div", "text1"),
                            text("span", "text2"),
                            output(text("p", "header")),
                            output(ReactElement.createFragment(null, text("h2", "footer"), text("h3", "about")))),
                    "DIV",
                    "SPAN",
                    "P",
                    "H2",
                    "H3");
            checkTags(
                    root,
                    ReactElement.createFragment(
                            null,
                            ReactElement.createFragment(null, text("div", "text1")),
                            text("span", "text2"),
                            ReactElement.createFragment(
                                    null,
                                    ReactElement.createFragment(
                                            null,
                                            ReactElement.createFragment(null, null, host("p")),
                                            Js.uncheckedCast(false)))),
                    "DIV",
                    "SPAN",
                    "P");
            final var warnings = OriginalHostCasesBrowserTest.captureWarnings(() -> check(
                    null == ReactDOM.render(ReactElement.createFragment(null), root),
                    "An empty fragment returns no instance"));
            check(0 == warnings.length, "An empty fragment mounts without warnings");
            check(null == Browser.property(root, "firstChild"), "An empty fragment has no DOM children");
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void iterables(boolean production) {
        final var root = Browser.append("div");
        try {
            final ViewConstructorFunction parent = Output::new;
            Objects.requireNonNull(Js.asPropertyMap(parent)).set("displayName", "IterableParent");
            final var map = ReactElement.createViewElement(parent)
                    .input("value", host("div").input("children", map()));
            final var mapWarnings = OriginalHostCasesBrowserTest.captureWarnings(() -> ReactDOM.render(map, root));
            diagnostic(production, mapWarnings, "Using Maps as children is unsupported");
            if (!production) {
                check(
                        mapWarnings[0].contains("in div") && mapWarnings[0].contains("in IterableParent"),
                        mapWarnings[0]);
            }

            final var items = new ReactElement[] {
                ReactElement.createHostElement("h1", "1", null, JsPropertyMap.of("children", "Hello")),
                ReactElement.createHostElement("h1", "2", null, JsPropertyMap.of("children", "World"))
            };
            final ViewConstructorFunction generator = Output::new;
            Objects.requireNonNull(Js.asPropertyMap(generator)).set("displayName", "GeneratorOutput");
            final var view = ReactElement.createViewElement(generator);
            final var generatorWarnings = OriginalHostCasesBrowserTest.captureWarnings(
                    () -> ReactDOM.render(view.dup().input("value", generator(items)), root));
            diagnostic(production, generatorWarnings, "enumerating a generator mutates it");
            if (!production) {
                check(generatorWarnings[0].contains("in GeneratorOutput"), generatorWarnings[0]);
            }
            final var repeated = OriginalHostCasesBrowserTest.captureWarnings(
                    () -> ReactDOM.render(view.dup().input("value", generator(items)), root));
            check(0 == repeated.length, "Generator diagnostic is emitted once");

            for (final boolean modern : new boolean[] {false, true}) {
                final var reusable = iterable(items, modern);
                for (int i = 0; i < 2; i++) {
                    final var warnings = OriginalHostCasesBrowserTest.captureWarnings(
                            () -> ReactDOM.render(output(Js.uncheckedCast(reusable)), root));
                    check(0 == warnings.length, "A fresh iterator for each render is supported");
                    equal("HelloWorld", root.getTextContent());
                }
            }

            final ViewConstructorFunction wrapper = Output::new;
            Objects.requireNonNull(Js.asPropertyMap(wrapper)).set("displayName", "IterableWrapper");
            final var duplicate = new ReactElement[] {keyed("1", ""), keyed("1", "")};
            final var wrapperView = ReactElement.createViewElement(wrapper)
                    .input("value", host("div").input("children", iterable(duplicate, false)));
            final var wrapped = ReactElement.createViewElement(parent).input("value", tree("div", wrapperView));
            final var duplicateWarnings =
                    OriginalHostCasesBrowserTest.captureWarnings(() -> ReactDOM.render(wrapped, root));
            diagnostic(production, duplicateWarnings, "Encountered two children with the same key, `1`");
            if (!production) {
                check(
                        duplicateWarnings[0].contains("in IterableWrapper")
                                && duplicateWarnings[0].contains("in IterableParent"),
                        duplicateWarnings[0]);
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    public static void reconcileComponents() {
        final var root = Browser.append("div");
        final var trace = new ArrayList<String>();
        try {
            checkTrace(trace, 0, 0, 0);
            ReactDOM.render(tree("div", traced(null, "child", trace, host("span"))), root);
            checkTrace(trace, 1, 0, 0);
            ReactDOM.render(tree("div", traced(null, "child", trace, host("span"))), root);
            checkTrace(trace, 1, 1, 0);
            ReactDOM.render(tree("div", host("span")), root);
            checkTrace(trace, 1, 1, 1);
            ReactDOM.unmountComponentAtNode(root);

            trace.clear();
            checkTrace(trace, 0, 0, 0);
            final ViewConstructorFunction owner = inputs -> new NativeView(inputs) {
                @Override
                public ReactNode render() {
                    final var child = requireInputs().get("child");
                    return null == child ? traced(null, "child", trace, host("span")) : Js.uncheckedCast(child);
                }
            };
            ReactDOM.render(ReactElement.createViewElement(owner), root);
            checkTrace(trace, 1, 0, 0);
            ReactDOM.render(
                    ReactElement.createViewElement(owner).input("child", traced(null, "child", trace, host("span"))),
                    root);
            checkTrace(trace, 1, 1, 0);
            ReactDOM.unmountComponentAtNode(root);

            trace.clear();
            checkTrace(trace, 0, 0, 0);
            ReactDOM.render(tree("div", traced("A", "child", trace, host("span"))), root);
            checkTrace(trace, 1, 0, 0);
            ReactDOM.render(tree("div", traced("B", "child", trace, host("span"))), root);
            checkTrace(trace, 2, 0, 1);
            ReactDOM.unmountComponentAtNode(root);

            trace.clear();
            for (final var step : new String[][] {{"A", "orange"}, {"B", "green"}, {"B", "blue"}}) {
                ReactDOM.render(traced(step[0], "root", trace, text("span", step[1])), root);
                markup(Browser.query(root, "span"), step[1]);
                switch (step[1]) {
                    case "orange" -> checkTrace(trace, 1, 0, 0);
                    case "green" -> checkTrace(trace, 2, 0, 1);
                    default -> checkTrace(trace, 2, 1, 1);
                }
            }
            ReactDOM.unmountComponentAtNode(root);

            for (final var text : new String[] {"XKwHomsNjIkBcQWFbiZU", "EHCjpdTUuiybDvhRJwZt"}) {
                final var children = new ReactElement[text.length()];
                for (int i = 0; i < children.length; i++) {
                    final var letter = text.substring(i, i + 1);
                    children[i] = ReactElement.createViewElement(BAILOUT).input("letter", letter);
                    children[i].setKey(letter);
                }
                ReactDOM.render(tree("div", children), root);
                equal(text, root.getTextContent());
            }
        } finally {
            close(root);
        }
    }

    private static void checkTrace(List<String> trace, int mounted, int updated, int unmounted) {
        final String[] lifecycles = {" componentDidMount", " componentDidUpdate", " componentWillUnmount"};
        final int[] expected = {mounted, updated, unmounted};
        for (int i = 0; i < lifecycles.length; i++) {
            int calls = 0;
            for (final var line : trace) {
                if (line.endsWith(lifecycles[i])) {
                    calls++;
                }
            }
            check(expected[i] == calls, "Unexpected lifecycle calls " + trace);
        }
    }

    @JsMethod
    public static void remountIterableChild() {
        final var root = Browser.append("div");
        try {
            for (int kind = 0; kind < 3; kind++) {
                final var ref = JsPropertyMap.<@Nullable Object>of("current", null);
                final var row = ReactElement.createViewElement(STATE_ROW);
                row.setKey("jcw");
                Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(row).set("ref", ref);
                final var one = new ReactElement[] {row};
                final var none = new ReactElement[0];
                ReactDOM.render(host("div").input("children", 0 == kind ? one : iterable(one, 2 == kind)), root);
                final var original = Js.<StateRow>uncheckedCast(Objects.requireNonNull(ref.get("current")));
                original.setState(JsPropertyMap.of("token", new Object()));
                ReactDOM.render(host("div").input("children", 0 == kind ? none : iterable(none, 2 == kind)), root);
                check(null == ref.get("current"), "Removing the keyed row clears its ref");
                ReactDOM.render(host("div").input("children", 0 == kind ? one : iterable(one, 2 == kind)), root);
                final var next = Js.<StateRow>uncheckedCast(Objects.requireNonNull(ref.get("current")));
                check(original.state() != next.state(), "A remounted row gets new state");
                ReactDOM.unmountComponentAtNode(root);
            }
        } finally {
            close(root);
        }
    }

    @JsMethod
    private static native Object generator(ReactElement[] elements);

    @JsMethod
    static native Object iterable(@Nullable ReactElement[] elements, boolean modern);

    @JsMethod
    private static native Object map();

    private static void checkTags(Browser.Element root, ReactElement element, String... expected) {
        final var warnings = OriginalHostCasesBrowserTest.captureWarnings(() -> ReactDOM.render(element, root));
        check(0 == warnings.length, "A fragment mounts without warnings");
        var child = root.getFirstElementChild();
        for (final var tag : expected) {
            equal(tag, Browser.string(Browser.property(child, "tagName")));
            child = child.getNextElementSibling();
        }
        check(null == child, "A fragment contains exactly the expected host elements");
        ReactDOM.unmountComponentAtNode(root);
    }

    private static final Object ELEMENT_NODE = new Object();

    @JsMethod
    public static void mixedTextChildren() {
        final var root = Browser.append("div");
        try {
            final TextCase[] cases = {
                new TextCase(Js.undefined(), new Object[] {}),
                new TextCase(null, new Object[] {}),
                new TextCase(false, new Object[] {}),
                new TextCase(true, new Object[] {}),
                new TextCase(Browser.number(0), "0"),
                new TextCase(Browser.number(1.2), "1.2"),
                new TextCase("", ""),
                new TextCase("foo", "foo"),
                new TextCase(new Object[] {}, new Object[] {}),
                new TextCase(new Object[] {Js.undefined()}, new Object[] {}),
                new TextCase(new Object[] {null}, new Object[] {}),
                new TextCase(new Object[] {false}, new Object[] {}),
                new TextCase(new Object[] {true}, new Object[] {}),
                new TextCase(new Object[] {Browser.number(0)}, new Object[] {"0"}),
                new TextCase(new Object[] {Browser.number(1.2)}, new Object[] {"1.2"}),
                new TextCase(new Object[] {""}, new Object[] {""}),
                new TextCase(new Object[] {"foo"}, new Object[] {"foo"}),
                new TextCase(new Object[] {host("div")}, new Object[] {ELEMENT_NODE}),
                new TextCase(new Object[] {true, Browser.number(0)}, new Object[] {"0"}),
                new TextCase(new Object[] {Browser.number(0), Browser.number(0)}, new Object[] {"0", "0"}),
                new TextCase(new Object[] {Browser.number(1.2), Browser.number(0)}, new Object[] {"1.2", "0"}),
                new TextCase(new Object[] {Browser.number(0), ""}, new Object[] {"0", ""}),
                new TextCase(new Object[] {"foo", Browser.number(0)}, new Object[] {"foo", "0"}),
                new TextCase(new Object[] {Browser.number(0), host("div")}, new Object[] {"0", ELEMENT_NODE}),
                new TextCase(new Object[] {true, Browser.number(1.2)}, new Object[] {"1.2"}),
                new TextCase(new Object[] {Browser.number(1.2), Browser.number(0)}, new Object[] {"1.2", "0"}),
                new TextCase(new Object[] {Browser.number(1.2), Browser.number(1.2)}, new Object[] {"1.2", "1.2"}),
                new TextCase(new Object[] {Browser.number(1.2), ""}, new Object[] {"1.2", ""}),
                new TextCase(new Object[] {"foo", Browser.number(1.2)}, new Object[] {"foo", "1.2"}),
                new TextCase(new Object[] {Browser.number(1.2), host("div")}, new Object[] {"1.2", ELEMENT_NODE}),
                new TextCase(new Object[] {true, ""}, new Object[] {""}),
                new TextCase(new Object[] {"", Browser.number(0)}, new Object[] {"", "0"}),
                new TextCase(new Object[] {Browser.number(1.2), ""}, new Object[] {"1.2", ""}),
                new TextCase(new Object[] {"", ""}, new Object[] {"", ""}),
                new TextCase(new Object[] {"foo", ""}, new Object[] {"foo", ""}),
                new TextCase(new Object[] {"", host("div")}, new Object[] {"", ELEMENT_NODE}),
                new TextCase(new Object[] {true, "foo"}, new Object[] {"foo"}),
                new TextCase(new Object[] {"foo", Browser.number(0)}, new Object[] {"foo", "0"}),
                new TextCase(new Object[] {Browser.number(1.2), "foo"}, new Object[] {"1.2", "foo"}),
                new TextCase(new Object[] {"foo", ""}, new Object[] {"foo", ""}),
                new TextCase(new Object[] {"foo", "foo"}, new Object[] {"foo", "foo"}),
                new TextCase(new Object[] {"foo", host("div")}, new Object[] {"foo", ELEMENT_NODE}),
                new TextCase(new Object[] {true, host("div"), true}, new Object[] {ELEMENT_NODE}),
                new TextCase(
                        new Object[] {Browser.number(1.2), host("div"), Browser.number(1.2)},
                        new Object[] {"1.2", ELEMENT_NODE, "1.2"}),
                new TextCase(new Object[] {"", host("div"), ""}, new Object[] {"", ELEMENT_NODE, ""}),
                new TextCase(new Object[] {"foo", host("div"), "foo"}, new Object[] {"foo", ELEMENT_NODE, "foo"}),
                new TextCase(
                        new Object[] {true, Browser.number(1.2), host("div"), "", "foo"},
                        new Object[] {"1.2", ELEMENT_NODE, "", "foo"}),
                new TextCase(
                        new Object[] {Browser.number(1.2), "", host("div"), "foo", true},
                        new Object[] {"1.2", "", ELEMENT_NODE, "foo"}),
                new TextCase(
                        new Object[] {"", "foo", host("div"), true, Browser.number(1.2)},
                        new Object[] {"", "foo", ELEMENT_NODE, "1.2"}),
                new TextCase(
                        new Object[] {true, Browser.number(1.2), "", host("div"), "foo", true, Browser.number(1.2)},
                        new Object[] {"1.2", "", ELEMENT_NODE, "foo", "1.2"}),
                new TextCase(
                        new Object[] {"", "foo", true, host("div"), Browser.number(1.2), "", "foo"},
                        new Object[] {"", "foo", ELEMENT_NODE, "1.2", "", "foo"}),
                new TextCase(new Object[] {new Object[] {true}, new Object[] {true}}, new Object[] {}),
                new TextCase(
                        new Object[] {new Object[] {Browser.number(1.2)}, new Object[] {Browser.number(1.2)}},
                        new Object[] {"1.2", "1.2"}),
                new TextCase(new Object[] {new Object[] {""}, new Object[] {""}}, new Object[] {"", ""}),
                new TextCase(new Object[] {new Object[] {"foo"}, new Object[] {"foo"}}, new Object[] {"foo", "foo"}),
                new TextCase(
                        new Object[] {new Object[] {host("div")}, new Object[] {host("div")}},
                        new Object[] {ELEMENT_NODE, ELEMENT_NODE}),
                new TextCase(
                        new Object[] {new Object[] {true, Browser.number(1.2), host("div")}, "", "foo"},
                        new Object[] {"1.2", ELEMENT_NODE, "", "foo"}),
                new TextCase(
                        new Object[] {Browser.number(1.2), "", new Object[] {host("div"), "foo", true}},
                        new Object[] {"1.2", "", ELEMENT_NODE, "foo"}),
                new TextCase(
                        new Object[] {"", new Object[] {"foo", host("div"), true}, Browser.number(1.2)},
                        new Object[] {"", "foo", ELEMENT_NODE, "1.2"}),
                new TextCase(
                        new Object[] {
                            true, new Object[] {Browser.number(1.2), "", host("div"), "foo"}, true, Browser.number(1.2)
                        },
                        new Object[] {"1.2", "", ELEMENT_NODE, "foo", "1.2"}),
                new TextCase(
                        new Object[] {"", "foo", new Object[] {true, host("div"), Browser.number(1.2), ""}, "foo"},
                        new Object[] {"", "foo", ELEMENT_NODE, "1.2", "", "foo"}),
                new TextCase(
                        new Object[] {
                            host("div").input("children", new Object[] {true, Browser.number(1.2), host("div")}),
                            "",
                            "foo"
                        },
                        new Object[] {ELEMENT_NODE, "", "foo"}),
                new TextCase(
                        new Object[] {
                            Browser.number(1.2),
                            "",
                            host("div").input("children", new Object[] {host("div"), "foo", true})
                        },
                        new Object[] {"1.2", "", ELEMENT_NODE}),
                new TextCase(
                        new Object[] {
                            "",
                            host("div").input("children", new Object[] {"foo", host("div"), true}),
                            Browser.number(1.2)
                        },
                        new Object[] {"", ELEMENT_NODE, "1.2"}),
                new TextCase(
                        new Object[] {
                            true,
                            host("div").input("children", new Object[] {Browser.number(1.2), "", host("div"), "foo"}),
                            true,
                            Browser.number(1.2)
                        },
                        new Object[] {ELEMENT_NODE, "1.2"}),
                new TextCase(
                        new Object[] {
                            "",
                            "foo",
                            host("div").input("children", new Object[] {true, host("div"), Browser.number(1.2), ""}),
                            "foo"
                        },
                        new Object[] {"", "foo", ELEMENT_NODE, "foo"}),
            };
            for (final var initial : cases) {
                for (final var update : cases) {
                    renderTextCase(root, initial);
                    renderTextCase(root, update);
                    ReactDOM.unmountComponentAtNode(root);
                }
            }
            EventsBrowserTest.withoutReportedErrors(() -> expectInvariant(
                    "Can only set one of",
                    60,
                    () -> ReactDOM.render(
                            host("div")
                                    .input("children", "ghjkl")
                                    .input("dangerouslySetInnerHTML", JsPropertyMap.of("__html", "abcdef")),
                            root)));
            ReactDOM.render(tree("div", tree("h1", host("span"), host("span"))), root);
            ReactDOM.render(tree("div", text("h1", "A")), root);
            markup(root, "<div><h1>A</h1></div>");
            ReactDOM.render(tree("div", tree("h1", ReactNode.of("A"))), root);
            markup(root, "<div><h1>A</h1></div>");
            ReactDOM.render(tree("div", tree("h1", ReactNode.of("A"), ReactNode.of("B"))), root);
            markup(root, "<div><h1>AB</h1></div>");
        } finally {
            close(root);
        }
    }

    private static void renderTextCase(Browser.Element root, TextCase test) {
        ReactDOM.render(host("div").input("children", test.input()), root);
        final var outer = Browser.query(root, "div");
        final var expected = test.expected();
        if (expected instanceof String single) {
            if (single.isEmpty()) {
                check(0 == childCount(outer), "Empty direct text has no DOM node");
            } else {
                check(1 == childCount(outer), "Direct text has one DOM node");
                textNode(childNode(outer, 0), single);
            }
        } else {
            final var children = (Object[]) expected;
            check(children.length == childCount(outer), "Array children preserve host slot count");
            for (int i = 0; i < children.length; i++) {
                final var node = childNode(outer, i);
                if (ELEMENT_NODE == children[i]) {
                    equal("DIV", Browser.string(Browser.property(node, "tagName")));
                } else {
                    textNode(node, (String) children[i]);
                }
            }
        }
    }

    private static void textNode(Object node, String expected) {
        check(3 == Js.asInt(Browser.property(node, "nodeType")), "The child is a text node");
        equal(expected, Browser.string(Browser.property(node, "data")));
    }

    private record TextCase(@Nullable Object input, Object expected) {}

    @JsMethod
    private static native int childCount(Browser.Element element);

    @JsMethod
    private static native int appended(Browser.Element element, BrowserPromise.Action action);

    @JsMethod
    public static void portalNamespaces() {
        final var root = Browser.append("div");
        final var observations = new ArrayList<NamespaceObservation>();
        try {
            checkNamespaces(
                    root,
                    observations,
                    namespace(
                            observations,
                            "svg",
                            SVG,
                            namespace(observations, "image", SVG),
                            ReactDOM.createPortal(namespace(observations, "div", HTML), detached()),
                            namespace(observations, "image", SVG)));
            checkNamespaces(
                    root,
                    observations,
                    namespace(
                            observations,
                            "math",
                            MATH,
                            namespace(observations, "mi", MATH),
                            ReactDOM.createPortal(namespace(observations, "div", HTML), detached()),
                            namespace(observations, "mi", MATH)));
            checkNamespaces(
                    root,
                    observations,
                    namespace(
                            observations,
                            "div",
                            HTML,
                            namespace(observations, "p", HTML),
                            ReactDOM.createPortal(
                                    namespace(observations, "svg", SVG, namespace(observations, "image", SVG)),
                                    detached()),
                            namespace(observations, "p", HTML)));
            checkNamespaces(
                    root,
                    observations,
                    namespace(
                            observations,
                            "svg",
                            SVG,
                            namespace(observations, "image", SVG),
                            ReactDOM.createPortal(namespace(observations, "div", HTML), detached()),
                            namespace(observations, "image", SVG),
                            ReactDOM.createPortal(namespace(observations, "div", HTML), detached()),
                            namespace(observations, "image", SVG)));
            checkNamespaces(
                    root,
                    observations,
                    namespace(
                            observations,
                            "div",
                            HTML,
                            namespace(
                                    observations,
                                    "math",
                                    MATH,
                                    namespace(observations, "mi", MATH),
                                    ReactDOM.createPortal(
                                            namespace(observations, "svg", SVG, namespace(observations, "image", SVG)),
                                            detached())),
                            namespace(observations, "p", HTML)));
            checkNamespaces(
                    root,
                    observations,
                    namespace(
                            observations,
                            "math",
                            MATH,
                            namespace(observations, "mi", MATH),
                            ReactDOM.createPortal(
                                    namespace(
                                            observations,
                                            "svg",
                                            SVG,
                                            namespace(observations, "image", SVG),
                                            namespace(
                                                    observations,
                                                    "foreignObject",
                                                    SVG,
                                                    namespace(observations, "p", HTML),
                                                    namespace(
                                                            observations,
                                                            "math",
                                                            MATH,
                                                            namespace(observations, "mi", MATH)),
                                                    namespace(observations, "p", HTML)),
                                            namespace(observations, "image", SVG)),
                                    detached()),
                            namespace(observations, "mi", MATH)));
            checkNamespaces(
                    root,
                    observations,
                    namespace(
                            observations,
                            "div",
                            HTML,
                            ReactDOM.createPortal(
                                    namespace(
                                            observations,
                                            "svg",
                                            SVG,
                                            ReactDOM.createPortal(namespace(observations, "div", HTML), detached()),
                                            namespace(observations, "image", SVG)),
                                    detached()),
                            namespace(observations, "p", HTML)));
            checkNamespaces(
                    root,
                    observations,
                    namespace(
                            observations,
                            "svg",
                            SVG,
                            namespace(
                                    observations,
                                    "svg",
                                    SVG,
                                    ReactDOM.createPortal(namespace(observations, "div", HTML), detached()),
                                    namespace(observations, "image", SVG)),
                            namespace(observations, "image", SVG)));
            checkNamespaces(
                    root,
                    observations,
                    namespace(
                            observations,
                            "div",
                            HTML,
                            ReactDOM.createPortal(
                                    namespace(observations, "svg", SVG, namespace(observations, "image", SVG)),
                                    detached()),
                            namespace(observations, "p", HTML),
                            namespace(observations, "svg", SVG, namespace(observations, "image", SVG)),
                            namespace(
                                    observations,
                                    "svg",
                                    SVG,
                                    namespace(observations, "svg", SVG, namespace(observations, "image", SVG)),
                                    namespace(observations, "image", SVG)),
                            namespace(observations, "p", HTML)));
            checkNamespaces(
                    root,
                    observations,
                    namespace(
                            observations,
                            "div",
                            HTML,
                            namespace(
                                    observations,
                                    "svg",
                                    SVG,
                                    namespace(
                                            observations,
                                            "svg",
                                            SVG,
                                            namespace(observations, "image", SVG),
                                            ReactDOM.createPortal(
                                                    namespace(
                                                            observations,
                                                            "svg",
                                                            SVG,
                                                            namespace(observations, "image", SVG),
                                                            namespace(
                                                                    observations,
                                                                    "svg",
                                                                    SVG,
                                                                    namespace(observations, "image", SVG)),
                                                            namespace(observations, "image", SVG)),
                                                    detached()),
                                            namespace(observations, "image", SVG),
                                            namespace(
                                                    observations,
                                                    "foreignObject",
                                                    SVG,
                                                    namespace(observations, "p", HTML),
                                                    ReactDOM.createPortal(
                                                            namespace(observations, "p", HTML), detached()),
                                                    namespace(observations, "p", HTML))),
                                    namespace(observations, "image", SVG)),
                            namespace(observations, "p", HTML)));
            checkNamespaces(
                    root,
                    observations,
                    namespace(
                            observations,
                            "div",
                            HTML,
                            namespace(
                                    observations,
                                    "svg",
                                    SVG,
                                    namespace(
                                            observations,
                                            "foreignObject",
                                            SVG,
                                            namespace(observations, "p", HTML),
                                            ReactDOM.createPortal(
                                                    namespace(
                                                            observations,
                                                            "svg",
                                                            SVG,
                                                            namespace(observations, "image", SVG),
                                                            namespace(
                                                                    observations,
                                                                    "svg",
                                                                    SVG,
                                                                    namespace(observations, "image", SVG),
                                                                    namespace(
                                                                            observations,
                                                                            "foreignObject",
                                                                            SVG,
                                                                            namespace(observations, "p", HTML)),
                                                                    ReactDOM.createPortal(
                                                                            namespace(observations, "p", HTML),
                                                                            detached())),
                                                            namespace(observations, "image", SVG)),
                                                    detached()),
                                            namespace(observations, "p", HTML)),
                                    namespace(observations, "image", SVG)),
                            namespace(observations, "p", HTML)));

            final ViewConstructorFunction broken = inputs -> new NativeView(inputs) {
                @Override
                public ReactNode render() {
                    throw new IllegalStateException("Hello");
                }
            };
            final var failure = ReactElement.createViewElement(broken);
            EventsBrowserTest.withoutReportedErrors(() ->
                    RuntimeParityBrowserTest.expectFailure("Hello", () -> ReactDOM.render(tree("svg", failure), root)));
            checkNamespaces(root, observations, namespace(observations, "div", HTML));

            EventsBrowserTest.withoutReportedErrors(() -> checkNamespaces(
                    root,
                    observations,
                    namespace(
                            observations,
                            "svg",
                            SVG,
                            namespace(
                                    observations,
                                    "foreignObject",
                                    SVG,
                                    namespaceBoundary(tree("math", failure), namespace(observations, "p", HTML))),
                            namespace(observations, "image", SVG))));
            checkNamespaces(root, observations, namespace(observations, "div", HTML));

            EventsBrowserTest.withoutReportedErrors(() -> checkNamespaces(
                    root,
                    observations,
                    namespace(
                            observations,
                            "svg",
                            SVG,
                            namespaceBoundary(
                                    ReactDOM.createPortal(tree("div", tree("math", failure)), detached()),
                                    namespace(observations, "image", SVG)),
                            ReactDOM.createPortal(namespace(observations, "div", HTML), detached()))));
        } finally {
            close(root);
        }
    }

    private static ReactElement namespaceBoundary(Renderable children, ReactElement fallback) {
        return ReactElement.createViewElement(NAMESPACE_BOUNDARY)
                .input("children", children)
                .input("fallback", fallback);
    }

    private static ReactElement namespace(
            List<NamespaceObservation> observations, String tag, String uri, Renderable... children) {
        final var captured = new @Nullable Object[1];
        observations.add(new NamespaceObservation(uri, captured));
        final Receiver ref = node -> captured[0] = node;
        return ReactElement.createHostElement(tag, null, ref, JsPropertyMap.of("children", ReactNode.of(children)));
    }

    private static void checkNamespaces(
            Browser.Element root, List<NamespaceObservation> observations, ReactElement element) {
        ReactDOM.render(element, root);
        for (final var observation : observations) {
            final var node = Objects.requireNonNull(observation.captured()[0]);
            equal(observation.uri(), Browser.string(Browser.property(node, "namespaceURI")));
        }
        ReactDOM.unmountComponentAtNode(root);
        markup(root, "");
        observations.clear();
    }

    private record NamespaceObservation(
            String uri, @Nullable Object[] captured) {}

    @JsMethod
    public static void portalCommits() {
        final var root = Browser.append("div");
        final var first = Browser.append("div");
        final var second = Browser.append("div");
        final var third = Browser.append("div");
        try {
            final var trace = new ArrayList<String>();
            for (final var step : new String[] {"a", "b"}) {
                final var children = ReactNode.of(
                        traced("a", "normal[0]:" + step, trace, null),
                        ReactDOM.createPortal(traced("b", "portal1[0]:" + step, trace, null), first),
                        traced("c", "normal[1]:" + step, trace, null),
                        ReactDOM.createPortal(
                                ReactNode.of(
                                        traced("d", "portal2[0]:" + step, trace, null),
                                        traced("e", "portal2[1]:" + step, trace, null)),
                                second));
                ReactDOM.render(traced(null, "Parent:" + step, trace, children), root);
                markup(first, "<div>portal1[0]:" + step + "</div>");
                markup(second, "<div>portal2[0]:" + step + "</div><div>portal2[1]:" + step + "</div>");
                markup(root, "<div>normal[0]:" + step + "</div><div>normal[1]:" + step + "</div>");
                final var phase = "a".equals(step) ? " componentDidMount" : " componentDidUpdate";
                check(
                        trace.equals(List.of(
                                "normal[0]:" + step + phase, "portal1[0]:" + step + phase,
                                "normal[1]:" + step + phase, "portal2[0]:" + step + phase,
                                "portal2[1]:" + step + phase, "Parent:" + step + phase)),
                        trace.toString());
                trace.clear();
            }
            ReactDOM.unmountComponentAtNode(root);
            markup(first, "");
            markup(second, "");
            markup(root, "");
            check(
                    trace.equals(List.of(
                            "Parent:b componentWillUnmount",
                            "normal[0]:b componentWillUnmount",
                            "portal1[0]:b componentWillUnmount",
                            "normal[1]:b componentWillUnmount",
                            "portal2[0]:b componentWillUnmount",
                            "portal2[1]:b componentWillUnmount")),
                    trace.toString());

            ReactDOM.render(
                    ReactNode.of(
                            keyed("a", "normal[0]"),
                            ReactDOM.createPortal(
                                    ReactNode.of(
                                            keyed("b", "portal1[0]"),
                                            ReactDOM.createPortal(keyed("c", "portal2[0]"), second),
                                            ReactDOM.createPortal(keyed("d", "portal3[0]"), third),
                                            keyed("e", "portal1[1]")),
                                    first),
                            keyed("f", "normal[1]")),
                    root);
            markup(first, "<div>portal1[0]</div><div>portal1[1]</div>");
            markup(second, "<div>portal2[0]</div>");
            markup(third, "<div>portal3[0]</div>");
            markup(root, "<div>normal[0]</div><div>normal[1]</div>");
            ReactDOM.unmountComponentAtNode(root);
            for (final var container : new Browser.Element[] {first, second, third, root}) {
                markup(container, "");
            }

            final var outputs = new @Nullable Renderable[] {
                text("div", "portal:1"),
                text("div", "portal:2"),
                text("p", "portal:3"),
                ReactNode.of(ReactNode.of("Hi"), ReactNode.of("Bye")),
                ReactNode.of(ReactNode.of("Bye"), ReactNode.of("Hi")),
                null
            };
            final String[] expected = {
                "<div>portal:1</div>", "<div>portal:2</div>", "<p>portal:3</p>", "HiBye", "ByeHi", ""
            };
            for (int i = 0; i < outputs.length; i++) {
                final var child = ReactNode.of(outputs[i]);
                ReactDOM.render(tree("div", ReactDOM.createPortal(child, first)), root);
                markup(first, expected[i]);
                markup(root, "<div></div>");
            }
        } finally {
            close(root);
            first.remove();
            second.remove();
            third.remove();
        }
    }

    private static ReactElement traced(
            @Nullable String key, String name, List<String> trace, @Nullable ReactNode children) {
        final var element = ReactElement.createViewElement(PORTAL_TRACE)
                .input("name", name)
                .input("trace", trace)
                .input("children", children);
        element.setKey(key);
        return element;
    }

    private static ReactElement keyed(String key, String text) {
        return ReactElement.createHostElement("div", key, null, JsPropertyMap.of("children", text));
    }

    private static ReactElement text(String tag, String text) {
        return host(tag).input("children", text);
    }

    private static void warning(boolean production, Browser.Element element, String text) {
        final var warnings = OriginalHostCasesBrowserTest.captureWarnings(() -> check(
                !ReactDOM.unmountComponentAtNode(element), "Unmount returns false for a host that is not a container"));
        check((production ? 0 : 1) == warnings.length, "Expected the unmount diagnostic only in development");
        if (!production) {
            check(warnings[0].contains("unmountComponentAtNode()") && warnings[0].contains(text), warnings[0]);
        }
    }

    private static ReactElement host(String tag) {
        return ReactElement.createHostElement(tag, null, null, JsPropertyMap.of());
    }

    private static ReactElement tree(String tag, Renderable... children) {
        return host(tag).input("children", ReactNode.of(children));
    }

    private static ReactElement output(ReactNode value) {
        return ReactElement.createViewElement(OUTPUT).input("value", value);
    }

    @JsMethod
    private static native void append(Browser.Element parent, Browser.Element child);

    private static void markup(Browser.Element root, String expected) {
        equal(expected, Browser.string(Browser.property(root, "innerHTML")));
    }

    private static void close(Browser.Element root) {
        ReactDOM.unmountComponentAtNode(root);
        root.remove();
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

    private static final class Refs extends NativeView {
        @JsConstructor
        private Refs(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var mode = requireInputs().get("mode");
            if ("child".equals(mode)) {
                return host("div");
            }
            final var hasRefs = !"removeRef".equals(mode);
            final var child = ReactElement.createViewElement(OUTPUT);
            Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(child)
                    .set("ref", hasRefs ? "theInnerClassComponent" : null);
            return tree(
                    "div",
                    ReactElement.createHostElement("div", null, hasRefs ? "theInnerDiv" : null, JsPropertyMap.of()),
                    child);
        }
    }

    private static final class KeyOwner extends NativeView {
        @JsConstructor
        private KeyOwner(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return tree(
                    "div",
                    ReactElement.createHostElement(
                            "span", (String) requireInputs().get("keyValue"), "span", JsPropertyMap.of()));
        }
    }

    private static final class StateRow extends NativeView {
        @JsConstructor
        private StateRow(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return host("span");
        }
    }

    private static final class Flip extends NativeView {
        @JsConstructor
        private Flip(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var first = Js.<ReactElement>uncheckedCast(requireInputs().get("first"));
            final var second = Js.<ReactElement>uncheckedCast(requireInputs().get("second"));
            final var state = state();
            return output(
                    null != state && Js.isTruthy(state.get("flipped"))
                            ? tree("div", second, first)
                            : tree("div", first, second));
        }
    }

    private static final class NamespaceBoundary extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        private NamespaceBoundary(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            setState(JsPropertyMap.of("failed", true));
        }

        @Override
        public ReactNode render() {
            return Js.uncheckedCast(requireInputs().get(null == state() ? "children" : "fallback"));
        }
    }

    private static final class BailoutLetter extends NativeView implements OnShouldComponentUpdate {
        @JsConstructor
        private BailoutLetter(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> inputs) {
            return false;
        }

        @Override
        public ReactNode render() {
            return output(text("div", Browser.string(requireInputs().get("letter"))));
        }
    }

    private static final class PortalTrace extends NativeView
            implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount {
        @JsConstructor
        private PortalTrace(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidMount() {
            record("componentDidMount");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            record("componentDidUpdate");
        }

        @Override
        public void componentWillUnmount() {
            record("componentWillUnmount");
        }

        private void record(String phase) {
            final var trace = Js.<List<String>>uncheckedCast(requireInputs().get("trace"));
            trace.add(requireInputs().get("name") + " " + phase);
        }

        @Override
        public ReactNode render() {
            final var children = requireInputs().get("children");
            return null == children
                    ? host("div").input("children", requireInputs().get("name"))
                    : Js.uncheckedCast(children);
        }
    }
}
