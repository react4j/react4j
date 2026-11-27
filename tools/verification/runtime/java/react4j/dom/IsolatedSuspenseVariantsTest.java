package react4j.dom;

import static react4j.dom.IsolatedReconcilerTest.array;
import static react4j.dom.IsolatedReconcilerTest.captureWarnings;
import static react4j.dom.IsolatedReconcilerTest.equal;
import static react4j.dom.IsolatedReconcilerTest.host;
import static react4j.dom.IsolatedReconcilerTest.props;
import static react4j.dom.IsolatedSchedulingTest.untilYield;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.JsError;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.dom.IsolatedSuspenseTest.Scenario;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing")
public final class IsolatedSuspenseVariantsTest {
    private IsolatedSuspenseVariantsTest() {}

    public static void cases(int number) {
        final var s = new Scenario(20 > number, false, true);
        switch (number) {
            case 3, 4 -> rejection(s, 4 == number);
            case 5, 6, 7 -> priority(s, number);
            case 9 -> nestedTimeouts(s);
            case 10, 11 -> synchronous(s, 11 == number);
            case 12 -> timeout(s);
            case 17 -> betweenSuspensions(s);
            case 18 -> expiredBatch(s);
            case 19 -> delay(s);
            case 20 -> {
                s.render(s.boundary(1000, "Loading...", s.async("Result", 100)));
                s.yields("Suspend! [Result]", "Loading...");
                s.output("Loading...");
                s.time(100);
                expire(s, 100);
                s.yields("Promise resolved [Result]", "Result");
                s.output("Result");
            }
            case 21 -> looseUpdate(s);
            case 22, 23 -> looseContinues(s, 23 == number);
            case 24, 27 -> lifecycleSuspension(s, 27 == number);
            case 25 -> constructor(s);
            case 26 -> fallbackUpdate(s);
            default -> throw new AssertionError(number);
        }
    }

    private static void rejection(Scenario s, boolean placeholder) {
        final ViewConstructorFunction boundary = ResultBoundary::new;
        final var contents = ReactElement.createViewElement(boundary)
                .input("child", s.async("Result", placeholder ? 3000 : 1000))
                .input("scenario", s);
        s.render(s.boundary(placeholder ? 1000 : 5000, "Loading...", contents));
        s.yields("Suspend! [Result]", "Loading...");
        s.output();
        if (placeholder) {
            expire(s, 2000);
            s.time(2000);
            s.flush();
            s.yields();
            s.output("Loading...");
        }
        s.reject = true;
        expire(s, 1000);
        s.time(1000);
        s.reject = false;
        captureWarnings(s::flush);
        s.yields(
                "Promise rejected [Result]",
                "Error! [Result]",
                "Error! [Result]",
                "Caught error: Failed to load: Result");
        s.output("Caught error: Failed to load: Result");
    }

    private static void priority(Scenario s, int number) {
        if (5 == number) {
            s.render(highLow(s, "A", "1"));
            s.time(1);
            s.flush();
            s.output("A", "1");
            s.lines.clear();
            s.render(highLow(s, "A", "2"));
            s.yields("A", "Suspend! [2]", "Loading...");
            s.harness.memory.flushSync(() -> s.root.render(highLow(s, "B", "1"), null));
            s.flush();
            s.yields("B", "1");
            s.output("B", "1");
            s.time(1);
            s.flush();
            s.yields("Promise resolved [2]");
            s.output("B", "1");
        } else if (6 == number) {
            s.render(s.boundary(5000, "Loading...", s.async("A", 1)));
            s.yields("Suspend! [A]", "Loading...");
            s.output();
            expire(s, 1200);
            s.render(s.boundary(5000, "Loading...", array(s.async("A", 1), s.textNode("B"))));
            s.yields("Suspend! [A]", "B", "Loading...");
            s.output();
            s.time(1);
            s.flush();
            s.yields("Promise resolved [A]", "A", "B");
            s.output("A", "B");
        } else {
            final var missing = s.boundary(5000, "Loading...", s.async("Async", 2000));
            missing.inputs().delete("fallback");
            s.harness.memory.interactiveUpdates(() -> s.root.render(missing, null));
            s.root.render(s.textNode("(empty)"), null);
            s.flush();
            s.yields("Suspend! [Async]", "(empty)");
            s.output("(empty)");
        }
    }

    private static ReactNode highLow(Scenario s, String high, String low) {
        return s.boundary(5000, "Loading...", array(s.textNode(high), s.async(low, 1)));
    }

    private static void nestedTimeouts(Scenario s) {
        s.render(array(
                s.textNode("Sync"),
                s.boundary(
                        1000,
                        "Loading outer...",
                        array(
                                s.async("Outer content", 2000),
                                s.boundary(2500, "Loading inner...", s.async("Inner content", 5000))))));
        s.yields(
                "Sync", "Suspend! [Outer content]", "Suspend! [Inner content]", "Loading inner...", "Loading outer...");
        s.output();
        expire(s, 1500);
        s.time(1500);
        s.flush();
        s.yields();
        s.output("Sync", "Loading outer...");
        expire(s, 2000);
        s.time(2000);
        s.flush();
        s.yields("Promise resolved [Outer content]", "Outer content", "Suspend! [Inner content]", "Loading inner...");
        s.output("Sync", "Loading outer...");
        expire(s, 500);
        s.time(500);
        s.output("Sync", "Outer content", "Loading inner...");
        expire(s, 1000);
        s.time(1000);
        s.flush();
        s.yields("Promise resolved [Inner content]", "Inner content");
        s.output("Sync", "Outer content", "Inner content");
    }

    private static void synchronous(Scenario s, boolean bubble) {
        final var contents = bubble
                ? s.boundary(
                        5000,
                        "Loading (outer)...",
                        array(
                                s.boundary(5000, s.async("Loading (inner)...", 1), s.async("Async", 1)),
                                s.textNode("Sync")))
                : array(s.boundary(5000, "Loading...", s.async("Async", 1)), s.textNode("Sync"));
        s.harness.memory.flushSync(() -> s.root.render(contents, null));
        if (bubble) {
            s.yields("Suspend! [Async]", "Suspend! [Loading (inner)...]", "Sync", "Loading (outer)...");
            s.output("Loading (outer)...");
        } else {
            s.yields("Suspend! [Async]", "Loading...", "Sync");
            s.output("Loading...", "Sync");
            s.time(1);
            s.flush();
            s.yields("Promise resolved [Async]", "Async");
            s.output("Async", "Sync");
        }
    }

    private static void timeout(Scenario s) {
        s.render(array(s.boundary(1000, "Loading...", s.async("Async", 3000)), s.textNode("Sync")));
        s.yields("Suspend! [Async]", "Loading...", "Sync");
        s.output();
        expire(s, 2000);
        s.time(2000);
        s.flush();
        s.yields();
        s.output("Loading...", "Sync");
        s.time(1000);
        s.flush();
        s.yields("Promise resolved [Async]", "Async");
        s.output("Async", "Sync");
    }

    private static void betweenSuspensions(Scenario s) {
        s.render(s.boundary(10000, "Loading...", s.async("A", 10000)));
        s.yields("Suspend! [A]", "Loading...");
        s.output();
        s.time(4999);
        expire(s, 4999);
        s.flush();
        s.yields();
        s.output();
        s.render(s.boundary(10000, "Loading...", s.async("B", 10000)));
        s.yields("Suspend! [B]", "Loading...");
        s.output();
        s.harness.memory.interactiveUpdates(
                () -> s.root.render(s.boundary(10000, "Loading...", s.textNode("C")), null));
        s.flush();
        s.yields("C");
        s.output("C");
        s.time(10000);
        s.flush();
        s.yields("Promise resolved [A]", "Promise resolved [B]");
        s.output("C");
    }

    private static void expiredBatch(Scenario s) {
        final ViewConstructorFunction type = CommitText::new;
        final var node = ReactElement.createViewElement(type).input("scenario", s);
        for (final var text : new String[] {"", "go", "good", "goodbye"}) {
            s.root.render(node.dup().input("text", text), null);
            if (!"goodbye".equals(text)) {
                expire(s, 1000);
                s.time(1000);
            }
        }
        s.harness.advanceClock(10000);
        s.time(10000);
        s.flush();
        s.yields("Suspend! [goodbye]", "Loading...", "Commit: goodbye");
        s.output("Loading...");
        s.harness.advanceClock(20000);
        s.time(20000);
        s.yields("Promise resolved [goodbye]");
        s.output("Loading...");
        s.flush();
        s.yields("goodbye");
        s.output("goodbye");
    }

    private static void delay(Scenario s) {
        final var pending = new IsolatedWrappedViewsTest.Pending();
        final var never = s.wrapper(() -> IsolatedSuspenseTest.suspend(pending), null);
        s.render(array(
                ReactElement.createSuspense(null, null, 1000, never).input("maxDuration", 1000), s.textNode("A")));
        s.output();
        s.time(800);
        expire(s, 800);
        s.flush();
        s.output();
        s.time(1000);
        expire(s, 1000);
        s.flush();
        s.output("A");
    }

    private static void looseUpdate(Scenario s) {
        final var ref = JsPropertyMap.<@Nullable Object>of("current", null);
        final ViewConstructorFunction type = UpdatingText::new;
        final var child = updating(type, "Step: 1", text -> s.async(text, 100), ref);
        final var loading = array(s.textNode("Loading (1)"), s.textNode("Loading (2)"), s.textNode("Loading (3)"));
        s.render(s.boundary(1000, loading, concurrent(array(child, s.textNode("Sibling")))));
        s.time(100);
        s.yields(
                "Suspend! [Step: 1]",
                "Sibling",
                "Loading (1)",
                "Loading (2)",
                "Loading (3)",
                "Promise resolved [Step: 1]",
                "Step: 1");
        s.output("Step: 1", "Sibling");
        instance(ref).setStateWithCallback(JsPropertyMap.of("text", "Step: 2"), () -> s.lines.add("Update did commit"));
        untilYield(s.harness, s.lines, 1);
        s.yields("Suspend! [Step: 2]");
        s.flush();
        s.yields("Update did commit", "Loading (1)", "Loading (2)", "Loading (3)");
        children(s, "!Sibling", "Loading (1)", "Loading (2)", "Loading (3)");
        s.time(100);
        s.flush();
        s.yields("Promise resolved [Step: 2]", "Step: 2");
        s.output("Step: 2", "Sibling");
    }

    private static void looseContinues(Scenario s, boolean strict) {
        final var async = JsPropertyMap.<@Nullable Object>of("current", null);
        final var sync = JsPropertyMap.<@Nullable Object>of("current", null);
        final ViewConstructorFunction type = UpdatingText::new;
        final var first = updating(
                type,
                "Async: 1",
                value -> array(s.textNode("Before"), s.async(value, 100), s.textNode("After")),
                async);
        final var second = updating(
                type, "Sync: 1", value -> array(s.textNode("Before"), s.textNode(value), s.textNode("After")), sync);
        final var content = array(s.boundary(1000, "Loading...", concurrent(first)), concurrent(second));
        s.root.render(strict ? ReactElement.createStrictMode(content) : content, () -> s.lines.add("Did mount"));
        s.time(100);
        s.yields(
                "Before",
                "Suspend! [Async: 1]",
                "After",
                "Before",
                "Sync: 1",
                "After",
                "Did mount",
                "Loading...",
                "Promise resolved [Async: 1]",
                "Async: 1");
        s.output("Before", "Async: 1", "After", "Before", "Sync: 1", "After");
        instance(async)
                .setStateWithCallback(JsPropertyMap.of("text", "Async: 2"), () -> s.lines.add("Update 1 did commit"));
        instance(sync)
                .setStateWithCallback(JsPropertyMap.of("text", "Sync: 2"), () -> s.lines.add("Update 2 did commit"));
        untilYield(s.harness, s.lines, 1);
        s.yields("Before");
        untilYield(s.harness, s.lines, 1);
        s.yields("Suspend! [Async: 2]");
        s.flush();
        s.yields("After", "Before", "Sync: 2", "After", "Update 1 did commit", "Update 2 did commit", "Loading...");
        children(s, "!Before", "!After", "Loading...", "Before", "Sync: 2", "After");
        s.time(100);
        s.yields("Promise resolved [Async: 2]", "Async: 2");
        s.output("Before", "Async: 2", "After", "Before", "Sync: 2", "After");
    }

    private static ReactElement updating(
            ViewConstructorFunction type,
            String initial,
            Function<String, ReactNode> render,
            JsPropertyMap<@Nullable Object> ref) {
        final var element =
                ReactElement.createViewElement(type).input("initial", initial).input("render", render);
        props(element).set("ref", ref);
        return element;
    }

    private static UpdatingText instance(JsPropertyMap<@Nullable Object> ref) {
        return Js.uncheckedCast(Objects.requireNonNull(ref.get("current")));
    }

    private static ReactNode concurrent(ReactNode child) {
        final var element = host("temporary", null, child);
        props(element).set("type", React.ConcurrentMode);
        return element;
    }

    private static void lifecycleSuspension(Scenario s, boolean direct) {
        final ViewConstructorFunction type = LifecycleText::new;
        final var a = lifecycle(s, type, "A", () -> s.logText("A"));
        final var b = lifecycle(s, type, "B", () -> direct ? s.read("B", 100) : s.async("B", 100));
        final var c = lifecycle(s, type, "C", () -> s.logText("C"));
        final var fallback = lifecycle(s, type, "Loading...", () -> s.logText("Loading..."));
        s.root.render(s.boundary(1000, fallback, array(a, b, c)), () -> s.lines.add("Commit root"));
        if (direct) {
            s.yields(
                    "A",
                    "Suspend! [B]",
                    "C",
                    "Mount [A]",
                    "Mount [C]",
                    "Commit root",
                    "Loading...",
                    "Mount [Loading...]");
        } else {
            s.yields(
                    "A",
                    "Suspend! [B]",
                    "C",
                    "Mount [A]",
                    "Mount [B]",
                    "Mount [C]",
                    "Commit root",
                    "Loading...",
                    "Mount [Loading...]");
        }
        children(s, "!A", "!C", "Loading...");
        if (!direct) {
            s.time(1000);
            expire(s, 1000);
            s.yields("Promise resolved [B]", "B");
            s.output("A", "B", "C");
        }
    }

    private static ReactElement lifecycle(
            Scenario s, ViewConstructorFunction type, String name, Supplier<ReactNode> render) {
        return ReactElement.createViewElement(type)
                .input("scenario", s)
                .input("name", name)
                .input("output", render);
    }

    private static void constructor(Scenario s) {
        final ViewConstructorFunction type = ConstructorText::new;
        s.render(s.boundary(
                5000, "Loading...", ReactElement.createViewElement(type).input("scenario", s)));
        s.yields("constructor", "Suspend! [Hi]", "Loading...");
        s.output("Loading...");
        s.time(1000);
        s.yields("Promise resolved [Hi]", "constructor", "Hi", "componentDidMount");
        s.output("Hi");
    }

    private static void fallbackUpdate(Scenario s) {
        final ViewConstructorFunction type = Fallback::new;
        final var fallback = ReactElement.createViewElement(type).input("scenario", s);
        s.render(s.boundary(5000, fallback, s.async("Hi", 100)));
        s.output("Loading...");
        s.time(100);
        s.output("Hi");
    }

    private static void expire(Scenario s, int milliseconds) {
        s.harness.advanceClock(milliseconds);
        s.harness.memory.flushExpired();
    }

    private static void children(Scenario s, String... expected) {
        final var children = Js.<Object[]>uncheckedCast(s.root.getChildren());
        equal(expected.length, children.length);
        for (int i = 0; expected.length > i; i++) {
            final var record = props(children[i]);
            final var name = expected[i];
            final boolean hidden = name.startsWith("!");
            equal("span", record.get("type"));
            equal(hidden, record.get("hidden"));
            equal(hidden ? name.substring(1) : name, record.get("prop"));
        }
    }

    private static final class ResultBoundary extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        ResultBoundary(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            final var error = state();
            if (null == error) {
                return Js.uncheckedCast(inputs.get("child"));
            }
            final var s = Js.<Scenario>uncheckedCast(inputs.get("scenario"));
            return s.logText("Caught error: " + error.get("message"));
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            setState(JsPropertyMap.of("message", props(error).get("message")));
        }
    }

    private static final class CommitText extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        @JsConstructor
        CommitText(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        private Scenario scenario() {
            return Js.uncheckedCast(requireInputs().get("scenario"));
        }

        private String text() {
            return Js.asString(requireInputs().get("text"));
        }

        @Override
        public ReactNode render() {
            final var s = scenario();
            return s.boundary(5000, "Loading...", s.async(text(), 20000));
        }

        @Override
        public void componentDidMount() {
            scenario().lines.add("Commit: " + text());
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            scenario().lines.add("Commit: " + text());
        }
    }

    private static final class UpdatingText extends NativeView {
        @JsConstructor
        UpdatingText(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("text", inputs.get("initial")));
        }

        @Override
        public ReactNode render() {
            final var text = Js.asString(Objects.requireNonNull(state()).get("text"));
            return Js.<Function<String, ReactNode>>uncheckedCast(requireInputs().get("render"))
                    .apply(text);
        }
    }

    private static final class LifecycleText extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        @JsConstructor
        LifecycleText(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return Js.<Supplier<ReactNode>>uncheckedCast(requireInputs().get("output"))
                    .get();
        }

        @Override
        public void componentDidMount() {
            lifecycle("Mount");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            lifecycle("Update");
        }

        private void lifecycle(String phase) {
            final var props = requireInputs();
            Js.<Scenario>uncheckedCast(props.get("scenario")).lines.add(phase + " [" + props.get("name") + "]");
        }
    }

    private static final class ConstructorText extends NativeView implements OnComponentDidMount {
        final Scenario scenario;
        final String text;

        @JsConstructor
        ConstructorText(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            scenario = Js.uncheckedCast(inputs.get("scenario"));
            scenario.lines.add("constructor");
            text = scenario.readValue("Hi", 100);
        }

        @Override
        public ReactNode render() {
            return scenario.logText(text);
        }

        @Override
        public void componentDidMount() {
            scenario.lines.add("componentDidMount");
        }
    }

    private static final class Fallback extends NativeView implements OnComponentDidMount {
        @JsConstructor
        Fallback(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return Js.<Scenario>uncheckedCast(requireInputs().get("scenario")).logText("Loading...");
        }

        @Override
        public void componentDidMount() {
            setState(JsPropertyMap.of("name", "bar"));
        }
    }
}
