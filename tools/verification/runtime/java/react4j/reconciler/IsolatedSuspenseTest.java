package react4j.reconciler;

import static react4j.reconciler.IsolatedReconcilerTest.array;
import static react4j.reconciler.IsolatedReconcilerTest.captureWarnings;
import static react4j.reconciler.IsolatedReconcilerTest.check;
import static react4j.reconciler.IsolatedReconcilerTest.equal;
import static react4j.reconciler.IsolatedReconcilerTest.host;
import static react4j.reconciler.IsolatedReconcilerTest.props;
import static react4j.reconciler.IsolatedSchedulingTest.untilYield;

import java.util.ArrayList;
import java.util.HashMap;
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
import react4j.React;
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
public final class IsolatedSuspenseTest {
    private IsolatedSuspenseTest() {}

    public static void placeholder(boolean persistent, boolean hidden) {
        final var s = new Scenario(true, persistent);
        final var app = s.wrapper(() -> s.boundary(500, "Loading...", placeholderContent(s, hidden, s.middle)), null);
        s.middle = "B";
        s.render(app);
        s.yields("A", "Suspend! [B]", "C", "Loading...");
        s.visible("");
        s.time(1000);
        s.yields("Promise resolved [B]");
        s.flush();
        s.yields("A", "B", "C");
        s.visible(hidden ? "span{hidden}(A)span(B)span(C)" : "ABC");

        s.middle = "B2";
        s.render(app.dup());
        if (hidden) {
            s.yields("Suspend! [B2]", "C", "Loading...");
        } else {
            s.yields("A", "Suspend! [B2]", "C", "Loading...");
        }
        s.time(750);
        s.flush();
        s.yields();
        s.visible("Loading...");
        s.time(1000);
        s.yields("Promise resolved [B2]");
        s.flush();
        if (hidden) {
            s.yields("B2", "C");
        } else {
            s.yields("A", "B2", "C");
        }
        s.visible(hidden ? "span{hidden}(A)span(B2)span(C)" : "AB2C");
    }

    private static ReactNode placeholderContent(Scenario scenario, boolean hidden, String middle) {
        if (hidden) {
            return array(
                    ReactElement.createViewElement(scenario.hidden),
                    host("span", null, scenario.async(middle, 1000)),
                    host("span", null, scenario.textNode("C")));
        }
        return array(scenario.textNode("A"), scenario.async(middle, 1000), scenario.textNode("C"));
    }

    public static void suspense(int number) {
        suspense(number, false);
    }

    private static void suspense(int number, boolean noop) {
        final var s = new Scenario(3 != number && (6 > number || 9 == number), false, noop);
        switch (number) {
            case 0 -> {
                final var app = s.wrapper(
                        () -> {
                            s.lines.add("Foo");
                            return s.boundary(
                                    5000,
                                    "Loading...",
                                    s.wrapper(
                                            () -> {
                                                s.lines.add("Bar");
                                                return array(s.async("A", 100), s.textNode("B"));
                                            },
                                            null));
                        },
                        null);
                s.render(app);
                s.yields("Foo", "Bar", "Suspend! [A]", "B", "Loading...");
                s.output();
                s.time(50);
                s.flush();
                s.yields();
                s.output();
                s.time(50);
                s.yields("Promise resolved [A]");
                s.flush();
                s.yields("Foo", "Bar", "A", "B");
                s.output("A", "B");
            }
            case 1 -> {
                s.render(array(
                        s.boundary(1000, "Loading A...", s.async("A", 5000)),
                        s.boundary(3000, "Loading B...", s.async("B", 6000))));
                s.yields("Suspend! [A]", "Loading A...", "Suspend! [B]", "Loading B...");
                s.output();
                s.time(4000);
                s.flush();
                s.yields();
                s.output("Loading A...", "Loading B...");
                s.time(1000);
                s.yields("Promise resolved [A]");
                s.flush();
                s.yields("A");
                s.output("A", "Loading B...");
                s.time(1000);
                s.yields("Promise resolved [B]");
                s.flush();
                s.yields("B");
                s.output("A", "B");
            }
            case 2 -> {
                final var pending = new IsolatedWrappedViewsTest.Pending();
                final boolean[] ready = {false};
                final var async = s.wrapper(
                        () -> {
                            if (!ready[0]) {
                                s.lines.add("Suspend!");
                                return suspend(pending);
                            }
                            return s.logText("Async");
                        },
                        null);
                s.root.render(s.boundary(1000, "Loading...", array(async, s.textNode("Sibling"))), null);
                untilYield(s.harness, s.lines, 1);
                s.yields("Suspend!");
                ready[0] = true;
                pending.resolve("Async");
                s.yields();
                s.flush();
                s.yields("Async", "Sibling");
                s.visible("AsyncSibling");
            }
            case 3 -> {
                final var pending = new IsolatedWrappedViewsTest.Pending();
                final var lazy = host("i", null, null);
                props(lazy).set("type", JsPropertyMap.of("$$typeof", React.Lazy, "_status", -1, "_ctor", (Loader)
                        () -> pending));
                lazy.input("name", "Hi").input("scenario", s).input("mode", "text");
                s.render(s.boundary(5000, "Loading...", lazy));
                s.yields("Loading...");
                s.visible("Loading...");
                final ViewConstructorFunction child = Subject::new;
                pending.module(child);
                s.yields("Hi", "Mount [Hi]");
                s.visible("Hi");
            }
            case 4 -> {
                final var inner = ReactElement.createSuspense(null, null, 100, s.async("Hi", 5000))
                        .input("maxDuration", 100);
                inner.inputs().delete("fallback");
                s.render(s.boundary(5000, "Loading...", inner));
                s.yields("Suspend! [Hi]", "Loading...");
                s.time(1000);
                s.yields();
                s.flush();
                s.yields();
                s.visible("Loading...");
                s.time(5000);
                s.yields("Promise resolved [Hi]");
                s.flush();
                s.yields("Hi");
                s.visible("Hi");
            }
            case 5 -> {
                final var tree = ReactElement.createSuspense(null, null, 5000, s.async("Hi", 1000));
                tree.inputs().delete("fallback");
                s.root.render(tree, null);
                captureWarnings(() -> {
                    try {
                        s.flush();
                    } catch (Throwable error) {
                        check(
                                Objects.requireNonNull(error.getMessage())
                                        .contains("An update was suspended, but no placeholder UI was provided."),
                                "Uncaught suspension reports missing fallback");
                        return;
                    }
                    throw new AssertionError("Expected uncaught suspension");
                });
                s.yields("Suspend! [Hi]", "Suspend! [Hi]");
            }
            case 6, 7, 8, 9 -> legacy(s, number);
            default -> throw new AssertionError(number);
        }
    }

    public static void noop(int number) {
        if (2 > number) {
            suspense(number, true);
            return;
        }
        final var s = new Scenario(true, false, true);
        switch (number) {
            case 2 -> {
                s.render(s.boundary(
                        5000,
                        "Loading...",
                        array(s.textNode("A"), s.async("B", 100), s.textNode("C"), s.textNode("D"))));
                s.yields("A", "Suspend! [B]", "C", "D", "Loading...");
                s.output();
                s.time(100);
                s.flush();
                s.yields("Promise resolved [B]", "A", "B", "C", "D");
                s.output("A", "B", "C", "D");
            }
            case 8 -> {
                s.render(array(s.boundary(5000, "Loading...", s.async("Async", 20000)), s.textNode("Sync")));
                s.yields("Suspend! [Async]", "Loading...", "Sync");
                s.output();
                s.harness.advanceClock(10000);
                s.time(10000);
                s.harness.memory.flushExpired();
                s.yields();
                s.output("Loading...", "Sync");
                s.time(10000);
                s.flush();
                s.yields("Promise resolved [Async]", "Async");
                s.output("Async", "Sync");
            }
            case 13 -> {
                s.root.render(s.boundary(1000, "Loading...", s.async("Async", 3000)), null);
                untilYield(s.harness, s.lines, 1);
                s.yields("Suspend! [Async]");
                s.time(1500);
                s.harness.advanceClock(1500);
                s.yields();
                s.time(2000);
                s.yields("Promise resolved [Async]");
                s.flush();
                s.yields("Async");
                s.output("Async");
            }
            case 14 -> {
                captureWarnings(() -> {
                    try {
                        s.harness.memory.flushSync(() -> s.root.render(s.async("Async", 100), null));
                    } catch (Throwable error) {
                        check(
                                Objects.requireNonNull(error.getMessage())
                                        .contains("An update was suspended, but no placeholder UI was provided."),
                                "Synchronous suspension reports the missing boundary");
                        return;
                    }
                    throw new AssertionError("Expected uncaught suspension");
                });
                s.output();
            }
            case 15 -> {
                s.root.render(s.boundary(0, "Loading...", array(s.async("A", 100), s.async("B", 100))), null);
                s.harness.advanceClock(10000);
                s.harness.memory.flushExpired();
                s.yields("Suspend! [A]", "Suspend! [B]", "Loading...");
                s.output("Loading...");
                s.time(100);
                s.flush();
                s.yields("Promise resolved [A]", "Promise resolved [B]", "A", "B");
                s.output("A", "B");
            }
            case 16 -> {
                s.render(s.boundary(1000, "Loading...", s.async("Async", 100)));
                s.yields("Suspend! [Async]", "Loading...");
                s.output();
                s.time(99);
                s.flush();
                s.yields();
                s.output();
                s.time(1);
                s.flush();
                s.yields("Promise resolved [Async]", "Async");
                s.output("Async");
            }
            default -> throw new AssertionError(number);
        }
    }

    private static void legacy(Scenario s, int number) {
        switch (number) {
            case 6 -> {
                final var a = s.subject("A", "text", 0);
                final var b = s.subject("B", "suspending", 100);
                final var c = s.subject("C", "text", 0);
                final var fallback = s.subject("Loading...", "text", 0);
                s.render(s.boundary(1000, fallback, array(a, b, c)));
                s.yields("A", "Suspend! [B:1]", "C", "Mount [A]", "Mount [C]", "Loading...", "Mount [Loading...]");
                s.visible("Loading...");
                s.time(100);
                s.yields("Promise resolved [B:1]", "B:1", "Unmount [Loading...]", "Mount [B:1]");
                s.visible("AB:1C");
                Objects.requireNonNull(s.stateful).setState(JsPropertyMap.of("step", 2));
                s.yields("Suspend! [B:2]", "Loading...", "Mount [Loading...]");
                s.visible("Loading...");
                s.time(100);
                s.yields("Promise resolved [B:2]", "B:2", "Unmount [Loading...]", "Update [B:2]");
                s.visible("AB:2C");
            }
            case 7 -> {
                final var app = s.wrapper(
                        () -> s.boundary(
                                5000, "Loading...", array(s.subject("Stateful", "plain", 0), s.async(s.middle, 1000))),
                        null);
                s.middle = "A";
                s.render(app);
                s.yields("Stateful: 1", "Suspend! [A]", "Loading...");
                s.time(1000);
                s.yields("Promise resolved [A]", "A");
                s.visible("Stateful: 1A");
                s.middle = "B";
                s.render(app.dup());
                s.yields("Stateful: 1", "Suspend! [B]", "Loading...");
                s.visible("Loading...");
                Objects.requireNonNull(s.stateful).setState(JsPropertyMap.of("step", 2));
                s.yields("Stateful: 2", "Suspend! [B]", "Loading...");
                s.visible("Loading...");
                s.time(1000);
                s.yields("Promise resolved [B]", "B", "B");
                s.visible("Stateful: 2B");
            }
            case 8 -> {
                s.render(s.boundary(5000, "Loading...", s.subject("A", "deleted", 100)));
                s.yields("Suspend! [A]", "Loading...");
                s.render(s.textNode("B"));
                s.yields("B");
                s.visible("B");
            }
            case 9 -> {
                s.render(s.boundary(10, "Loading...", s.subject("Step", "step", 1000)));
                s.yields("Suspend! [Step: 1]", "Loading...");
                s.time(1000);
                s.yields("Promise resolved [Step: 1]");
                s.flush();
                s.yields("Step: 1");
                s.visible("Step: 1");
                Objects.requireNonNull(s.stateful).setState(JsPropertyMap.of("step", 2));
                s.flush();
                s.yields("Suspend! [Step: 2]", "Loading...");
                s.time(500);
                s.visible("Loading...");
                Objects.requireNonNull(s.stateful).setState(JsPropertyMap.of("step", 3));
                s.flush();
                s.yields("Suspend! [Step: 3]");
                s.visible("Loading...");
                s.time(1000);
                s.yields("Promise resolved [Step: 2]", "Promise resolved [Step: 3]");
                s.flush();
                s.yields("Step: 3");
                s.visible("Step: 3");
            }
            default -> throw new AssertionError(number);
        }
    }

    @JsMethod
    static native <T> T suspend(Object pending);

    @JsMethod
    private static native Object failure(String message);

    @JsFunction
    private interface Loader {
        IsolatedWrappedViewsTest.Pending load();
    }

    private static final class Resource {
        final IsolatedWrappedViewsTest.Pending pending = new IsolatedWrappedViewsTest.Pending();
        boolean ready;

        @Nullable
        Object rejection;
    }

    static final class Scenario {
        final IsolatedReconcilerTest.Harness harness = new IsolatedReconcilerTest.Harness();
        final MemoryRenderer.Root root;
        final boolean noop;
        final ArrayList<String> lines = new ArrayList<>();
        final HashMap<String, Resource> resources = new HashMap<>();
        final ViewConstructorFunction hidden;
        final ViewConstructorFunction wrapper = IsolatedReconcilerTest.Output::new;
        final ViewConstructorFunction subject = Subject::new;

        @Nullable
        Subject stateful;

        String middle = "";
        boolean reject;

        Scenario(boolean concurrent, boolean persistent) {
            this(concurrent, persistent, false);
        }

        Scenario(boolean concurrent, boolean persistent, boolean noop) {
            this.noop = noop;
            root = persistent
                    ? harness.memory.createPersistentRoot(concurrent)
                    : noop ? harness.memory.createRoot(concurrent) : harness.memory.createTestRoot(concurrent);
            hidden = (JsPropertyMap<@Nullable Object> inputs) -> new Hidden(inputs).observe(this);
        }

        ReactNode logText(String text) {
            lines.add(text);
            return noop ? host("span", null, null).input("prop", text) : ReactNode.of(text);
        }

        ReactNode read(String text, int delay) {
            return logText(readValue(text, delay));
        }

        String readValue(String text, int delay) {
            final var resource = resources.computeIfAbsent(text, key -> {
                final var pending = new Resource();
                harness.schedule(
                        () -> {
                            pending.ready = true;
                            if (reject) {
                                final var reason = failure("Failed to load: " + key);
                                pending.rejection = reason;
                                lines.add("Promise rejected [" + key + "]");
                                pending.pending.reject(reason);
                            } else {
                                lines.add("Promise resolved [" + key + "]");
                                pending.pending.resolve(key);
                            }
                        },
                        delay);
                return pending;
            });
            if (resource.ready) {
                final var rejected = resource.rejection;
                if (null != rejected) {
                    lines.add("Error! [" + text + "]");
                    return suspend(rejected);
                }
                return text;
            }
            lines.add("Suspend! [" + text + "]");
            return suspend(resource.pending);
        }

        ReactElement wrapper(Supplier<ReactNode> render, @Nullable String key) {
            final var element = ReactElement.createViewElement(wrapper).input("output", render);
            element.setKey(key);
            return element;
        }

        ReactElement textNode(String text) {
            return wrapper(() -> logText(text), null);
        }

        ReactElement async(String text, int delay) {
            return wrapper(() -> read(text, delay), null);
        }

        ReactElement subject(String text, String mode, int delay) {
            return ReactElement.createViewElement(subject)
                    .input("name", text)
                    .input("mode", mode)
                    .input("delay", delay)
                    .input("scenario", this);
        }

        ReactElement boundary(int duration, String fallback, ReactNode child) {
            return boundary(duration, textNode(fallback), child);
        }

        ReactElement boundary(int duration, ReactNode fallback, ReactNode child) {
            return ReactElement.createSuspense(null, fallback, duration, child).input("maxDuration", duration);
        }

        void time(int milliseconds) {
            harness.advanceTimers(milliseconds);
        }

        void render(ReactNode value) {
            harness.render(root, value);
        }

        void flush() {
            harness.flush();
        }

        void yields(String... expected) {
            equal(List.of(expected), lines);
            lines.clear();
        }

        void output(String... labels) {
            if (noop) {
                final var expected = new ArrayList<String>();
                for (final var label : labels) {
                    expected.add("span[" + label + "]()");
                }
                IsolatedReconcilerTest.rootShape(root, String.join(",", expected));
            } else {
                visible(String.join("", labels));
            }
        }

        void visible(String output) {
            equal(output, visibleChildren(root.getChildren()));
        }

        private String visibleChildren(Object children) {
            final var result = new StringBuilder();
            for (final var value : Js.<Object[]>uncheckedCast(children)) {
                final var item = props(value);
                if (Boolean.TRUE.equals(item.get("suspended"))) {
                    continue;
                }
                final var type = item.get("type");
                final var text = item.get("text");
                if (null == type) {
                    result.append(text);
                } else {
                    result.append(type);
                    if (Boolean.TRUE.equals(item.get("hidden"))) {
                        result.append("{hidden}");
                    }
                    result.append("(")
                            .append(null == text ? visibleChildren(Objects.requireNonNull(item.get("children"))) : text)
                            .append(")");
                }
            }
            return result.toString();
        }
    }

    private static final class Hidden extends NativeView implements OnShouldComponentUpdate {
        private @Nullable Scenario _scenario;

        @JsConstructor
        Hidden(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        Hidden observe(Scenario scenario) {
            _scenario = scenario;
            return this;
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> next) {
            return false;
        }

        @Override
        public ReactNode render() {
            return host("span", null, Objects.requireNonNull(_scenario).logText("A"))
                    .input("hidden", true);
        }
    }

    private static final class Subject extends NativeView
            implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount {
        @JsConstructor
        Subject(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("step", 1));
        }

        private Scenario scenario() {
            return Js.uncheckedCast(requireInputs().get("scenario"));
        }

        private String mode() {
            return Js.asString(requireInputs().get("mode"));
        }

        private String label() {
            final var name = Js.asString(requireInputs().get("name"));
            final var step = Objects.requireNonNull(state()).get("step");
            return switch (mode()) {
                case "suspending" -> name + ":" + step;
                case "plain", "step" -> name + ": " + step;
                default -> name;
            };
        }

        @Override
        public ReactNode render() {
            final var scenario = scenario();
            if (List.of("suspending", "plain", "step").contains(mode())) {
                scenario.stateful = this;
            }
            return "text".equals(mode()) || "plain".equals(mode())
                    ? scenario.logText(label())
                    : scenario.read(label(), Js.coerceToInt(requireInputs().get("delay")));
        }

        private void lifecycle(String action) {
            if ("text".equals(mode()) || "suspending".equals(mode())) {
                scenario().lines.add(action + " [" + label() + "]");
            }
        }

        @Override
        public void componentDidMount() {
            lifecycle("Mount");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            lifecycle("Update");
        }

        @Override
        public void componentWillUnmount() {
            if ("deleted".equals(mode())) {
                scenario().lines.add("will unmount");
            }
            lifecycle("Unmount");
        }
    }
}
