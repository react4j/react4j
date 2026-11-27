package react4j.dom;

import static react4j.dom.IsolatedReconcilerTest.array;
import static react4j.dom.IsolatedReconcilerTest.check;
import static react4j.dom.IsolatedReconcilerTest.equal;
import static react4j.dom.IsolatedReconcilerTest.host;
import static react4j.dom.IsolatedReconcilerTest.rootShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedSchedulingTest {
    private IsolatedSchedulingTest() {}

    public static void hostContext(boolean withContainer) {
        final var callbacks = new HostCallbacks(withContainer);
        final var fixture = new HostFixture(callbacks);
        final var container = fixture.createContainer(withContainer ? callbacks.context : null);
        fixture.updateContainer(host("a", null, host("b", null, null)), container, null);
        if (withContainer) {
            equal(1, callbacks.before);
            equal(1, callbacks.after);
        } else {
            equal(2, callbacks.creates);
        }
    }

    private static final class HostCallbacks implements HostFixture.Callbacks {
        final Object context = new Object();
        final boolean withContainer;
        int creates;
        int before;
        int after;

        HostCallbacks(boolean withContainer) {
            this.withContainer = withContainer;
        }

        @Override
        public @Nullable Object getRootHostContext(@Nullable Object container) {
            return null;
        }

        @Override
        public @Nullable Object getChildHostContext(
                @Nullable Object parentContext, Object type, @Nullable Object container) {
            return null;
        }

        @Override
        public boolean shouldSetTextContent(Object type, Object props) {
            return false;
        }

        @Override
        public @Nullable Object createInstance(
                Object type, Object props, @Nullable Object container, @Nullable Object context, Object fiber) {
            creates++;
            return withContainer ? null : Js.undefined();
        }

        @Override
        public @Nullable Object createTextInstance(
                String text, @Nullable Object container, @Nullable Object context, Object fiber) {
            throw new AssertionError("Host context fixture does not render text");
        }

        @Override
        public @Nullable Object finalizeInitialChildren(
                @Nullable Object instance,
                Object type,
                Object props,
                @Nullable Object container,
                @Nullable Object context) {
            return null;
        }

        @Override
        public void appendInitialChild(@Nullable Object parent, @Nullable Object child) {}

        @Override
        public void appendChildToContainer(@Nullable Object container, @Nullable Object child) {}

        @Override
        public void prepareForCommit(@Nullable Object container) {
            if (withContainer) {
                check(context == container, "prepareForCommit receives the original container");
            }
            before++;
        }

        @Override
        public void resetAfterCommit(@Nullable Object container) {
            if (withContainer) {
                check(context == container, "resetAfterCommit receives the original container");
            }
            after++;
        }

        @Override
        public double now() {
            return 0;
        }
    }

    public static void expiration(int number) {
        final var harness = new IsolatedReconcilerTest.Harness();
        final var root = harness.memory.createRoot(true);
        if (0 == number) {
            root.render(host("span", null, null).input("prop", "done"), null);
            rootShape(root, "");
            harness.memory.flushExpired();
            rootShape(root, "");
            harness.advance(4500);
            harness.memory.flushExpired();
            rootShape(root, "");
            harness.advance(1000);
            harness.memory.flushExpired();
            rootShape(root, "span[done]()");
        } else if (3 == number) {
            overlappingState(harness, root);
        } else {
            eventBatch(harness, root, 2 == number);
        }
    }

    private static void eventBatch(IsolatedReconcilerTest.Harness harness, MemoryRenderer.Root root, boolean sync) {
        final var trace = new ArrayList<String>();
        final ViewConstructorFunction text = CommitText::new;
        final var a = ReactElement.createViewElement(text).input("text", "A").input("trace", trace);
        final var b = a.dup().input("text", "B");
        root.render(a, null);
        harness.advance(2000);
        harness.memory.flushExpired();
        equal(List.of(), trace);
        rootShape(root, "");
        root.render(b, null);
        harness.flush();
        equal(List.of("A [render]", "A [commit]", "B [render]", "B [commit]"), trace);
        rootShape(root, "span[B]()");
        trace.clear();

        root.render(a.dup(), null);
        harness.advance(2000);
        equal(List.of(), trace);
        rootShape(root, "span[B]()");
        if (sync) {
            final var second = harness.memory.createRoot(true);
            harness.memory.flushSync(() -> second.render(ReactNode.of("1"), null));
        }
        root.render(b.dup(), null);
        harness.flush();
        equal(List.of("B [render]", "B [commit]"), trace);
    }

    private static void overlappingState(IsolatedReconcilerTest.Harness harness, MemoryRenderer.Root root) {
        final var trace = new ArrayList<String>();
        final var subscribers = new ArrayList<Connected>();
        final ViewConstructorFunction wrapper = IsolatedReconcilerTest.Output::new;
        final ViewConstructorFunction connect = Connected::new;
        final Supplier<ReactNode> output = () -> array(
                connected(connect, trace, subscribers, "A"),
                connected(connect, trace, subscribers, "B"),
                connected(connect, trace, subscribers, "C"),
                connected(connect, trace, subscribers, "D"));
        harness.render(root, ReactElement.createViewElement(wrapper).input("output", output));
        equal(
                List.of(
                        "initial [A] [render]",
                        "initial [B] [render]",
                        "initial [C] [render]",
                        "initial [D] [render]",
                        "initial [A] [commit]",
                        "initial [B] [commit]",
                        "initial [C] [commit]",
                        "initial [D] [commit]"),
                trace);
        trace.clear();
        subscribers.forEach(child -> child.setState(JsPropertyMap.of("text", "1")));
        untilYield(harness, trace, 2);
        equal(List.of("1 [A] [render]", "1 [B] [render]"), trace);
        trace.clear();

        subscribers.forEach(child -> child.setState(JsPropertyMap.of("text", "2")));
        untilYield(harness, trace, 2);
        equal(List.of("1 [C] [render]", "1 [D] [render]"), trace);
        harness.flush();
    }

    private static ReactElement connected(
            ViewConstructorFunction type, List<String> trace, List<Connected> subscribers, String label) {
        return ReactElement.createViewElement(type)
                .input("trace", trace)
                .input("subscribers", subscribers)
                .input("label", label);
    }

    static void untilYield(IsolatedReconcilerTest.Harness harness, List<String> trace, int count) {
        harness.yieldWhen(() -> trace.size() >= count);
        try {
            while (trace.size() < count && harness.turn()) {
                // Continue until the requested observable render events occur.
            }
        } finally {
            harness.yieldWhen(() -> false);
        }
    }

    public static void scheduling(int number, boolean development) {
        final var harness = new IsolatedReconcilerTest.Harness();
        final var root = harness.memory.createRoot(true);
        switch (number) {
            case 0 -> {
                submit(root, "1");
                rootShape(root, "");
                harness.flush();
                rootShape(root, "span[1]()");
            }
            case 1, 4 -> {
                final var b = harness.memory.createRoot(true);
                final var c = harness.memory.createRoot(true);
                submit(root, "a:1");
                submit(b, "b:1");
                submit(c, "c:1");
                harness.flush();
                roots(root, b, c, "a:1", "b:1", "c:1");
                if (4 == number) {
                    submit(c, "c:2");
                    submit(b, "b:2");
                    workUnits(harness, 3);
                    roots(root, b, c, "a:1", "b:1", "c:2");
                    submit(root, "a:2");
                    workUnits(harness, 3);
                    roots(root, b, c, "a:1", "b:2", "c:2");
                    workUnits(harness, 3);
                    roots(root, b, c, "a:2", "b:2", "c:2");
                }
            }
            case 2, 3 -> {
                submit(root, 1);
                harness.flush();
                rootShape(root, "span[1]()");
                if (2 == number) {
                    harness.memory.batchedUpdates(() -> {
                        submit(root, 5);
                        harness.memory.flushSync(() -> {
                            submit(root, 2);
                            submit(root, 3);
                            submit(root, 4);
                        });
                    });
                    rootShape(root, "span[4]()");
                } else {
                    for (int i = 2; i <= 5; i++) {
                        submit(root, i);
                    }
                }
                harness.flush();
                rootShape(root, 2 == number ? "span[4]()" : "span[5]()");
            }
            case 5, 6, 7, 9 -> commitScheduling(harness, root, number);
            case 8 -> {
                harness.memory.flushSync(() -> {
                    submit(root, 0);
                    rootShape(root, "");
                    harness.memory.unbatchedUpdates(() -> {
                        submit(root, 1);
                        rootShape(root, "span[1]()");
                        submit(root, 2);
                        rootShape(root, "span[2]()");
                    });
                    submit(root, 3);
                    rootShape(root, "span[2]()");
                });
                rootShape(root, "span[3]()");
            }
            case 10 -> {
                final var trace = new ArrayList<String>();
                final ViewConstructorFunction type = inputs -> {
                    final var child = new CallbackProbe(inputs);
                    child.configure(harness, trace);
                    return child;
                };
                IsolatedReconcilerTest.props(type).set("childContextTypes", JsPropertyMap.of());
                IsolatedReconcilerTest.props(type).set("displayName", "Foo");
                final var first = ReactElement.createViewElement(type).input("step", 1);
                final var warning = IsolatedReconcilerTest.captureWarnings(() -> harness.render(root, first));
                if (development) {
                    check(
                            warning.contains("componentWillReceiveProps: Please update the following components "
                                    + "to use static getDerivedStateFromProps instead: Foo"),
                            "Legacy receive-props warning");
                }
                harness.render(root, first.dup().input("step", 2));
                equal(List.of("has callback before setState: false", "has callback after setState: false"), trace);
            }
            default -> throw new AssertionError(number);
        }
    }

    private static void submit(MemoryRenderer.Root root, Object value) {
        root.render(host("span", null, null).input("prop", value), null);
    }

    private static void roots(
            MemoryRenderer.Root a,
            MemoryRenderer.Root b,
            MemoryRenderer.Root c,
            String expectedA,
            String expectedB,
            String expectedC) {
        rootShape(a, "span[" + expectedA + "]()");
        rootShape(b, "span[" + expectedB + "]()");
        rootShape(c, "span[" + expectedC + "]()");
    }

    static void workUnits(IsolatedReconcilerTest.Harness harness, int units) {
        final int[] remaining = {units};
        harness.yieldWhen(() -> 0 >= remaining[0]--);
        try {
            while (0 <= remaining[0] && harness.turn()) {
                // A turn can finish earlier than the allotted render work.
            }
        } finally {
            harness.yieldWhen(() -> false);
        }
    }

    private static void commitScheduling(IsolatedReconcilerTest.Harness harness, MemoryRenderer.Root root, int number) {
        final var trace = new ArrayList<String>();
        final @Nullable Steps[] instance = {null};
        final ViewConstructorFunction type = inputs -> {
            final var child = new Steps(inputs);
            instance[0] = child;
            return child.configure(harness, root, trace, number);
        };
        final var initial = ReactElement.createViewElement(type);
        if (6 == number) {
            harness.memory.flushSync(() -> root.render(initial, null));
            rootShape(root, "span[0]()");
            trace.clear();
            harness.flush();
            equal(List.of("render: 1", "componentDidUpdate: 1"), trace);
            rootShape(root, "span[1]()");
            trace.clear();
            Objects.requireNonNull(instance[0]).setStep(2);
            untilYield(harness, trace, 4);
            equal(
                    List.of(
                            "render: 2",
                            "componentDidUpdate: 2",
                            "componentDidUpdate (before setState): 2",
                            "componentDidUpdate (after setState): 2"),
                    trace);
            rootShape(root, "span[2]()");
            trace.clear();
            harness.flush();
            equal(List.of("render: 3", "componentDidUpdate: 3"), trace);
            rootShape(root, "span[3]()");
        } else if (5 == number) {
            root.render(initial, null);
            workUnits(harness, 4);
            equal(
                    List.of(
                            "render: 0",
                            "componentDidMount (before setState): 0",
                            "componentDidMount (after setState): 0",
                            "render: 1",
                            "componentDidUpdate: 1"),
                    trace);
            trace.clear();
            Objects.requireNonNull(instance[0]).setStep(2);
            workUnits(harness, 4);
            equal(
                    List.of(
                            "render: 2",
                            "componentDidUpdate: 2",
                            "componentDidUpdate (before setState): 2",
                            "componentDidUpdate (after setState): 2",
                            "render: 3",
                            "componentDidUpdate: 3"),
                    trace);
        } else if (7 == number) {
            root.render(initial, null);
            workUnits(harness, 3);
            rootShape(root, "");
            workUnits(harness, 1);
            rootShape(root, "span[5]()");
        } else {
            harness.render(root, initial);
            rootShape(root, "span[0]()");
            harness.memory.flushSync(() -> Objects.requireNonNull(instance[0]).setStep(1));
            rootShape(root, "span[2]()");
            equal(
                    List.of("render: 0", "render: 1", "componentDidUpdate: 1", "render: 2", "componentDidUpdate: 2"),
                    trace);
        }
    }

    @JsFunction
    private interface Hook {
        void run();
    }

    private static final class CallbackProbe extends NativeView {
        @JsConstructor
        CallbackProbe(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        void configure(IsolatedReconcilerTest.Harness harness, List<String> trace) {
            IsolatedReconcilerTest.props(this).set("UNSAFE_componentWillReceiveProps", (Hook) () -> {
                trace.add("has callback before setState: " + harness.hasScheduledWork());
                setState(JsPropertyMap.of("foo", "baz"));
                trace.add("has callback after setState: " + harness.hasScheduledWork());
            });
        }

        @Override
        public @Nullable ReactNode render() {
            return null;
        }
    }

    private static final class Steps extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        private IsolatedReconcilerTest.@Nullable Harness _harness;
        private MemoryRenderer.@Nullable Root _root;
        private @Nullable List<String> _trace;
        private int _scenario;

        @JsConstructor
        Steps(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        Steps configure(
                IsolatedReconcilerTest.Harness harness, MemoryRenderer.Root root, List<String> trace, int scenario) {
            _harness = harness;
            _root = root;
            _trace = trace;
            _scenario = scenario;
            applyState(JsPropertyMap.of("step", 7 == scenario ? 1 : 0));
            return this;
        }

        int step() {
            return Js.coerceToInt(Objects.requireNonNull(state()).get("step"));
        }

        void setStep(int step) {
            setState(JsPropertyMap.of("step", step));
        }

        private void record(String name) {
            Objects.requireNonNull(_trace).add(name + ": " + step());
        }

        private void updateStep(String lifecycle, int value) {
            record(lifecycle + " (before setState)");
            setStep(value);
            record(lifecycle + " (after setState)");
        }

        @Override
        public ReactNode render() {
            record("render");
            return host("span", null, null).input("prop", step());
        }

        @Override
        public void componentDidMount() {
            switch (_scenario) {
                case 5 -> updateStep("componentDidMount", 1);
                case 6 ->
                    Objects.requireNonNull(_harness).memory.deferredUpdates(() -> updateStep("componentDidMount", 1));
                case 7 ->
                    setStateWithCallback(
                            JsPropertyMap.of("step", 2),
                            () -> setStateWithCallback(
                                    JsPropertyMap.of("step", 3),
                                    () -> setStateWithCallback(JsPropertyMap.of("step", 4), () -> setStep(5))));
                default -> {}
            }
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            final var memory = Objects.requireNonNull(_harness).memory;
            if (6 == _scenario) {
                memory.deferredUpdates(this::advanceAfterUpdate);
            } else if (9 == _scenario) {
                record("componentDidUpdate");
                if (1 == step()) {
                    memory.unbatchedUpdates(() -> setStep(2));
                    rootShape(Objects.requireNonNull(_root), "span[1]()");
                }
            } else {
                advanceAfterUpdate();
            }
        }

        private void advanceAfterUpdate() {
            record("componentDidUpdate");
            if (2 == step() && 7 != _scenario) {
                updateStep("componentDidUpdate", 3);
            }
        }
    }

    private static class CommitText extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        @JsConstructor
        CommitText(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Nullable
        Object text() {
            return requireInputs().get("text");
        }

        String display() {
            return String.valueOf(text());
        }

        private void log(String phase) {
            Js.<List<String>>uncheckedCast(requireInputs().get("trace")).add(display() + " [" + phase + "]");
        }

        @Override
        public ReactNode render() {
            log("render");
            return host("span", null, null).input("prop", text());
        }

        @Override
        public void componentDidMount() {
            log("commit");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            log("commit");
        }
    }

    private static final class Connected extends CommitText {
        @JsConstructor
        Connected(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("text", "initial"));
        }

        @Override
        @Nullable
        Object text() {
            return Objects.requireNonNull(state()).get("text");
        }

        @Override
        String display() {
            return text() + " [" + requireInputs().get("label") + "]";
        }

        @Override
        public void componentDidMount() {
            Js.<List<Connected>>uncheckedCast(requireInputs().get("subscribers"))
                    .add(this);
            super.componentDidMount();
        }
    }
}
