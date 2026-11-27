package react4j.dom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.JsError;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedReconcilerTest {
    private IsolatedReconcilerTest() {}

    public static void fragment(int number) {
        final var harness = new Harness();
        final var root = harness.memory.createRoot(true);
        if (4 > number) {
            final var value =
                    switch (number) {
                        case 0 -> fragment(null, host("span", null, ReactNode.of("foo")));
                        case 1 -> fragment(null);
                        case 2 -> fragment(null, ReactNode.of("hello "), host("span", null, ReactNode.of("world")));
                        case 3 ->
                            fragment(null, iterable(new Object[] {
                                host("span", "a", ReactNode.of("hi")), host("span", "b", ReactNode.of("bye"))
                            }));
                        default -> throw new AssertionError(number);
                    };
            harness.render(root, value);
            rootShape(
                    root,
                    switch (number) {
                        case 0 -> "span()";
                        case 1 -> "";
                        case 2 -> "text(hello ),span()";
                        default -> "span(),span()";
                    });
            return;
        }

        final var updates = new ArrayList<String>();
        final ViewConstructorFunction stateful =
                props -> new Stateful(props).observe(() -> {}, () -> updates.add("Update Stateful"));
        final ViewConstructorFunction wrapper = Output::new;
        final boolean[] condition = {true};
        final Supplier<ReactNode> output = () -> fragmentOutput(number, condition[0], stateful);
        final var test = ReactElement.createViewElement(wrapper).input("output", output);
        harness.render(root, test);
        condition[0] = false;
        harness.render(root, test.dup());
        final boolean retained =
                switch (number) {
                    case 4, 5, 6, 10, 11, 14, 17, 19 -> true;
                    default -> false;
                };
        final var afterUpdate =
                switch (number) {
                    case 4, 6, 9 -> "div(),div()";
                    case 15 -> "div(),span()";
                    case 17 -> "div(span(),div(div()),span())";
                    case 18 -> "div(div(),span())";
                    case 19 -> "span(),div()";
                    default -> "div()";
                };
        equal(retained ? List.of("Update Stateful") : List.of(), updates);
        rootShape(root, afterUpdate);

        condition[0] = true;
        harness.render(root, test.dup());
        equal(retained ? List.of("Update Stateful", "Update Stateful") : List.of(), updates);
        rootShape(
                root,
                switch (number) {
                    case 17, 18, 19 -> afterUpdate;
                    default -> "div()";
                });
    }

    private static ReactNode fragmentOutput(int number, boolean condition, ViewConstructorFunction stateful) {
        final var child = view(stateful, "a");
        final var unkeyed = view(stateful, null);
        final var world = host("div", "b", ReactNode.of("World"));
        return switch (number) {
            case 4 -> condition ? child : fragment(null, child, world);
            case 5 -> fragment(null, unkeyed);
            case 6 ->
                fragment(
                        null,
                        fragment(
                                null,
                                condition ? fragment(null, child) : fragment(null, host("div", null, null), child)));
            case 7 -> condition ? fragment(null, fragment(null, child)) : fragment(null, child);
            case 8 -> condition ? child : fragment(null, fragment(null, child));
            case 9 -> condition ? child : fragment(null, fragment(null, child), host("div", null, null));
            case 10 -> condition ? fragment(null, child) : fragment(null, array(child));
            case 11 -> condition ? array(child) : fragment(null, child);
            case 12 -> condition ? fragment(null, array(child)) : fragment(null, fragment(null, child));
            case 13 -> condition ? fragment(null, array(child)) : array(array(child));
            case 14 -> condition ? fragment(null, fragment(null, child)) : array(array(child));
            case 15 ->
                condition ? fragment("a", unkeyed) : fragment("b", unkeyed, host("span", null, ReactNode.of("World")));
            case 16 -> fragment(condition ? "a" : null, unkeyed);
            case 17 -> {
                final var keyedDiv = host("div", "b", child);
                yield host(
                        "div",
                        null,
                        condition
                                ? array(
                                        fragment("c", host("span", null, ReactNode.of("foo")), keyedDiv),
                                        host("span", null, ReactNode.of("boop")))
                                : array(
                                        host("span", null, ReactNode.of("beep")),
                                        fragment("c", keyedDiv, host("span", null, ReactNode.of("bar")))));
            }
            case 18 ->
                host(
                        "div",
                        null,
                        array(condition ? fragment("foo", unkeyed) : array(unkeyed), host("span", null, null)));
            case 19 -> array(host("span", null, null), fragment(null, unkeyed));
            default -> throw new AssertionError(number);
        };
    }

    public static void topLevelFragment(int number) {
        final var harness = new Harness();
        final var root = harness.memory.createRoot(true);
        final @Nullable Stateful[] instance = {null};
        final ViewConstructorFunction stateful = props -> {
            final var child = new Stateful(props);
            return child.observe(() -> instance[0] = child, () -> {});
        };
        final ViewConstructorFunction wrapper = Output::new;
        final boolean[] condition = {false};
        final Supplier<ReactNode> output = () -> {
            final var child = view(stateful, "a");
            final var world = host("div", "b", ReactNode.of("World"));
            final var third = host("div", "c", null);
            return switch (number) {
                case 0 -> array(host("div", "a", ReactNode.of("Hello")), world);
                case 1 -> condition[0] ? child : array(child, world);
                case 2 -> condition[0] ? child : array(array(child, world), third);
                case 3 -> condition[0] ? array(null, child) : array(world, child);
                case 4 -> condition[0] ? array(array(world, child)) : array(array(child, world), third);
                default -> throw new AssertionError(number);
            };
        };
        final var test = ReactElement.createViewElement(wrapper).input("output", output);
        harness.render(root, test);
        if (0 == number) {
            rootShape(root, "div(),div()");
            return;
        }
        final var previous = Objects.requireNonNull(instance[0]);
        condition[0] = true;
        harness.render(root, test.dup());
        check((2 != number) == (previous == instance[0]), "Unexpected class identity across fragment transition");
        if (3 == number) {
            condition[0] = false;
            harness.render(root, test.dup());
            check(previous == instance[0], "Filling the implicit key slot retains the class instance");
        }
    }

    public static void topLevelText(boolean number) {
        final var harness = new Harness();
        final var root = harness.memory.createRoot(true);
        final ViewConstructorFunction type = Output::new;
        final Supplier<ReactNode> output = () -> number ? Js.uncheckedCast(10) : Js.uncheckedCast("foo");
        harness.render(root, ReactElement.createViewElement(type).input("output", output));
        rootShape(root, number ? "text(10)" : "text(foo)");
    }

    public static void errorBoundary(boolean concurrent, boolean derive, boolean sameType) {
        final var harness = new Harness();
        final var root = harness.memory.createTestRoot(concurrent);
        final ViewConstructorFunction boundary = derive ? Recovering::new : Catching::new;
        if (derive) {
            props(boundary).set("getDerivedStateFromError", (Derive)
                    error -> JsPropertyMap.<@Nullable Object>of("error", error));
        }
        final ViewConstructorFunction broken = Output::new;
        final boolean[] fail = {false};
        final Supplier<ReactNode> output = () -> {
            final var next = host("span", null, null).input("prop", "BrokenRender");
            if (fail[0]) {
                props(next).set("type", Js.undefined());
            }
            return next;
        };
        final var child = ReactElement.createViewElement(broken).input("output", output);
        final var fallback = sameType ? "span" : "div";
        final var tree = ReactElement.createViewElement(boundary)
                .input("children", child)
                .input("fallback", fallback);
        harness.render(root, tree);
        rootShape(root, "span[BrokenRender]()");
        final var previous = Js.<Object[]>uncheckedCast(root.getChildren())[0];
        fail[0] = true;
        captureWarnings(() -> harness.render(root, tree.dup().input("children", child.dup())));
        rootShape(root, fallback + "[ErrorBoundary]()");
        check(
                previous != Js.<Object[]>uncheckedCast(root.getChildren())[0],
                "Fallback replaces the failing host output");
    }

    public static void persistence(int number) {
        final var harness = new Harness();
        final var root = harness.memory.createPersistentRoot(true);
        if (3 == number) {
            persistentPortals(harness, root);
            return;
        }
        final String[] text = {"Hello"};
        final ViewConstructorFunction wrapper = Output::new;
        final ViewConstructorFunction bar = 1 == number ? Bailout::new : Output::new;
        final Supplier<ReactNode> render = () -> {
            final var first = persistentBar(bar, text[0], 1 == number);
            final var second = "World".equals(text[0]) ? persistentBar(bar, text[0], 1 == number) : null;
            final ReactNode children =
                    2 == number ? array(ReactNode.of(text[0]), host("span", null, null)) : array(first, second);
            return host("div", null, children);
        };
        final var app = ReactElement.createViewElement(wrapper).input("output", render);
        harness.render(root, app);
        final var original = root.getChildren();
        final var before =
                switch (number) {
                    case 0 -> "div(span())";
                    case 1 -> "div(span[Hello]())";
                    default -> "div(text(Hello),span())";
                };
        equal(before, shape(original));

        text[0] = "World";
        harness.render(root, app.dup());
        final var updated = root.getChildren();
        equal(
                switch (number) {
                    case 0 -> "div(span(),span())";
                    case 1 -> "div(span[Hello](),span[World]())";
                    default -> "div(text(World),span())";
                },
                shape(updated));
        equal(before, shape(original));
        if (1 == number) {
            check(firstChildOfFirst(original) == firstChildOfFirst(updated), "Unchanged child shares identity");
        }
    }

    private static ReactElement persistentBar(ViewConstructorFunction type, String text, boolean bailout) {
        final Supplier<ReactNode> output =
                () -> bailout ? host("span", null, null).input("prop", text) : host("span", null, ReactNode.of(text));
        return ReactElement.createViewElement(type).input("output", output);
    }

    private static void persistentPortals(Harness harness, MemoryRenderer.Root root) {
        final var target = harness.memory.createPersistentRoot(false);
        final var empty = target.getChildren();
        final ViewConstructorFunction wrapper = Output::new;
        final ViewConstructorFunction bailout = Bailout::new;
        final Supplier<ReactNode> span = () -> host("span", null, null);
        final Supplier<ReactNode> initialChild =
                () -> host("div", null, ReactElement.createViewElement(bailout).input("output", span));
        final Supplier<ReactNode> finalChild = () -> host(
                "div",
                null,
                array(
                        ReactElement.createViewElement(bailout).input("output", span),
                        ReactNode.of("Hello "),
                        ReactNode.of("World")));
        final var child = ReactElement.createViewElement(wrapper).input("output", initialChild);
        harness.render(root, host("div", null, harness.memory.createPortal(child, target, null)));
        equal("", shape(empty));
        final var initialRoot = root.getChildren();
        equal("div()", shape(initialRoot));
        final var initialPortal = target.getChildren();
        equal("div(span())", shape(initialPortal));

        harness.render(
                root,
                host("div", null, harness.memory.createPortal(child.dup().input("output", finalChild), target, null)));
        rootShape(root, "div()");
        final var updatedPortal = target.getChildren();
        equal("div(span(),text(Hello ),text(World))", shape(updatedPortal));
        equal("div()", shape(initialRoot));
        equal("div(span())", shape(initialPortal));
        check(firstChildOfFirst(updatedPortal) == firstChildOfFirst(initialPortal), "Portal reused its stable span");

        harness.render(root, host("div", null, null));
        rootShape(target, "");
        equal("div(span(),text(Hello ),text(World))", shape(updatedPortal));
    }

    private static Object firstChildOfFirst(Object children) {
        final var first = Js.<Object[]>uncheckedCast(children)[0];
        return Js.<Object[]>uncheckedCast(props(first).get("children"))[0];
    }

    public static void minimalism(int number, boolean persistent) {
        final var harness = new Harness();
        final var root = persistent ? harness.memory.createPersistentRoot(true) : harness.memory.createRoot(true);
        final ViewConstructorFunction wrapper = Output::new;
        final ViewConstructorFunction childType = CounterChild::new;
        final var constant = host("div", null, persistentLeaf("world"));
        final @Nullable CounterChild[] counter = {null};
        final Supplier<ReactNode> output =
                switch (number) {
                    case 0 -> () -> host("div", null, ReactNode.of("Hello World"));
                    case 1 -> () -> constant;
                    case 2 ->
                        () -> host(
                                "section",
                                null,
                                host(
                                        "div",
                                        null,
                                        array(
                                                persistentLeaf("world"),
                                                ReactElement.createViewElement(childType)
                                                        .input("instance", (ObserveCounter)
                                                                value -> counter[0] = value),
                                                host("hr", null, null),
                                                persistentLeaf("world"))));
                    default -> throw new AssertionError(number);
                };
        final var app = ReactElement.createViewElement(wrapper).input("output", output);
        harness.render(root, app);
        counters(harness, persistent, 0, 0);

        if (2 == number) {
            Objects.requireNonNull(counter[0]).setState(JsPropertyMap.of("name", "Robin"));
            harness.flush();
            counters(harness, persistent, 3, persistent ? 5 : 4);
        }

        harness.render(root, app.dup());
        switch (number) {
            case 0 -> counters(harness, persistent, 1, 1);
            case 1 -> counters(harness, persistent, 0, 0);
            case 2 -> counters(harness, persistent, 10, 10);
            default -> throw new AssertionError(number);
        }
    }

    private static ReactElement persistentLeaf(String name) {
        return host("span", null, array(ReactNode.of("hello"), host("b", null, null), ReactNode.of(name)));
    }

    private static void counters(Harness harness, boolean persistent, int diffs, int mutations) {
        final var counters = props(persistent ? harness.memory.getPersistentCounters() : harness.memory.getCounters());
        equal(diffs, Js.coerceToInt(counters.get("hostDiffCounter")));
        equal(mutations, Js.coerceToInt(counters.get(persistent ? "hostCloneCounter" : "hostUpdateCounter")));
        harness.memory.resetCounters();
    }

    @JsFunction
    private interface ObserveCounter {
        void observe(CounterChild child);
    }

    private static final class Bailout extends Output implements OnShouldComponentUpdate {
        @JsConstructor
        Bailout(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> inputs) {
            return false;
        }
    }

    private static final class CounterChild extends NativeView {
        @JsConstructor
        CounterChild(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("name", "Batman"));
        }

        @Override
        public ReactNode render() {
            Js.<ObserveCounter>uncheckedCast(requireInputs().get("instance")).observe(this);
            return host(
                    "div",
                    null,
                    persistentLeaf(Js.asString(Objects.requireNonNull(state()).get("name"))));
        }
    }

    static ReactElement view(ViewConstructorFunction type, @Nullable String key) {
        final var node = ReactElement.createViewElement(type);
        node.setKey(key);
        return node;
    }

    static ReactElement fragment(@Nullable String key, @Nullable Object... children) {
        return ReactElement.createFragment(key).input("children", 1 == children.length ? children[0] : children);
    }

    static ReactNode array(@Nullable Object... values) {
        return Js.uncheckedCast(values);
    }

    static ReactElement host(String type, @Nullable String key, @Nullable ReactNode children) {
        return ReactElement.createHostElement(type, key, null, JsPropertyMap.of("children", children));
    }

    static JsPropertyMap<@Nullable Object> props(Object value) {
        return Js.uncheckedCast(value);
    }

    static void rootShape(MemoryRenderer.Root root, String expected) {
        equal(expected, shape(root.getChildren()));
    }

    static String shape(Object children) {
        final var result = new ArrayList<String>();
        for (final var node : Js.<Object[]>uncheckedCast(children)) {
            final var record = props(node);
            equal(false, record.get("hidden"));
            final var type = record.get("type");
            if (null == type) {
                result.add("text(" + record.get("text") + ")");
            } else {
                final var prop = record.get("prop");
                final var subtree = shape(Objects.requireNonNull(record.get("children")));
                result.add(type + (null == prop ? "" : "[" + prop + "]") + "(" + subtree + ")");
            }
        }
        return String.join(",", result);
    }

    static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void equal(Object expected, @Nullable Object actual) {
        check(expected.equals(actual), "Expected " + expected + ", got " + actual);
    }

    @JsMethod
    private static native Object iterable(Object[] elements);

    @JsMethod
    static native String captureWarnings(Reconciler.Task action);

    @JsFunction
    private interface Derive {
        JsPropertyMap<@Nullable Object> update(JsError error);
    }

    static final class Harness implements MemoryRenderer.SchedulerCallbacks {
        private record Scheduled(Reconciler.Task task, double time, boolean timer) {}

        private final LinkedHashMap<Integer, Scheduled> _tasks = new LinkedHashMap<>();
        private int _next;
        private double _now;
        private double _timerNow;
        private BooleanSupplier _shouldYield = () -> false;
        final MemoryRenderer memory = new MemoryRenderer(this);

        void render(MemoryRenderer.Root root, @Nullable ReactNode value) {
            root.render(value, null);
            flush();
        }

        void flush() {
            int turns = 0;
            while (turn()) {
                check(100 > turns++, "Scheduled work should settle");
            }
        }

        boolean turn() {
            for (final var entry : _tasks.entrySet()) {
                final var item = entry.getValue();
                if (item.time() <= _timerNow) {
                    _tasks.remove(entry.getKey());
                    item.task().run();
                    return true;
                }
            }
            return false;
        }

        boolean hasScheduledWork() {
            return !_tasks.isEmpty();
        }

        void advance(int milliseconds) {
            advanceClock(milliseconds);
            _timerNow += milliseconds;
        }

        void advanceClock(int milliseconds) {
            _now += milliseconds;
        }

        void advanceTimers(int milliseconds) {
            final var end = _timerNow + milliseconds;
            for (int turns = 0; ; turns++) {
                Integer handle = null;
                Scheduled event = null;
                for (final var entry : _tasks.entrySet()) {
                    final var candidate = entry.getValue();
                    if (candidate.timer()
                            && candidate.time() <= end
                            && (null == event || candidate.time() < event.time())) {
                        handle = entry.getKey();
                        event = candidate;
                    }
                }
                if (null == event) {
                    _timerNow = end;
                    return;
                }
                check(100 > turns, "Timers should settle");
                _timerNow = event.time();
                _tasks.remove(handle);
                event.task().run();
            }
        }

        @Override
        public double now() {
            return _now;
        }

        @Override
        public boolean strictReplay() {
            return false;
        }

        @Override
        public boolean userTimingEnabled() {
            return false;
        }

        @Override
        public boolean replayFailedRender() {
            return false;
        }

        @Override
        public boolean profilerEnabled() {
            return false;
        }

        @Override
        public Reconciler.@Nullable ProfilerInteractions profilerInteractions() {
            return null;
        }

        @Override
        public boolean shouldYield(double deadline) {
            return _shouldYield.getAsBoolean();
        }

        void yieldWhen(BooleanSupplier shouldYield) {
            _shouldYield = shouldYield;
        }

        @Override
        public int schedule(Reconciler.Task task, int delay) {
            final int handle = ++_next;
            _tasks.put(handle, new Scheduled(task, _timerNow + delay, 0 != delay));
            return handle;
        }

        @Override
        public void cancel(int handle) {
            _tasks.remove(handle);
        }
    }

    private static final class Stateful extends NativeView implements OnComponentDidUpdate {
        private @Nullable Runnable _render;
        private @Nullable Runnable _update;

        @JsConstructor
        Stateful(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        Stateful observe(Runnable render, Runnable update) {
            _render = render;
            _update = update;
            return this;
        }

        @Override
        public ReactNode render() {
            Objects.requireNonNull(_render).run();
            return host("div", null, ReactNode.of("Hello"));
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            Objects.requireNonNull(_update).run();
        }
    }

    static class Output extends NativeView {
        @JsConstructor
        Output(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return Js.<Supplier<ReactNode>>uncheckedCast(requireInputs().get("output"))
                    .get();
        }
    }

    private static class Recovering extends NativeView {
        @JsConstructor
        Recovering(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var state = state();
            final var inputs = requireInputs();
            return null != state && null != state.get("error")
                    ? host(Js.asString(inputs.get("fallback")), null, null).input("prop", "ErrorBoundary")
                    : Js.uncheckedCast(inputs.get("children"));
        }
    }

    private static final class Catching extends Recovering implements OnComponentDidCatch {
        @JsConstructor
        Catching(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            setState(JsPropertyMap.of("error", error));
        }
    }
}
