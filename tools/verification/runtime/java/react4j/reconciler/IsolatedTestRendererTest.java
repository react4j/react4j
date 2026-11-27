package react4j.reconciler;

import static react4j.reconciler.IsolatedReconcilerTest.array;
import static react4j.reconciler.IsolatedReconcilerTest.captureWarnings;
import static react4j.reconciler.IsolatedReconcilerTest.check;
import static react4j.reconciler.IsolatedReconcilerTest.equal;
import static react4j.reconciler.IsolatedReconcilerTest.host;
import static react4j.reconciler.IsolatedReconcilerTest.props;
import static react4j.reconciler.IsolatedReconcilerTest.rootShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.JsError;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedTestRendererTest {
    private static final ViewConstructorFunction OUTPUT = IsolatedReconcilerTest.Output::new;
    private static final ViewConstructorFunction TRACKED = Lifecycle::new;
    private static final ViewConstructorFunction UPDATE = UpdateOnMount::new;
    private static final ViewConstructorFunction MOUSE = Mouse::new;
    private static final ViewConstructorFunction BOUNDARY = Boundary::new;
    private static final ViewConstructorFunction ANGRY = Angry::new;
    private static final ViewConstructorFunction INTERRUPTED = Interrupted::new;

    private IsolatedTestRendererTest() {}

    public static void basicOutput() {
        final var root = new IsolatedReconcilerTest.Harness().memory.createTestRoot(false);
        root.render(view(() -> host("a", null, null).input("role", "link")), null);
        rootShape(root, "a()");
        assertProps(first(root), "role", "link");
        root.render(view(() -> null), null);
        rootShape(root, "");
        root.render(
                view(() ->
                        host("div", null, view(() -> host("moo", null, null))).input("className", "purple")),
                null);
        rootShape(root, "div(moo())");
        assertProps(first(root), "className", "purple");
        assertProps(firstChild(first(root)), null, null);
    }

    public static void updatingOnMount() {
        final var root = new IsolatedReconcilerTest.Harness().memory.createTestRoot(false);
        final var renders = new int[1];
        root.render(ReactElement.createViewElement(UPDATE).input("renders", renders), null);
        rootShape(root, "div(text(7),moo())");
        assertProps(first(root), "className", "purple");
        assertProps(Js.<Object[]>uncheckedCast(props(first(root)).get("children"))[1], null, null);
        equal(6, renders[0]);
    }

    public static void classInstance() {
        final var root = new IsolatedReconcilerTest.Harness().memory.createTestRoot(false);
        root.render(ReactElement.createViewElement(MOUSE), null);
        rootShape(root, "div(text(mouse))");
        assertProps(first(root), null, null);
        final var instance = Objects.requireNonNull(snapshot(root).get("stateNode"));
        check(instance instanceof Mouse, "A class root exposes the mounted class instance");
        ((Mouse) instance).handleMoose();
        rootShape(root, "div(text(moose))");
        assertProps(first(root), null, null);
        check(instance == snapshot(root).get("stateNode"), "State updates preserve the mounted root instance");
    }

    public static void hostUpdates() {
        final var root = new IsolatedReconcilerTest.Harness().memory.createTestRoot(false);
        root.render(host("div", null, ReactNode.of("mouse")), null);
        rootShape(root, "div(text(mouse))");
        assertProps(first(root), null, null);
        root.render(host("span", null, ReactNode.of("mice")), null);
        rootShape(root, "span(text(mice))");
        assertProps(first(root), null, null);

        root.render(host("div", null, array(keyed("A"), keyed("B"), keyed("C"))), null);
        rootShape(root, "div(span(text(A)),span(text(B)),span(text(C)))");
        final var before = Js.<Object[]>uncheckedCast(props(first(root)).get("children"));
        final var originalB = before[1];
        final var originalC = before[2];
        assertUnstyledSpans(root);
        root.render(host("div", null, array(keyed("D"), keyed("C"), keyed("B"))), null);
        rootShape(root, "div(span(text(D)),span(text(C)),span(text(B)))");
        final var after = Js.<Object[]>uncheckedCast(props(first(root)).get("children"));
        check(originalB == after[2] && originalC == after[1], "Keyed host children retain their identities");
        assertUnstyledSpans(root);
    }

    public static void lifecycle() {
        final var root = new IsolatedReconcilerTest.Harness().memory.createTestRoot(false);
        final var trace = new ArrayList<String>();
        root.render(tracked("foo", "Foo", trace), null);
        root.render(tracked("bar", "Bar", trace), null);
        root.unmount(null);
        equal(List.of("render Foo", "mount Foo", "render Bar", "unmount Foo", "mount Bar", "unmount Bar"), trace);
    }

    public static void errorBoundary() {
        final var root = new IsolatedReconcilerTest.Harness().memory.createTestRoot(false);
        final var trace = new ArrayList<String>();
        captureWarnings(
                () -> root.render(ReactElement.createViewElement(BOUNDARY).input("trace", trace), null));
        rootShape(root, "div(text(Happy Birthday!))");
        assertProps(first(root), null, null);
        equal(
                List.of(
                        "Boundary render",
                        "Angry render",
                        "Boundary componentDidMount",
                        "Boundary componentDidCatch",
                        "Boundary render"),
                trace);
    }

    public static void textAndRootFragments() {
        final var root = new IsolatedReconcilerTest.Harness().memory.createTestRoot(false);
        final var child = view(() -> null);
        final ViewConstructorFunction wrap = TextHost::new;
        final var component = ReactElement.createViewElement(wrap);
        for (final var sequence : new TextCase[] {
            new TextCase(ReactNode.of("Hi"), "div(text(Hi))"),
            new TextCase(array(ReactNode.of("Hi"), ReactNode.of("Bye")), "div(text(Hi),text(Bye))"),
            new TextCase(ReactNode.of("Bye"), "div(text(Bye))"),
            new TextCase(ReactNode.of(42), "div(text(42))"),
            new TextCase(host("div", null, null), "div(div())")
        }) {
            root.render(component.dup().input("children", sequence.children()), null);
            rootShape(root, sequence.shape());
            assertProps(first(root), null, null);
        }
        assertProps(firstChild(first(root)), null, null);

        root.render(array(ReactNode.of("Hello"), ReactNode.of("world")), null);
        rootShape(root, "text(Hello),text(world)");
        root.render(ReactNode.of(42), null);
        rootShape(root, "text(42)");
        root.render(array(ReactNode.of(42), ReactNode.of("world")), null);
        rootShape(root, "text(42),text(world)");

        final var first = child.dup().input("output", (Supplier<ReactNode>) () -> ReactNode.of("Hi"));
        first.setKey("a");
        final var second = child.dup().input("output", (Supplier<ReactNode>) () -> ReactNode.of("Bye"));
        second.setKey("b");
        root.render(array(first, second), null);
        rootShape(root, "text(Hi),text(Bye)");
        root.render(host("div", null, null), null);
        rootShape(root, "div()");
        assertProps(first(root), null, null);
        final var goodbye = host("div", null, ReactNode.of("goodbye"));
        goodbye.setKey("a");
        root.render(array(goodbye, ReactNode.of("world")), null);
        rootShape(root, "div(text(goodbye)),text(world)");
        assertProps(first(root), null, null);
    }

    public static void treeSnapshots(int number) {
        final var root = new IsolatedReconcilerTest.Harness().memory.createTestRoot(false);
        final var plain = Map.<String, Object>of();
        final var qoo =
                component(true, ignored -> textHost("span", "Hello World!").input("className", "Qoo"));
        final var qooShape =
                new Tree(0, qoo, plain, new Tree(5, "span", Map.of("className", "Qoo"), text("Hello World!")));
        final var one = new Tree(5, "div", plain, text("One"));
        final var two = new Tree(5, "div", plain, text("Two"));
        final var three = new Tree(5, "div", plain, text("Three"));
        final var four = new Tree(5, "div", plain, text("Four"));
        switch (number) {
            case 17 -> {
                root.render(ReactElement.createViewElement(qoo), null);
                assertTree(root, qooShape);
            }
            case 18 -> {
                final var foo = component(
                        true,
                        ignored -> ReactElement.createFragment(
                                "outer", ReactElement.createFragment("inner", ReactNode.of("foo"))));
                root.render(ReactElement.createViewElement(foo), null);
                assertTree(root, new Tree(0, foo, plain, text("foo")));
            }
            case 19 -> {
                final var foo = component(false, ignored -> null);
                root.render(ReactElement.createViewElement(foo), null);
                assertTree(root, new Tree(1, foo, plain));
            }
            case 20, 21 -> {
                final var foo = component(20 == number, inputs -> Js.uncheckedCast(inputs.get("children")));
                final var input = ReactElement.createViewElement(foo);
                final var first = textHost("div", "One");
                final var second = textHost("div", "Two");
                if (20 == number) {
                    root.render(input.input("children", array(first, second)), null);
                    assertTree(root, new Tree(0, foo, plain, one, two));
                } else {
                    final var nested = input.dup().input("children", textHost("div", "Three"));
                    final var parent = input.input("children", array(first, second, nested));
                    root.render(host("div", null, array(parent, textHost("div", "Four"))), null);
                    assertTree(
                            root,
                            new Tree(
                                    5,
                                    "div",
                                    plain,
                                    new Tree(1, foo, plain, one, two, new Tree(1, foo, plain, three)),
                                    four));
                }
            }
            case 22 -> {
                root.render(
                        ReactElement.createFragment(
                                "outer",
                                ReactElement.createFragment(
                                        "first",
                                        textHost("div", "One"),
                                        textHost("div", "Two"),
                                        ReactElement.createFragment("third", textHost("div", "Three"))),
                                textHost("div", "Four")),
                        null);
                assertTree(root, one, two, three, four);
            }
            case 24 -> {
                final var foo = component(
                        true,
                        inputs -> host(
                                        "div",
                                        null,
                                        array(
                                                textHost("span", "Literal").input("className", "Foo2"),
                                                inputs.get("children")))
                                .input("className", "Foo " + inputs.get("className")));
                final var bar = component(
                        false,
                        inputs -> ReactElement.createViewElement(foo)
                                .input("className", Boolean.TRUE.equals(inputs.get("special")) ? "special" : "normal")
                                .input("children", inputs.get("children")));
                final var bam = component(
                        false,
                        ignored -> ReactElement.createViewElement(bar)
                                .input("special", true)
                                .input("children", ReactElement.createViewElement(qoo)));
                root.render(ReactElement.createViewElement(bam), null);
                assertTree(
                        root,
                        new Tree(
                                1,
                                bam,
                                plain,
                                new Tree(
                                        1,
                                        bar,
                                        Map.of("special", true),
                                        new Tree(
                                                0,
                                                foo,
                                                Map.of("className", "special"),
                                                new Tree(
                                                        5,
                                                        "div",
                                                        Map.of("className", "Foo special"),
                                                        new Tree(
                                                                5,
                                                                "span",
                                                                Map.of("className", "Foo2"),
                                                                text("Literal")),
                                                        qooShape)))));
            }
            case 27, 28 -> {
                final var child = component(true, inputs -> ReactNode.of(Js.asString(inputs.get("value"))));
                if (27 == number) {
                    final var context = new Context<String>();
                    props(context).set("_currentValue", "a");
                    final var app = component(
                            true,
                            ignored -> context.provide(
                                    "b",
                                    context.consumer()
                                            .render(value -> ReactElement.createViewElement(child)
                                                    .input("value", value))));
                    root.render(ReactElement.createViewElement(app), null);
                    assertTree(root, new Tree(0, app, plain, new Tree(0, child, Map.of("value", "b"), text("b"))));
                    rootShape(root, "text(b)");
                } else {
                    final var app = component(
                            true,
                            inputs -> ReactElement.createStrictMode(
                                    ReactElement.createViewElement(child).input("value", inputs.get("value"))));
                    root.render(ReactElement.createViewElement(app).input("value", "a"), null);
                    assertTree(
                            root,
                            new Tree(
                                    0, app, Map.of("value", "a"), new Tree(0, child, Map.of("value", "a"), text("a"))));
                    rootShape(root, "text(a)");
                }
            }
            case 29 -> {
                final Forward render = (ignored, ref) ->
                        host("div", null, ReactElement.createHostElement("span", null, ref, JsPropertyMap.of()));
                final var descriptor = JsPropertyMap.of("$$typeof", React.ForwardRef, "render", render);
                final var ref = JsPropertyMap.<@Nullable Object>of("current", "before");
                final var app = component(false, ignored -> {
                    final var wrapped = host("span", null, null);
                    props(wrapped).set("type", descriptor);
                    props(wrapped).set("ref", ref);
                    return wrapped;
                });
                root.render(ReactElement.createViewElement(app), null);
                assertTree(root, new Tree(1, app, plain, new Tree(5, "div", plain, new Tree(5, "span", plain))));
                check(
                        Js.isTripleEqual(null, ref.get("current")),
                        "A forwarded test host ref receives its default null");
            }
            default -> throw new IllegalArgumentException("Unexpected tree fixture " + number);
        }
    }

    private static void assertTree(MemoryRenderer.Root root, Tree... tree) {
        assertTrees(props(Objects.requireNonNull(root.getTestTree())), tree);
    }

    private static void assertTrees(JsPropertyMap<@Nullable Object> parent, Tree[] expected) {
        final var children = new ArrayList<JsPropertyMap<@Nullable Object>>();
        exposedChildren(parent, children);
        equal(expected.length, children.size());
        for (int i = 0; i < expected.length; i++) {
            final var node = children.get(i);
            final var tree = expected[i];
            equal(tree.tag(), Js.coerceToInt(node.get("tag")));
            if (6 == tree.tag()) {
                equal(Objects.requireNonNull(tree.type()), node.get("memoizedProps"));
                continue;
            }
            check(Js.isTripleEqual(tree.type(), node.get("type")), "Test renderer exposes the original component type");
            final var props = props(Objects.requireNonNull(node.get("memoizedProps")));
            final var names = new ArrayList<String>();
            props.forEach(name -> {
                if (!"children".equals(name)) {
                    names.add(name);
                }
            });
            check(
                    names.size() == tree.properties().size() && names.stream().allMatch(tree.properties()::containsKey),
                    "Committed tree carries the expected prop names " + names);
            tree.properties().forEach((name, value) -> equal(value, props.get(name)));
            if (1 == tree.tag()) {
                check(node.get("stateNode") instanceof TreeView, "Class snapshots retain their runtime instance");
            } else if (0 == tree.tag()) {
                check(null == node.get("stateNode"), "Function snapshots have no public instance");
            }
            assertTrees(node, tree.children());
        }
    }

    private static void exposedChildren(
            JsPropertyMap<@Nullable Object> parent, List<JsPropertyMap<@Nullable Object>> children) {
        for (var child = parent.get("child"); null != child; ) {
            final var node = props(child);
            check(node.get("return") == parent, "Published child links point to the containing snapshot");
            final int tag = Js.coerceToInt(node.get("tag"));
            switch (tag) {
                case 0, 1, 5, 6 -> children.add(node);
                default -> exposedChildren(node, children);
            }
            child = node.get("sibling");
        }
    }

    private static Tree text(String value) {
        return new Tree(6, value, Map.of());
    }

    private static ReactElement textHost(String tag, String value) {
        return host(tag, null, ReactNode.of(value));
    }

    private static ViewConstructorFunction component(boolean function, Render render) {
        final ViewConstructorFunction type = inputs -> new TreeView(inputs, render);
        props(type).set("functionComponent", function);
        return type;
    }

    private record Tree(int tag, Object type, Map<String, Object> properties, Tree... children) {}

    @JsFunction
    private interface Render {
        @Nullable
        ReactNode apply(JsPropertyMap<@Nullable Object> inputs);
    }

    @JsFunction
    private interface Forward {
        ReactNode render(Object inputs, @Nullable Object ref);
    }

    private static final class TreeView extends NativeView {
        private final Render _render;

        @JsConstructor
        TreeView(JsPropertyMap<@Nullable Object> inputs, Render render) {
            super(inputs);
            _render = render;
        }

        @Override
        public @Nullable ReactNode render() {
            return _render.apply(requireInputs());
        }
    }

    public static void specialRoots() {
        final var root = new IsolatedReconcilerTest.Harness().memory.createTestRoot(false);
        final var plain = Map.<String, Object>of();
        final var div = new Tree(5, "div", plain);
        final var first = host("div", null, null);
        final var second = host("div", null, null);
        final var pair = array(first, second);
        final Forward forward = (inputs, ignored) ->
                host("section", null, Js.uncheckedCast(props(inputs).get("children")));
        final var wrapped = host("section", null, pair);
        props(wrapped).set("type", JsPropertyMap.of("$$typeof", React.ForwardRef, "render", forward));
        root.render(wrapped, null);
        assertTree(root, new Tree(5, "section", plain, div, div));
        final var context = new Context<Object>();
        for (final var special : new ReactNode[] {
            ReactElement.createFragment(null, first, second),
            ReactElement.createFragment("foo", first, second),
            ReactElement.createStrictMode(first, second),
            context.provide(null, first, second)
        }) {
            root.render(special, null);
            assertTree(root, div, div);
        }
    }

    public static void transparentNodes() {
        final var root = new IsolatedReconcilerTest.Harness().memory.createTestRoot(false);
        final var context = new Context<Object>();
        final var view = component(true, inputs -> ReactElement.createHostElement("RCTView", null, null, inputs));
        final var nested = ReactElement.createViewElement(view).input("nested", true);
        final var app = component(false, ignored -> {
            final var nodes = new ReactNode[] {
                ReactElement.createViewElement(view).input("bar", "bar"),
                ReactElement.createViewElement(view).input("bar", "bar").input("baz", "baz"),
                ReactElement.createViewElement(view),
                ReactElement.createViewElement(view).input("bar", "bar"),
                ReactElement.createViewElement(view).input("bing", "bing"),
                ReactElement.createViewElement(view),
                ReactElement.createViewElement(view),
                ReactElement.createViewElement(view).input("qux", "qux"),
                ReactElement.createFragment(
                        "fragment",
                        context.provide(null, context.consumer().render(value -> nested.dup())),
                        nested.dup(),
                        nested.dup())
            };
            final var foo =
                    ReactElement.createViewElement(view).input("foo", "foo").input("children", array((Object[]) nodes));
            return ReactElement.createViewElement(view).input("children", foo);
        });
        root.render(ReactElement.createViewElement(app), null);
        final var record = props(Objects.requireNonNull(root.getTestTree()));
        check(null == record.get("return"), "The committed fixture root has no parent");
        final var outer = props(Objects.requireNonNull(snapshot(root).get("child")));
        final var outerHost = props(Objects.requireNonNull(outer.get("child")));
        final var foo = props(Objects.requireNonNull(outerHost.get("child")));
        final var fooInputs = props(Objects.requireNonNull(foo.get("memoizedProps")));
        equal(9, Js.<Object[]>uncheckedCast(fooInputs.get("children")).length);
        check(Js.isTripleEqual(view, foo.get("type")), "The foo wrapper carries its original function type");
        final var fooHost = props(Objects.requireNonNull(foo.get("child")));
        check(fooHost.get("return") == foo, "The published host is attached to the foo wrapper");
        equal("RCTView", fooHost.get("type"));
        final var children = new ArrayList<JsPropertyMap<@Nullable Object>>();
        exposedChildren(fooHost, children);
        int found = 0;
        for (final var node : children) {
            final var inputs = props(Objects.requireNonNull(node.get("memoizedProps")));
            if (Boolean.TRUE.equals(inputs.get("nested"))) {
                check(
                        Js.isTripleEqual(view, node.get("type")),
                        "Fragments and context are transparent to the nested view");
                found++;
            }
        }
        equal(3, found);
    }

    public static void functionRef(boolean development) {
        final var root = new IsolatedReconcilerTest.Harness().memory.createTestRoot(false);
        final var bar = component(true, ignored -> textHost("div", "Hello, world"));
        props(bar).set("displayName", "Bar");
        final var foo = component(false, ignored -> {
            final var child = ReactElement.createViewElement(bar);
            props(child).set("ref", "foo");
            props(child).set("_source", JsPropertyMap.of("fileName", "IsolatedTestRendererTest.java", "lineNumber", 513));
            return child;
        });
        props(foo).set("displayName", "Foo");
        final var baz =
                component(false, ignored -> ReactElement.createHostElement("div", null, "baz", JsPropertyMap.of()));
        equal("", captureWarnings(() -> root.render(ReactElement.createViewElement(baz), null)));
        final var warning = captureWarnings(() -> root.render(ReactElement.createViewElement(foo), null));
        if (development) {
            check(
                    warning.contains("Function components cannot be given refs. Attempts to access this ref will fail.")
                            && warning.contains("Check the render method of `Foo`.")
                            && warning.contains("in Bar")
                            && warning.contains("in Foo"),
                    "Function-ref diagnostic includes the owner and the target: " + warning);
        } else {
            equal("", warning);
        }
        rootShape(root, "div(text(Hello, world))");
    }

    public static void flushPlainUpdates() {
        final var harness = new IsolatedReconcilerTest.Harness();
        final var root = harness.memory.createTestRoot(true);
        root.render(view(() -> ReactNode.of("Hi")), null);
        rootShape(root, "");
        harness.flush();
        rootShape(root, "text(Hi)");
        root.render(view(() -> ReactNode.of("Bye")), null);
        rootShape(root, "text(Hi)");
        harness.flush();
        rootShape(root, "text(Bye)");
    }

    public static void flushYields(boolean partial) {
        final var harness = new IsolatedReconcilerTest.Harness();
        final var root = harness.memory.createTestRoot(true);
        final var yields = new ArrayList<String>();
        root.render(yieldingSiblings(yields, 1), null);
        if (partial) {
            harness.yieldWhen(() -> 2 <= yields.size());
            check(harness.turn(), "The initial render has scheduled a work slice");
            equal(List.of("A:1", "B:1"), yields);
            rootShape(root, "");
            yields.clear();
            harness.yieldWhen(() -> false);
            harness.flush();
            equal(List.of("C:1"), yields);
            rootShape(root, "text(A:1),text(B:1),text(C:1)");
        } else {
            harness.flush();
            equal(List.of("A:1", "B:1", "C:1"), yields);
            rootShape(root, "text(A:1),text(B:1),text(C:1)");
            yields.clear();
            root.render(yieldingSiblings(yields, 2), null);
            harness.flush();
            equal(List.of("A:2", "B:2", "C:2"), yields);
            rootShape(root, "text(A:2),text(B:2),text(C:2)");
        }
    }

    public static void interruptedUpdate() {
        final var harness = new IsolatedReconcilerTest.Harness();
        final var root = harness.memory.createTestRoot(true);
        final var yields = new ArrayList<String>();
        final var mounts = new ArrayList<Integer>();
        final var first = ReactElement.createViewElement(INTERRUPTED)
                .input("step", 1)
                .input("yields", yields)
                .input("mounts", mounts);
        root.render(first, null);
        harness.yieldWhen(() -> !yields.isEmpty());
        check(harness.turn(), "The first priority schedules a work slice");
        equal(List.of("A:1"), yields);
        rootShape(root, "");
        yields.clear();
        harness.memory.flushSync(() -> root.render(first.dup().input("step", 2), null));
        equal(List.of("A:2", "B:2"), yields);
        equal(List.of(2), mounts);
        rootShape(root, "text(A:2),text(B:2)");
    }

    public static void flushErrors() {
        final var harness = new IsolatedReconcilerTest.Harness();
        final var root = harness.memory.createTestRoot(true);
        final var yields = new ArrayList<String>();
        for (int update = 0; update < 3; update++) {
            final var tree = host(
                    "div",
                    null,
                    array(
                            yielding(yields, "A"),
                            yielding(yields, "B"),
                            view(() -> {
                                throw new IllegalStateException("Oh no!");
                            }),
                            yielding(yields, "C"),
                            yielding(yields, "D")));
            root.render(tree, null);
            captureWarnings(() -> {
                try {
                    harness.flush();
                    throw new AssertionError("Expected a render failure during the flush");
                } catch (IllegalStateException expected) {
                    equal("Oh no!", expected.getMessage());
                }
            });
            equal(List.of("A", "B", "C", "D", "A", "B", "C", "D"), yields);
            yields.clear();
        }
    }

    private static ReactNode yieldingSiblings(List<String> yields, int step) {
        return array(yielding(yields, "A:" + step), yielding(yields, "B:" + step), yielding(yields, "C:" + step));
    }

    private static ReactElement yielding(List<String> yields, String label) {
        return view(() -> {
            yields.add(label);
            return ReactNode.of(label);
        });
    }

    private static void assertUnstyledSpans(MemoryRenderer.Root root) {
        assertProps(first(root), null, null);
        for (final var child : Js.<Object[]>uncheckedCast(props(first(root)).get("children"))) {
            assertProps(child, null, null);
        }
    }

    private static void assertProps(Object node, @Nullable String property, @Nullable Object value) {
        final var values = props(Objects.requireNonNull(props(node).get("props")));
        final var names = new ArrayList<String>();
        values.forEach(name -> {
            if (!"children".equals(name)) {
                names.add(name);
            }
        });
        equal(null == property ? List.of() : List.of(property), names);
        if (null != property) {
            equal(Objects.requireNonNull(value), values.get(property));
        }
    }

    private static JsPropertyMap<@Nullable Object> snapshot(MemoryRenderer.Root root) {
        return props(Objects.requireNonNull(
                props(Objects.requireNonNull(root.getTestTree())).get("child")));
    }

    private static Object first(MemoryRenderer.Root root) {
        return Js.<Object[]>uncheckedCast(root.getChildren())[0];
    }

    private static Object firstChild(Object parent) {
        return Js.<Object[]>uncheckedCast(props(parent).get("children"))[0];
    }

    private static ReactElement keyed(String label) {
        final var element = host("span", null, ReactNode.of(label));
        element.setKey(label.toLowerCase(Locale.ROOT));
        return element;
    }

    private static ReactElement tracked(String key, String name, List<String> trace) {
        final var element =
                ReactElement.createViewElement(TRACKED).input("name", name).input("trace", trace);
        element.setKey(key);
        return element;
    }

    private static ReactElement view(Supplier<@Nullable ReactNode> output) {
        return ReactElement.createViewElement(OUTPUT).input("output", output);
    }

    private record TextCase(ReactNode children, String shape) {}

    private static final class Lifecycle extends NativeView implements OnComponentDidMount, OnComponentWillUnmount {
        @JsConstructor
        Lifecycle(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        private void record(String phase) {
            final var inputs = requireInputs();
            Js.<List<String>>uncheckedCast(inputs.get("trace")).add(phase + " " + inputs.get("name"));
        }

        @Override
        public ReactNode render() {
            record("render");
            return host("div", null, null);
        }

        @Override
        public void componentDidMount() {
            record("mount");
        }

        @Override
        public void componentWillUnmount() {
            record("unmount");
        }
    }

    private static final class UpdateOnMount extends NativeView implements OnComponentDidMount {
        @JsConstructor
        UpdateOnMount(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("x", 3));
        }

        @Override
        public ReactNode render() {
            final var renders = Js.<int[]>uncheckedCast(requireInputs().get("renders"));
            renders[0]++;
            return host(
                            "div",
                            null,
                            array(
                                    ReactNode.of(Js.coerceToInt(
                                            Objects.requireNonNull(state()).get("x"))),
                                    view(() -> {
                                        renders[0]++;
                                        return host("moo", null, null);
                                    }),
                                    view(() -> {
                                        renders[0]++;
                                        return null;
                                    })))
                    .input("className", "purple");
        }

        @Override
        public void componentDidMount() {
            setState(JsPropertyMap.of("x", 7));
        }
    }

    private static final class Mouse extends NativeView {
        @JsConstructor
        Mouse(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("mouse", "mouse"));
        }

        private void handleMoose() {
            setState(JsPropertyMap.of("mouse", "moose"));
        }

        @Override
        public ReactNode render() {
            return host(
                    "div",
                    null,
                    ReactNode.of(Js.asString(Objects.requireNonNull(state()).get("mouse"))));
        }
    }

    private static final class TextHost extends NativeView {
        @JsConstructor
        TextHost(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return host("div", null, Js.uncheckedCast(requireInputs().get("children")));
        }
    }

    private static final class Boundary extends NativeView implements OnComponentDidMount, OnComponentDidCatch {
        @JsConstructor
        Boundary(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        private List<String> trace() {
            return Js.uncheckedCast(requireInputs().get("trace"));
        }

        @Override
        public ReactNode render() {
            trace().add("Boundary render");
            return host(
                    "div",
                    null,
                    null == state()
                            ? array(
                                    host("button", null, ReactNode.of("ClickMe")),
                                    ReactElement.createViewElement(ANGRY).input("trace", trace()))
                            : ReactNode.of("Happy Birthday!"));
        }

        @Override
        public void componentDidMount() {
            trace().add("Boundary componentDidMount");
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            trace().add("Boundary componentDidCatch");
            setState(JsPropertyMap.of("error", true));
        }
    }

    private static final class Interrupted extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        @JsConstructor
        Interrupted(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final int step = Js.coerceToInt(requireInputs().get("step"));
            final var yields = Js.<List<String>>uncheckedCast(requireInputs().get("yields"));
            return array(yielding(yields, "A:" + step), yielding(yields, "B:" + step));
        }

        @Override
        public void componentDidMount() {
            Js.<List<Integer>>uncheckedCast(requireInputs().get("mounts"))
                    .add(Js.coerceToInt(requireInputs().get("step")));
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            throw new AssertionError("Unexpected lower-priority update");
        }
    }

    private static final class Angry extends NativeView {
        @JsConstructor
        Angry(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            Js.<List<String>>uncheckedCast(requireInputs().get("trace")).add("Angry render");
            throw new IllegalStateException("Angry");
        }
    }
}
