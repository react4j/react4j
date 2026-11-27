package react4j.dom;

import static react4j.webtesting.Checks.check;

import java.util.ArrayList;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
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
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class DomRefsBrowserTest {
    private static final ViewConstructorFunction REF_HOP = RefHop::new;
    private static final ViewConstructorFunction VIEW = View::new;
    private static final ViewConstructorFunction COUNTER = Counter::new;
    private static final ViewConstructorFunction BOUND_HOST = BoundHost::new;
    private static final ViewConstructorFunction OWNER = Owner::new;
    private static final ViewConstructorFunction ASYNC_APP = AsyncApp::new;
    private static final ViewConstructorFunction ASYNC_MODAL = AsyncModal::new;

    private DomRefsBrowserTest() {}

    @JsMethod
    public static void run() {
        final var root = Browser.append("div");
        final boolean invariants = React.shouldCheckInvariants();
        ReactTestUtil.noCheckInvariants();
        try {
            attachedRootRefs(root);
            emptyRefMaps(root);
            hoppingRef(root);
            reactiveRefs(root);
            nestedCallbackRef(root);
            invalidRefs(root);
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
            if (invariants) {
                ReactTestUtil.checkInvariants();
            }
        }
    }

    private static void attachedRootRefs(Browser.Element root) {
        final var primary = new ArrayList<@Nullable Object>();
        final var secondary = new ArrayList<@Nullable Object>();
        final Ref save = value -> primary.add(value);
        final Ref saveSecond = value -> secondary.add(value);
        final var host = ReactDOM.render(host("div", null, save), root);
        check(1 == primary.size(), "The root host ref is called once at mount");
        check(isElement(primary.get(0), "DIV"), "The root ref receives a div");
        check(host == primary.get(0), "The root render returns its host");
        ReactDOM.unmountComponentAtNode(root);
        check(2 == primary.size(), "The root host ref is called again on unmount");
        check(null == primary.get(1), "The host ref clears");
        primary.clear();

        final var view = ReactDOM.render(withRef(ReactElement.createViewElement(VIEW), save), root);
        check(1 == primary.size(), "The root view ref is called once at mount");
        check(primary.get(0) instanceof View, "The ref receives the actual view");
        check(view == primary.get(0), "The root render returns its view");
        check(((View) Objects.requireNonNull(view)).method(), "The returned view exposes its method");
        check(((View) Objects.requireNonNull(primary.get(0))).method(), "The referenced view exposes its method");
        ReactDOM.unmountComponentAtNode(root);
        check(2 == primary.size(), "The root view ref is called again on unmount");
        check(null == primary.get(1), "The view ref clears");
        primary.clear();

        final var child = withRef(ReactElement.createViewElement(VIEW), save);
        child.setKey("a");
        final var result = ReactDOM.render(
                ReactNode.of(
                        child, ReactNode.of(5), host("div", "b", saveSecond).input("children", "Hello")),
                root);
        check(1 == primary.size(), "The first root child ref is called once");
        check(primary.get(0) instanceof View, "The first root child ref receives a view");
        check(result == primary.get(0), "Rendering a root child array returns the first visible view");
        check(1 == secondary.size(), "The second root child ref is called once");
        check(isElement(secondary.get(0), "DIV"), "The last child ref receives a div");
        check(result != secondary.get(0), "The return value differs from the last child's ref");
        ReactDOM.unmountComponentAtNode(root);
        check(2 == primary.size() && null == primary.get(1), "The first array child detaches its ref");
        check(2 == secondary.size() && null == secondary.get(1), "The last array child detaches its ref");
        check(null == ReactDOM.render(ReactNode.of(), root), "An empty render returns no instance");
        final var number = Objects.requireNonNull(ReactDOM.render(ReactNode.of(5), root));
        check(3 == Js.asInt(Browser.property(number, "nodeType")), "A number root returns its DOM text node");
        ReactDOM.unmountComponentAtNode(root);
    }

    private static void hoppingRef(Browser.Element root) {
        final var component = Objects.requireNonNull(ReactDOM.render(ReactElement.createViewElement(REF_HOP), root));
        final var view = (RefHop) component;
        final var hosts = Browser.queryAll(root, ".hop-child");
        final var refs = refs(component);
        final String[] named = {"divOneRef", "divTwoRef", "divThreeRef"};
        for (int hop = 0; hop < 4; hop++) {
            for (int index = 0; index < hosts.length; index++) {
                final var name = hop % 3 == index ? "hopRef" : named[index];
                check(hosts[index] == refs.get(name), "The " + name + " points to host at index " + index);
            }
            if (hop < 3) {
                view.setState(JsPropertyMap.of("hop", hop + 1));
            }
        }
        ReactDOM.unmountComponentAtNode(root);
    }

    private static void emptyRefMaps(Browser.Element root) {
        final var secondRoot = Browser.append("div");
        try {
            final var first = Objects.requireNonNull(ReactDOM.render(ReactElement.createViewElement(VIEW), root));
            final var second =
                    Objects.requireNonNull(ReactDOM.render(ReactElement.createViewElement(VIEW), secondRoot));
            final var empty = refs(first);
            check(empty == refs(second), "Views share the initial empty ref map");
            check(0 == refsSize(empty), "The shared ref map starts empty");
            ReactDOM.render(ReactElement.createViewElement(VIEW).input("ownRef", true), root);
            check(empty != refs(first), "Attaching the first ref gives its owner a private map");
            check(isElement(refs(first).get("owned"), "SPAN"), "The first ref resolves to the owner's host");
            check(empty == refs(second) && 0 == refsSize(empty), "The other view keeps the shared empty map");
            check(
                    "development".equals(System.getProperty("react4j.environment")) == isFrozen(empty),
                    "The shared empty ref map is frozen only in development");
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            ReactDOM.unmountComponentAtNode(secondRoot);
            secondRoot.remove();
        }
    }

    private static void reactiveRefs(Browser.Element root) {
        final var app = Objects.requireNonNull(ReactDOM.render(ReactElement.createViewElement(OWNER), root));
        check(app instanceof Owner, "The outer class is the public root instance");
        final var appRefs = refs(app);
        check(appRefs.get("myContainer") instanceof View, "The owner keeps its wrapper class ref");
        check(appRefs.get("myCounter") instanceof Counter, "The owner keeps the injected child view ref");
        final var counter = Objects.requireNonNull(appRefs.get("myCounter"));
        final var increment = Browser.query(root, ".increment");
        final var reset = Browser.query(root, ".reset");
        final Browser.Element[] actions = {reset, increment, increment, reset};
        final int[] expected = {1, 2, 3, 1};
        check(
                1 == refsSize(refs(counter)) && 1 == Browser.queryAll(root, ".clickLogDiv").length,
                "The initial log has one DOM node and one ref");
        for (int i = 0; i < actions.length; i++) {
            actions[i].click();
            check(expected[i] == Browser.queryAll(root, ".clickLogDiv").length, "One element for each click log");
            check(expected[i] == refsSize(refs(counter)), "One ref for each click log");
        }
        ReactDOM.unmountComponentAtNode(root);
    }

    private static void nestedCallbackRef(Browser.Element root) {
        final var count = new int[1];
        final Ref callback = value -> count[0]++;
        ReactDOM.render(ReactElement.createViewElement(BOUND_HOST).input("save", callback), root);
        check(1 == count[0], "A state update preserves the forwarded host ref");
        ReactDOM.unmountComponentAtNode(root);
    }

    private static void invalidRefs(Browser.Element root) {
        for (final var ref : new Object[] {Browser.number(10), true, symbol("foo"), Js.undefined()}) {
            EventsBrowserTest.withoutReportedErrors(() -> DomRootBrowserTest.expectInvariant(
                    "Expected ref to be", 284, () -> ReactDOM.render(host("div", null, ref), root)));
        }
        final var numeric = Objects.requireNonNull(
                ReactDOM.render(ReactElement.createViewElement(VIEW).input("numericRef", true), root));
        check(isElement(refs(numeric).get("1"), "DIV"), "A numeric ref in render becomes a string name");
    }

    @JsMethod
    public static void constructorRef() {
        final var root = Browser.append("div");
        try {
            final ViewConstructorFunction constructor = inputs -> {
                final var unattached = host("p", null, "p").input("children", "Hello!");
                return new NativeView(inputs) {
                    @Override
                    public ReactNode render() {
                        return tree("div", unattached);
                    }
                };
            };
            EventsBrowserTest.withoutReportedErrors(() -> RuntimeParityBrowserTest.expectFailure(
                    "no owner was set", () -> ReactDOM.render(ReactElement.createViewElement(constructor), root)));
        } finally {
            ReactDOM.unmountComponentAtNode(root);
            root.remove();
        }
    }

    @JsMethod
    public static BrowserPromise destroyAsynchronously() {
        final var outer = Browser.append("div");
        final var inner = Browser.append("div");
        final var pending = new BrowserPromise[1];
        ReactDOM.render(
                ReactElement.createViewElement(ASYNC_APP).input("inner", inner).input("pending", pending), outer);
        check(Browser.exists(inner, "a"), "The modal renders a nested root with its owner's string ref");
        ReactDOM.render(ReactNode.of(), outer);
        return Objects.requireNonNull(pending[0])
                .thenDo(() -> check(!Browser.exists(inner, "a"), "The scheduled unmount clears the nested root"))
                .finallyDo(() -> {
                    ReactDOM.unmountComponentAtNode(inner);
                    ReactDOM.unmountComponentAtNode(outer);
                    inner.remove();
                    outer.remove();
                });
    }

    private static int refsSize(JsPropertyMap<@Nullable Object> refs) {
        final var size = new int[1];
        refs.forEach(key -> size[0]++);
        return size[0];
    }

    private static boolean isElement(@Nullable Object node, String tag) {
        return null != node
                && 1 == Js.asInt(Browser.property(node, "nodeType"))
                && tag.equals(Browser.property(node, "tagName"));
    }

    private static JsPropertyMap<@Nullable Object> refs(Object owner) {
        return Js.uncheckedCast(Objects.requireNonNull(Browser.property(owner, "refs")));
    }

    private static ReactElement withRef(ReactElement element, Object ref) {
        Js.<JsPropertyMap<Object>>uncheckedCast(element).set("ref", ref);
        return element;
    }

    private static ReactElement host(String tag, @Nullable String key, @Nullable Object ref) {
        return ReactElement.createHostElement(tag, key, ref, JsPropertyMap.of());
    }

    private static ReactElement tree(String tag, Renderable... children) {
        return host(tag, null, null).input("children", ReactNode.of(children));
    }

    private static int integer(@Nullable JsPropertyMap<@Nullable Object> values, String key, int defaultValue) {
        final var value = null == values ? null : values.get(key);
        return null == value ? defaultValue : Js.asInt(value);
    }

    @JsFunction
    private interface Ref {
        void accept(@Nullable Object value);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Symbol")
    private static native Object symbol(String description);

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Object.isFrozen")
    private static native boolean isFrozen(Object value);

    private static final class RefHop extends NativeView {
        @JsConstructor
        private RefHop(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final int hop = integer(state(), "hop", 0) % 3;
            final var children = new ReactElement[3];
            final String[] names = {"divOneRef", "divTwoRef", "divThreeRef"};
            for (int i = 0; i < children.length; i++) {
                children[i] = host("div", null, hop == i ? "hopRef" : names[i]).input("className", "hop-child");
            }
            return tree("div", children);
        }
    }

    private static final class View extends NativeView {
        @JsConstructor
        private View(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        boolean method() {
            return true;
        }

        @Override
        public ReactNode render() {
            if (Js.isTruthy(requireInputs().get("ownRef"))) {
                return tree("div", host("span", null, "owned"));
            }
            if (Js.isTruthy(requireInputs().get("numericRef"))) {
                return host("div", null, Browser.number(1));
            }
            return host("div", null, null).input("children", requireInputs().get("children"));
        }
    }

    private static final class Counter extends NativeView {
        private final ReactEventHandler _increment = event -> setState(JsPropertyMap.of("count", count() + 1));

        @JsConstructor
        private Counter(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        void reset() {
            setState(JsPropertyMap.of("count", 1));
        }

        private int count() {
            return integer(state(), "count", 1);
        }

        @Override
        public ReactNode render() {
            final var children = new ReactElement[count()];
            for (int i = 0; i < children.length; i++) {
                final var name = "clickLog" + i;
                children[i] = host("div", name, name).input("className", "clickLogDiv");
            }
            return tree("span", children).input("className", "increment").input("onClick", _increment);
        }
    }

    private static final class Owner extends NativeView {
        private final ReactEventHandler _reset =
                event -> ((Counter) Objects.requireNonNull(refs(this).get("myCounter"))).reset();

        @JsConstructor
        private Owner(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return tree(
                    "div",
                    host("div", null, "resetDiv").input("className", "reset").input("onClick", _reset),
                    withRef(ReactElement.createViewElement(VIEW), "myContainer")
                            .input("children", withRef(ReactElement.createViewElement(COUNTER), "myCounter")));
        }
    }

    private static final class BoundHost extends NativeView implements OnComponentDidMount {
        @JsConstructor
        private BoundHost(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidMount() {
            setState(JsPropertyMap.of());
        }

        @Override
        public ReactNode render() {
            return ReactElement.createViewElement(VIEW)
                    .input("children", host("a", null, requireInputs().get("save")));
        }
    }

    private static final class AsyncApp extends NativeView {
        @JsConstructor
        private AsyncApp(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var props = requireInputs();
            return ReactElement.createViewElement(ASYNC_MODAL)
                    .input("inner", props.get("inner"))
                    .input("pending", props.get("pending"))
                    .input("child", host("a", null, "ref"));
        }
    }

    private static final class AsyncModal extends NativeView implements OnComponentDidMount, OnComponentWillUnmount {
        @JsConstructor
        private AsyncModal(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidMount() {
            final var child = Js.<ReactElement>uncheckedCast(requireInputs().get("child"));
            ReactDOM.render(
                    tree("div", child), Objects.requireNonNull(requireInputs().get("inner")));
        }

        @Override
        public void componentWillUnmount() {
            final var inner = Objects.requireNonNull(requireInputs().get("inner"));
            final var pending =
                    Js.<BrowserPromise[]>uncheckedCast(requireInputs().get("pending"));
            pending[0] = Browser.delay(0)
                    .thenDo(() -> check(
                            ReactDOM.unmountComponentAtNode(inner), "The scheduled modal unmount does not throw"));
        }

        @Override
        public @Nullable ReactNode render() {
            return null;
        }
    }
}
