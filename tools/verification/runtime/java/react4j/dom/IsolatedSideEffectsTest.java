package react4j.dom;

import static react4j.dom.IsolatedReconcilerTest.array;
import static react4j.dom.IsolatedReconcilerTest.captureWarnings;
import static react4j.dom.IsolatedReconcilerTest.check;
import static react4j.dom.IsolatedReconcilerTest.equal;
import static react4j.dom.IsolatedReconcilerTest.host;
import static react4j.dom.IsolatedReconcilerTest.props;
import static react4j.dom.IsolatedReconcilerTest.rootShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedSideEffectsTest {
    private IsolatedSideEffectsTest() {}

    public static void cases(int number, boolean development) {
        final var f = new Fixture();
        switch (number) {
            case 0 -> {
                f.render(() -> div(f.view(() -> host("span", null, ReactNode.of("Hello")))));
                f.expect("div(span())");
                f.render(() -> div(
                        f.view(() -> host("span", null, ReactNode.of("World"))),
                        f.view(() -> host("span", null, ReactNode.of("World")))));
                f.expect("div(span(),span())");
            }
            case 1 -> {
                f.render(() -> div(f.view(() -> host("span", null, ReactNode.of("Hello"))), span("test")));
                f.expect("div(span(),span[test]())");
                f.render(() -> div(
                        f.view(() -> host("span", null, ReactNode.of("World"))),
                        array(keyed("a", f.view(() -> host("span", null, ReactNode.of("World")))), keyed("b", div())),
                        span("test")));
                f.expect("div(span(),span(),div(),span[test]())");
                f.render(() -> div(
                        f.view(() -> host("span", null, ReactNode.of("Hi"))),
                        array(keyed("b", div()), keyed("a", f.view(() -> host("span", null, ReactNode.of("Hi"))))),
                        span("test")));
                f.expect("div(span(),div(),span(),span[test]())");
            }
            case 2 -> {
                f.render(() -> div(f.view(() -> ReactNode.of("Hello"))));
                f.expect("div(text(Hello))");
                f.render(() -> div(
                        f.view(() -> ReactNode.of("World")),
                        array(keyed("a", f.view(() -> ReactNode.of("World"))), ReactNode.of("!"))));
                f.expect("div(text(World),text(World),text(!))");
            }
            case 3 -> {
                f.render(() -> div(f.mixedChildren()));
                f.expect("div(div(),span[Hello](),text(World))");
                f.render(IsolatedSideEffectsTest::div);
                f.expect("div()");
            }
            case 4, 5 -> {
                final var trace = new ArrayList<String>();
                final var component = f.probe(trace, "Class", () -> span("Class"));
                final var replacement = f.view(() -> span("Function"));
                if (5 == number) {
                    component.setKey("a");
                    replacement.setKey("a");
                }
                f.render(() -> div(component, ReactNode.of("Trail")));
                f.expect("div(span[Class](),text(Trail))");
                equal(List.of("mount:Class"), trace);
                f.render(() -> div(replacement, ReactNode.of("Trail")));
                f.expect("div(span[Function](),text(Trail))");
                equal(List.of("mount:Class", "Class"), trace);
                if (4 == number) {
                    f.render(() -> div(ReactNode.of("Text"), ReactNode.of("Trail")));
                    f.expect("div(text(Text),text(Trail))");
                }
                f.render(() -> div(null, ReactNode.of("Trail")));
                f.expect("div(text(Trail))");
            }
            case 6, 7 -> portals(f, 6 == number);
            case 8 -> {
                f.render(() ->
                        div(div(f.view(() -> span("Hello")), f.view(() -> span("Hello"))), f.view(() -> span("Yo"))));
                f.expect("div(div(span[Hello](),span[Hello]()),span[Yo]())");
                final boolean[] rendered = {false};
                f.root.render(
                        f.view(() -> div(
                                div(f.view(() -> {
                                    rendered[0] = true;
                                    return span("World");
                                })),
                                f.view(() -> span("Yo")))),
                        null);
                f.harness.yieldWhen(() -> rendered[0]);
                check(f.harness.turn() && rendered[0], "Ran part of the root update");
                f.expect("div(div(span[Hello](),span[Hello]()),span[Yo]())");
                f.harness.yieldWhen(() -> false);
                f.harness.flush();
                f.expect("div(div(span[World]()),span[Yo]())");
            }
            case 9 -> {
                f.render(() -> div(div(f.view(() -> span("foo"))).input("hidden", true)));
                hiddenChild(f, true, "foo");
                f.root.render(f.view(() -> div(div(f.view(() -> span("bar"))).input("hidden", true))), null);
                partial(f, 4);
                hiddenChild(f, true, "foo");
                f.harness.flush();
                hiddenChild(f, true, "bar");
            }
            case 10, 11 -> interruptedHidden(f, 11 == number);
            case 12 -> pendingCommit(f);
            case 13 -> {
                f.render(() -> div(span(1)).input("hidden", true));
                hiddenChild(f, false, 1);
            }
            case 16 -> hiddenState(f);
            case 17, 18 -> callbacks(f, 18 == number);
            case 19, 20 -> lifecycle(f, 19 == number);
            case 21 -> refs(f, development);
            case 22 -> stringRef(f, development);
            default -> throw new AssertionError(number);
        }
    }

    private static void portals(Fixture f, boolean toggle) {
        final var target = f.harness.memory.createRoot(true);
        final var content = "div(),span[Hello](),text(World)";
        f.render(() -> div(f.harness.memory.createPortal(f.mixedChildren(), target, null)));
        f.expect("div()");
        rootShape(target, content);
        if (toggle) {
            f.render(() -> div(f.harness.memory.createPortal(array(), target, null)));
            f.expect("div()");
            rootShape(target, "");
            f.render(() -> div(f.harness.memory.createPortal(f.mixedChildren(), target, null)));
            f.expect("div()");
            rootShape(target, content);
        }
        f.harness.render(f.root, null);
        f.expect("");
        rootShape(target, "");
        if (toggle) {
            f.render(() -> f.harness.memory.createPortal(array(), target, null));
            f.expect("");
            rootShape(target, "");
        }
        f.render(() -> f.harness.memory.createPortal(f.mixedChildren(), target, null));
        f.expect("");
        rootShape(target, content);
        f.harness.render(f.root, null);
        f.expect("");
        rootShape(target, "");
    }

    private static void partial(Fixture f, int units) {
        final int[] remaining = {units};
        f.harness.yieldWhen(() -> 0 >= remaining[0]--);
        try {
            check(f.harness.turn(), "Expected pending work");
        } finally {
            f.harness.yieldWhen(() -> false);
        }
    }

    private static void interruptedHidden(Fixture f, boolean predicate) {
        final var trace = new ArrayList<String>();
        final ViewConstructorFunction contentType = SelectedContent::new;
        final ViewConstructorFunction labelType = SelectedLabel::new;
        final var stableContent = div(label(labelType, trace, "Hello"), label(labelType, trace, "World"));
        final BiFunction<Integer, String, ReactNode> output = (step, text) -> {
            final var child = predicate
                    ? ReactElement.createViewElement(contentType)
                            .input("step", step.intValue())
                            .input("text", text)
                            .input("labels", labelType)
                            .input("trace", trace)
                    : 0 == step ? div(label(labelType, trace, "Hi"), label(labelType, trace, text)) : stableContent;
            return div(child).input("hidden", true);
        };
        f.render(() -> output.apply(0, "foo"));
        final var before = "div(span[Hi](),span[foo]())";
        hiddenShape(f, before);
        trace.clear();
        f.root.render(f.view(() -> output.apply(1, "bar")), null);
        IsolatedSchedulingTest.untilYield(f.harness, trace, 2);
        equal(List.of("Hello", "World"), trace);
        hiddenShape(f, before);
        f.root.render(f.view(() -> output.apply(1, "foo")), null);
        f.harness.flush();
        hiddenShape(f, "div(span[Hello](),span[World]())");
    }

    private static ReactElement label(ViewConstructorFunction type, List<String> trace, String text) {
        return ReactElement.createViewElement(type).input("trace", trace).input("label", text);
    }

    private static void hiddenShape(Fixture f, String shape) {
        final var node = first(f.root.getChildren());
        equal("div", props(node).get("type"));
        equal(true, props(node).get("hidden"));
        equal(
                shape,
                IsolatedReconcilerTest.shape(Objects.requireNonNull(props(node).get("children"))));
    }

    private static void hiddenState(Fixture f) {
        final var trace = new ArrayList<String>();
        final var instances = new ArrayList<HiddenState>();
        final ViewConstructorFunction bar = inputs -> new HiddenState(inputs, trace, instances);
        final IntFunction<ReactNode> output = tick -> {
            trace.add("Foo");
            return div(
                    span(tick),
                    div(
                                    ReactElement.createViewElement(bar).input("idx", tick),
                                    ReactElement.createViewElement(bar).input("idx", tick),
                                    ReactElement.createViewElement(bar).input("idx", tick))
                            .input("hidden", true));
        };
        f.render(() -> output.apply(0));
        hiddenStateOutput(f, 0, "span[0](),span[0](),span[0]()");
        equal(List.of("Foo", "Bar", "Bar", "Bar"), trace);
        trace.clear();
        f.root.render(f.view(() -> output.apply(1)), null);
        IsolatedSchedulingTest.untilYield(f.harness, trace, 3);
        hiddenStateOutput(f, 1, "span[0](),span[0](),span[0]()");
        equal(List.of("Foo", "Bar", "Bar"), trace);
        trace.clear();
        instances.get(0).setState(JsPropertyMap.of("active", true));
        IsolatedSchedulingTest.untilYield(f.harness, trace, 1);
        hiddenStateOutput(f, 1, "span[0](),span[0](),span[0]()");
        equal(List.of("Bar"), trace);
        trace.clear();
        f.harness.flush();
        hiddenStateOutput(f, 1, "span[X](),span[1](),span[1]()");
        equal(List.of("Bar", "Bar", "Bar"), trace);
    }

    private static void hiddenStateOutput(Fixture f, int visible, String hidden) {
        final var root = first(f.root.getChildren());
        final var children = Js.<Object[]>uncheckedCast(props(root).get("children"));
        equal(2, children.length);
        equal(visible, props(children[0]).get("prop"));
        equal(true, props(children[1]).get("hidden"));
        equal(
                hidden,
                IsolatedReconcilerTest.shape(
                        Objects.requireNonNull(props(children[1]).get("children"))));
    }

    private static void pendingCommit(Fixture f) {
        f.root.render(f.view(() -> span(1)), null);
        partial(f, 3);
        f.expect("");
        partial(f, 2);
        f.expect("span[1]()");
        f.root.render(f.view(() -> span(2)), null);
        partial(f, 3);
        f.expect("span[1]()");
        f.root.render(f.view(() -> span(3)), null);
        partial(f, 2);
        f.expect("span[2]()");
        f.harness.flush();
        f.expect("span[3]()");
    }

    private static void hiddenChild(Fixture f, boolean nested, Object prop) {
        Object node = first(f.root.getChildren());
        if (nested) {
            equal(false, props(node).get("hidden"));
            node = first(Objects.requireNonNull(props(node).get("children")));
        }
        equal("div", props(node).get("type"));
        equal(true, props(node).get("hidden"));
        equal(
                "span[" + prop + "]()",
                IsolatedReconcilerTest.shape(Objects.requireNonNull(props(node).get("children"))));
    }

    private static Object first(Object array) {
        final var elements = Js.<Object[]>uncheckedCast(array);
        equal(1, elements.length);
        return elements[0];
    }

    private static void callbacks(Fixture f, boolean bailout) {
        final var trace = new ArrayList<String>();
        final ViewConstructorFunction type =
                (JsPropertyMap<@Nullable Object> inputs) -> new Stateful(inputs, bailout, trace);
        final var ref = JsPropertyMap.<@Nullable Object>of("current", null);
        final var view = ReactElement.createViewElement(type);
        props(view).set("ref", ref);
        f.harness.render(f.root, view);
        f.expect("span[foo]()");
        equal(List.of("render"), trace);
        final var instance = Js.<Stateful>uncheckedCast(Objects.requireNonNull(ref.get("current")));
        instance.setStateWithCallback(bailout ? JsPropertyMap.of() : JsPropertyMap.of("text", "bar"), () -> {
            f.expect(bailout ? "span[foo]()" : "span[bar]()");
            trace.add("callback");
        });
        equal(List.of("render"), trace);
        f.harness.flush();
        equal(bailout ? List.of("render", "callback") : List.of("render", "render", "callback"), trace);
    }

    private static void lifecycle(Fixture f, boolean deletion) {
        final var trace = new ArrayList<String>();
        f.render(() -> lifecycleTree(f, trace, true));
        equal(
                List.of(
                        "mount:A",
                        "mount:B",
                        "mount:Wrapper",
                        "mount:C",
                        "mount:D",
                        "mount:Wrapper",
                        "mount:E",
                        "mount:F",
                        "mount:G",
                        "mount:Outside"),
                trace);
        trace.clear();
        f.render(() -> lifecycleTree(f, trace, !deletion));
        equal(
                deletion
                        ? List.of("A", "Wrapper", "B", "C", "Wrapper", "D", "E", "F", "G", "update:Outside")
                        : List.of(
                                "update:A",
                                "update:B",
                                "update:Wrapper",
                                "update:C",
                                "update:D",
                                "update:Wrapper",
                                "update:E",
                                "update:F",
                                "update:G",
                                "update:Outside"),
                trace);
    }

    private static ReactNode lifecycleTree(Fixture f, List<String> trace, boolean show) {
        return div(
                show
                        ? array(
                                keyed("a", f.probe(trace, "A", IsolatedSideEffectsTest::emptySpan)),
                                keyed(
                                        "b",
                                        f.probe(
                                                trace,
                                                "Wrapper",
                                                () -> f.probe(trace, "B", IsolatedSideEffectsTest::emptySpan))),
                                keyed(
                                        "cd",
                                        div(
                                                f.probe(trace, "C", IsolatedSideEffectsTest::emptySpan),
                                                f.probe(
                                                        trace,
                                                        "Wrapper",
                                                        () -> f.probe(
                                                                trace, "D", IsolatedSideEffectsTest::emptySpan)))),
                                array(
                                        keyed("e", f.probe(trace, "E", IsolatedSideEffectsTest::emptySpan)),
                                        keyed("f", f.probe(trace, "F", IsolatedSideEffectsTest::emptySpan))))
                        : array(),
                div(show ? keyed("g", f.probe(trace, "G", IsolatedSideEffectsTest::emptySpan)) : null),
                f.probe(trace, "Outside", IsolatedSideEffectsTest::emptySpan));
    }

    private static void refs(Fixture f, boolean development) {
        final List<@Nullable Object> refs = new ArrayList<>();
        final var classType = (ViewConstructorFunction) Lifetime::new;
        final var functionType = (ViewConstructorFunction) IsolatedReconcilerTest.Output::new;
        props(functionType).set("functionComponent", true);
        props(functionType).set("displayName", "FunctionComponent");
        props(f.output).set("displayName", "Foo");
        final Supplier<ReactNode> output = () -> div(
                attach(
                        ReactElement.createViewElement(classType)
                                .input("name", "Class")
                                .input("trace", new ArrayList<String>())
                                .input("output", (Supplier<ReactNode>) IsolatedSideEffectsTest::emptySpan),
                        refs::add),
                attach(
                        ReactElement.createViewElement(functionType)
                                .input("output", (Supplier<ReactNode>) IsolatedSideEffectsTest::emptySpan),
                        refs::add),
                attach(div(), refs::add));
        final var warning = captureWarnings(() -> f.render(output));
        if (development) {
            check(
                    warning.contains("Function components cannot be given refs")
                            && warning.contains("in FunctionComponent")
                            && warning.contains("in Foo"),
                    "Expected the render owner and component in the function ref warning: " + warning);
        } else {
            equal("", warning);
        }
        equal(2, refs.size());
        final var instance = refs.get(0);
        check(instance instanceof Lifetime, "Class ref resolves to the view");
        final var container =
                Objects.requireNonNull(props(first(f.root.getChildren())).get("children"));
        final var child = Js.<Object[]>uncheckedCast(container)[2];
        check(child == refs.get(1), "Host ref resolves to the committed host");
        refs.clear();
        f.render(output);
        equal(Arrays.asList(null, null, instance, child), refs);
        refs.clear();
        f.harness.render(f.root, null);
        equal(Arrays.asList(null, null), refs);
    }

    private static void stringRef(Fixture f, boolean development) {
        final var ref = JsPropertyMap.<@Nullable Object>of("current", null);
        final var child = f.probe(new ArrayList<>(), "Bar", IsolatedSideEffectsTest::div);
        props(child).set("ref", "bar");
        final var parent = f.view(child::dup);
        props(parent).set("ref", ref);
        final var warning = captureWarnings(() -> f.harness.render(f.root, parent));
        if (development) {
            check(
                    warning.contains("A string ref, \"bar\", has been found within a strict mode tree"),
                    "Expected a named string ref warning: " + warning);
        } else {
            equal("", warning);
        }
        final var owner = Objects.requireNonNull(ref.get("current"));
        final var refs = Objects.requireNonNull(props(owner).get("refs"));
        final var bar = Objects.requireNonNull(props(refs).get("bar"));
        check(bar instanceof Lifetime lifetime && lifetime.mounted, "String ref resolves to the mounted view");
    }

    private static ReactElement attach(ReactElement element, Ref ref) {
        props(element).set("ref", ref);
        return element;
    }

    @JsFunction
    private interface Ref {
        void accept(@Nullable Object value);
    }

    private static ReactElement div(@Nullable ReactNode... children) {
        return host("div", null, array((Object[]) children));
    }

    private static ReactElement span(Object prop) {
        return emptySpan().input("prop", prop);
    }

    private static ReactElement emptySpan() {
        return host("span", null, null);
    }

    private static ReactElement keyed(String key, ReactElement element) {
        element.setKey(key);
        return element;
    }

    private static final class Fixture {
        final IsolatedReconcilerTest.Harness harness = new IsolatedReconcilerTest.Harness();
        final MemoryRenderer.Root root = harness.memory.createRoot(true);
        final ViewConstructorFunction output = IsolatedReconcilerTest.Output::new;
        final ViewConstructorFunction lifetime = Lifetime::new;

        void render(Supplier<ReactNode> supplier) {
            harness.render(root, view(supplier));
        }

        ReactElement view(Supplier<ReactNode> supplier) {
            return ReactElement.createViewElement(output).input("output", supplier);
        }

        ReactNode mixedChildren() {
            return array(keyed("a", div()), keyed("b", view(() -> span("Hello"))), ReactNode.of("World"));
        }

        ReactElement probe(List<String> trace, String name, Supplier<ReactNode> supplier) {
            return ReactElement.createViewElement(lifetime)
                    .input("trace", trace)
                    .input("name", name)
                    .input("output", supplier);
        }

        void expect(String shape) {
            rootShape(root, shape);
        }
    }

    private static final class SelectedContent extends NativeView implements OnShouldComponentUpdate {
        @JsConstructor
        SelectedContent(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> next) {
            return !Objects.equals(requireInputs().get("step"), next.get("step"));
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            final var trace = Js.<List<String>>uncheckedCast(inputs.get("trace"));
            final var labels = Js.<ViewConstructorFunction>uncheckedCast(inputs.get("labels"));
            return 0 == Js.asInt(inputs.get("step"))
                    ? div(label(labels, trace, "Hi"), label(labels, trace, Js.asString(inputs.get("text"))))
                    : div(label(labels, trace, "Hello"), label(labels, trace, "World"));
        }
    }

    private static final class SelectedLabel extends NativeView implements OnShouldComponentUpdate {
        @JsConstructor
        SelectedLabel(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> next) {
            return !Objects.equals(requireInputs().get("label"), next.get("label"));
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            final var label = Js.asString(inputs.get("label"));
            Js.<List<String>>uncheckedCast(inputs.get("trace")).add(label);
            return span(label);
        }
    }

    private static final class HiddenState extends NativeView {
        final List<String> trace;

        @JsConstructor
        HiddenState(JsPropertyMap<@Nullable Object> inputs, List<String> trace, List<HiddenState> instances) {
            super(inputs);
            this.trace = trace;
            instances.add(this);
        }

        @Override
        public ReactNode render() {
            trace.add("Bar");
            final var state = state();
            return span(
                    null != state && Boolean.TRUE.equals(state.get("active"))
                            ? "X"
                            : Js.asInt(requireInputs().get("idx")));
        }
    }

    private static final class Stateful extends NativeView implements OnShouldComponentUpdate {
        final boolean bailout;
        final List<String> trace;

        @JsConstructor
        Stateful(JsPropertyMap<@Nullable Object> inputs, boolean bailout, List<String> trace) {
            super(inputs);
            this.bailout = bailout;
            this.trace = trace;
            applyState(JsPropertyMap.of("text", "foo"));
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> next) {
            return !bailout;
        }

        @Override
        public ReactNode render() {
            trace.add("render");
            return span(Objects.requireNonNull(Objects.requireNonNull(state()).get("text")));
        }
    }

    private static final class Lifetime extends NativeView
            implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount {
        final List<String> trace;
        boolean mounted;

        @JsConstructor
        Lifetime(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            trace = Js.uncheckedCast(inputs.get("trace"));
        }

        String name() {
            return Js.asString(requireInputs().get("name"));
        }

        @Override
        public void componentDidMount() {
            mounted = true;
            trace.add("mount:" + name());
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            trace.add("update:" + name());
        }

        @Override
        public void componentWillUnmount() {
            trace.add(name());
        }

        @Override
        public ReactNode render() {
            return Js.<Supplier<ReactNode>>uncheckedCast(requireInputs().get("output"))
                    .get();
        }
    }
}
