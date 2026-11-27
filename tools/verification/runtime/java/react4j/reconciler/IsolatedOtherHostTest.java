package react4j.reconciler;

import static react4j.reconciler.IsolatedReconcilerTest.array;
import static react4j.reconciler.IsolatedReconcilerTest.captureWarnings;
import static react4j.reconciler.IsolatedReconcilerTest.check;
import static react4j.reconciler.IsolatedReconcilerTest.equal;
import static react4j.reconciler.IsolatedReconcilerTest.host;
import static react4j.reconciler.IsolatedReconcilerTest.props;
import static react4j.reconciler.IsolatedReconcilerTest.rootShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.JsError;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedOtherHostTest {
    private static final String BEFORE = "abcdefghijklmnopqrst";
    private static final String AFTER = "mxhpgwfralkeoivcstzy";
    private static final ViewConstructorFunction OUTPUT = IsolatedReconcilerTest.Output::new;
    private static final ViewConstructorFunction REF_OBSERVER = RefObserver::new;
    private static final ViewConstructorFunction SHAPE = Shape::new;
    private static final ViewConstructorFunction TITLES = Titles::new;
    private static final ViewConstructorFunction MOUNT_CALLBACK = MountCallback::new;

    private IsolatedOtherHostTest() {}

    public static void artLifecycle(boolean update) {
        final var root = new IsolatedReconcilerTest.Harness().memory.createRoot(false);
        final var seen = new ArrayList<@Nullable Object>();
        final var trace = new ArrayList<String>();
        final var app =
                ReactElement.createViewElement(REF_OBSERVER).input("seen", seen).input("trace", trace);
        root.render(app.input("visible", !update), null);
        if (update) {
            equal(Arrays.asList((Object) null), seen);
            equal(List.of(), trace);
            root.render(app.dup().input("visible", true), null);
            check(
                    seen.size() == 2 && seen.get(1) instanceof Shape,
                    "The updated ref reaches the parent before DidUpdate");
        } else {
            check(seen.size() == 1 && seen.get(0) instanceof Shape, "The child ref reaches the parent before DidMount");
        }
        equal(List.of("mounted"), trace);
        rootShape(root, "surface(group(shape()))");
    }

    public static void artContext() {
        final var fixture = new IsolatedReconcilerTest.Harness();
        final var primary = fixture.memory.createRoot(true);
        final var secondary = fixture.memory.createTestRoot(false);
        final var context = new Context<String>();
        final var yields = new ArrayList<String>();
        final var consumed = new ArrayList<@Nullable String>();
        primary.render(
                context.provide(
                        "Test",
                        yieldValue(yields, "A"),
                        yieldValue(yields, "B"),
                        context.consumer().render(value -> {
                            consumed.add(value);
                            return null;
                        }),
                        yieldValue(yields, "C")),
                null);
        fixture.yieldWhen(() -> !yields.isEmpty());
        check(fixture.turn(), "The primary provider has work ready");
        equal(List.of("A"), yields);
        equal(List.of(), consumed);
        final var warnings = captureWarnings(() -> secondary.render(
                host(
                        "surface",
                        null,
                        array(
                                context.consumer().render(value -> {
                                    consumed.add(value);
                                    return null;
                                }),
                                context.provide("ART", context.consumer().render(value -> {
                                    consumed.add(value);
                                    return null;
                                })))),
                null));
        equal("", warnings);
        equal(Arrays.asList(null, "ART"), consumed);
        consumed.clear();
        yields.clear();
        fixture.yieldWhen(() -> false);
        fixture.flush();
        equal(List.of("B", "C"), yields);
        equal(List.of("Test"), consumed);
    }

    public static void reorder(boolean persistent) {
        final var memory = new IsolatedReconcilerTest.Harness().memory;
        final var root = persistent ? memory.createPersistentRoot(false) : memory.createRoot(false);
        final var child = ReactElement.createViewElement(TITLES).input("chars", BEFORE);
        final @Nullable Object[] ref = {null};
        props(child).set("ref", (Ref) value -> ref[0] = value);
        root.render(host("outer", null, child), null);
        final var outer = first(root.getChildren());
        equal(BEFORE, titleOrder(first(children(outer))));
        final var instance = (Titles) Objects.requireNonNull(ref[0]);
        instance.setState(JsPropertyMap.of("chars", AFTER));
        final var next = first(root.getChildren());
        equal(AFTER, titleOrder(first(children(next))));
        if (persistent) {
            check(outer != next, "A persistent ancestor gets recreated after the descendant-only update");
            equal(BEFORE, titleOrder(first(children(outer))));
        } else {
            check(outer == next, "A mutable ancestor stays mounted during keyed descendant changes");
        }
    }

    public static void persistentCommit() {
        final var root = new IsolatedReconcilerTest.Harness().memory.createPersistentRoot(false);
        final var committed = new ArrayList<String>();
        root.render(
                host("view", null, host("view", null, null).input("prop", "b")).input("prop", "a"),
                () -> committed.add(IsolatedReconcilerTest.shape(root.getChildren())));
        equal(List.of("view[a](view[b]())"), committed);
    }

    public static void persistentProps() {
        final var memory = new IsolatedReconcilerTest.Harness().memory;
        final var root = memory.createPersistentRoot(false);
        root.render(host("view", null, null).input("foo", "foo"), null);
        final var first = first(root.getChildren());
        equal("foo", committedProp(first, "foo"));
        memory.resetCounters();
        root.render(host("view", null, null).input("foo", "bar"), null);
        final var next = first(root.getChildren());
        check(first != next, "The persistent host creates a replacement after a property update");
        equal("foo", committedProp(first, "foo"));
        equal("bar", committedProp(next, "foo"));
        equal(1, Js.coerceToInt(props(memory.getPersistentCounters()).get("hostCloneCounter")));

        root.render(persistentText("a", "a", "1"), null);
        final var initial = first(root.getChildren());
        assertText(initial, "a", "a", "1");
        root.render(persistentText("a", "b", "1"), null);
        final var propertyOnly = first(root.getChildren());
        assertText(propertyOnly, "a", "b", "1");
        root.render(persistentText("b", "b", "2"), null);
        assertText(first(root.getChildren()), "b", "b", "2");
        assertText(propertyOnly, "a", "b", "1");
        assertText(initial, "a", "a", "1");
    }

    private static ReactElement persistentText(String foo, String bar, String text) {
        return host("Text", null, ReactNode.of(text)).input("foo", foo).input("bar", bar);
    }

    private static void assertText(Object node, String foo, String bar, String text) {
        equal("Text", props(node).get("type"));
        equal(foo, committedProp(node, "foo"));
        equal(bar, committedProp(node, "bar"));
        equal(text, props(node).get("text"));
    }

    private static @Nullable Object committedProp(Object node, String name) {
        return props(Objects.requireNonNull(props(node).get("props"))).get(name);
    }

    public static void sharedErrorStack(boolean development) {
        final var root = new IsolatedReconcilerTest.Harness().memory.createRoot(false);
        final var reported = new ArrayList<String>();
        final ViewConstructorFunction classComponent = IsolatedReconcilerTest.Output::new;
        final ViewConstructorFunction function = IsolatedReconcilerTest.Output::new;
        final ViewConstructorFunction boundary = ErrorBoundary::new;
        props(classComponent).set("displayName", "ClassComponent");
        props(function).set("displayName", "FunctionComponent");
        props(function).set("functionComponent", true);
        final var child = source(host("View", null, null), 3);
        props(child).set("ref", (Ref) value -> {
            if (null != value) {
                throw new IllegalStateException("current host ref");
            }
        });
        final var inner =
                source(ReactElement.createViewElement(function).input("output", (Supplier<ReactNode>) () -> child), 2);
        final var outer = source(
                ReactElement.createViewElement(classComponent).input("output", (Supplier<ReactNode>) () -> inner), 1);
        captureWarnings(() -> root.render(
                ReactElement.createViewElement(boundary).input("child", outer).input("reported", reported), null));
        equal(1, reported.size());
        final var expected = development
                ? "\n    in View (at NativeFixture.java:3)\n    in FunctionComponent (at NativeFixture.java:2)"
                        + "\n    in ClassComponent (at NativeFixture.java:1)"
                : "\n    in View\n    in FunctionComponent\n    in ClassComponent";
        check(
                reported.get(0).startsWith(expected),
                "The host error reports shared component ancestry: " + reported.get(0));
    }

    private static ReactElement source(ReactElement element, double line) {
        props(element).set("_source", JsPropertyMap.of("fileName", "NativeFixture.java", "lineNumber", line));
        return element;
    }

    public static void emptyStateCallback(boolean persistent) {
        final var memory = new IsolatedReconcilerTest.Harness().memory;
        final var root = persistent ? memory.createPersistentRoot(false) : memory.createRoot(false);
        final var counts = new ArrayList<Integer>();
        root.render(
                ReactElement.createViewElement(MOUNT_CALLBACK).input("callback", countArguments(counts::add)), null);
        equal(List.of(0), counts);
        rootShape(root, "");
    }

    private static ReactElement yieldValue(List<String> values, String label) {
        return ReactElement.createViewElement(OUTPUT).input("output", (Supplier<@Nullable ReactNode>) () -> {
            values.add(label);
            return null;
        });
    }

    private static Object first(Object list) {
        return Js.<Object[]>uncheckedCast(list)[0];
    }

    private static Object children(Object node) {
        return Objects.requireNonNull(props(node).get("children"));
    }

    private static String titleOrder(Object parent) {
        final var result = new StringBuilder();
        for (final var node : Js.<Object[]>uncheckedCast(children(parent))) {
            final var hidden = props(Objects.requireNonNull(props(node).get("props")));
            result.append(Js.asString(hidden.get("title")));
        }
        return result.toString();
    }

    @JsMethod
    private static native NativeView.StateCallback countArguments(ArgumentCount callback);

    @JsFunction
    private interface ArgumentCount {
        void take(int count);
    }

    @JsFunction
    private interface Ref {
        void set(@Nullable Object value);
    }

    private static final class RefObserver extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        private final JsPropertyMap<@Nullable Object> _ref = JsPropertyMap.of("current", null);

        @JsConstructor
        RefObserver(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            final ReactElement shape;
            if (Boolean.TRUE.equals(inputs.get("visible"))) {
                shape = ReactElement.createViewElement(SHAPE).input("trace", inputs.get("trace"));
                props(shape).set("ref", _ref);
            } else {
                shape = null;
            }
            return host("surface", null, host("group", null, shape));
        }

        private void observe() {
            Js.<List<@Nullable Object>>uncheckedCast(requireInputs().get("seen"))
                    .add(_ref.get("current"));
        }

        @Override
        public void componentDidMount() {
            observe();
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            observe();
        }
    }

    private static final class Shape extends NativeView implements OnComponentDidMount {
        @JsConstructor
        Shape(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return host("shape", null, null);
        }

        @Override
        public void componentDidMount() {
            Js.<List<String>>uncheckedCast(requireInputs().get("trace")).add("mounted");
        }
    }

    private static final class Titles extends NativeView {
        @JsConstructor
        Titles(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var state = state();
            final var chars = Js.asString((null == state ? requireInputs() : state).get("chars"));
            final var children = new ReactElement[chars.length()];
            for (int i = 0; i < children.length; i++) {
                final var label = chars.substring(i, i + 1);
                final var view = host("view", null, null).input("title", label);
                view.setKey(label);
                children[i] = view;
            }
            return host("view", null, array((Object[]) children));
        }
    }

    private static final class ErrorBoundary extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        ErrorBoundary(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            return null == state() ? Js.uncheckedCast(requireInputs().get("child")) : null;
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            Js.<List<String>>uncheckedCast(requireInputs().get("reported")).add(info.componentStack);
            setState(JsPropertyMap.of("failed", true));
        }
    }

    private static final class MountCallback extends NativeView implements OnComponentDidMount {
        @JsConstructor
        MountCallback(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            return null;
        }

        @Override
        public void componentDidMount() {
            setStateWithCallback(
                    JsPropertyMap.of(), Js.uncheckedCast(requireInputs().get("callback")));
        }
    }
}
