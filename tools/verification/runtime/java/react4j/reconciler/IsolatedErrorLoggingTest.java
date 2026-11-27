package react4j.reconciler;

import static react4j.reconciler.IsolatedReconcilerTest.array;
import static react4j.reconciler.IsolatedReconcilerTest.check;
import static react4j.reconciler.IsolatedReconcilerTest.equal;
import static react4j.reconciler.IsolatedReconcilerTest.host;
import static react4j.reconciler.IsolatedReconcilerTest.rootShape;

import java.util.ArrayList;
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
import react4j.JsError;
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
public final class IsolatedErrorLoggingTest {
    private static final ViewConstructorFunction FAILING = Failing::new;
    private static final ViewConstructorFunction BOUNDARY = Boundary::new;
    private static final ViewConstructorFunction FAILED_UPDATE = FailedUpdate::new;

    static {
        propertyMap(FAILING).set("displayName", "ErrorThrowingComponent");
        propertyMap(BOUNDARY).set("displayName", "ErrorBoundary");
        propertyMap(FAILED_UPDATE).set("displayName", "Foo");
    }

    private IsolatedErrorLoggingTest() {}

    public static void errors(int scenario, boolean development) {
        final var harness = new IsolatedReconcilerTest.Harness();
        final var root = harness.memory.createRoot(true);
        final var messages = new ArrayList<@Nullable Object>();
        if (3 == scenario) {
            final var trace = new ArrayList<String>();
            final var child = ReactElement.createViewElement(FAILED_UPDATE).input("trace", trace);
            final var caught = new JsError[1];
            final var boundary = ReactElement.createViewElement(BOUNDARY)
                    .input("child", child)
                    .input("caught", caught);
            observeLogs(() -> harness.render(root, boundary), messages::add);
            equal(List.of("render: 0", "render: 1", "unmount: 0"), trace);
            rootShape(root, "");
            equal(1, messages.size());
            final var received = caught[0];
            check(null != received, "Boundary should receive the failed update");
            equal("oops", propertyMap(Objects.requireNonNull(received)).get("message"));
            if (development) {
                final var diagnostic = Js.asString(messages.get(0));
                check(
                        diagnostic.contains("The above error occurred in the <Foo> component:")
                                && diagnostic.contains("in Foo")
                                && diagnostic.contains("in ErrorBoundary")
                                && diagnostic.contains("using the error boundary you provided, ErrorBoundary."),
                        "Caught error should name its component and boundary: " + diagnostic);
            } else {
                check(received == messages.get(0), "Production logger should receive the caught error");
            }
            return;
        }

        final var phase = List.of("constructor", "componentDidMount", "render").get(scenario);
        final var error = phase + " error";
        final var child = ReactElement.createViewElement(FAILING).input("phase", phase);
        final var tree = host("div", null, host("span", null, child));
        final @Nullable Object[] failure = new Object[1];
        observeLogs(() -> failure[0] = thrownBy(() -> harness.render(root, tree)), message -> {
            messages.add(message);
            if (2 == scenario) {
                failWith("logCapturedError error");
            }
        });
        final var thrown = failure[0];
        check(null != thrown, "Failure must propagate");
        equal(error, propertyMap(Objects.requireNonNull(thrown)).get("message"));
        equal(1, messages.size());
        if (development) {
            final var diagnostic = Js.asString(messages.get(0));
            final int component = diagnostic.indexOf("\n    in ErrorThrowingComponent");
            final int span = diagnostic.indexOf("\n    in span");
            final int div = diagnostic.indexOf("\n    in div");
            check(
                    diagnostic.startsWith("The above error occurred in the <ErrorThrowingComponent> component:")
                            && 0 < component
                            && component < span
                            && span < div
                            && diagnostic.contains("Consider adding an error boundary to your tree"),
                    "Unhandled error should include its host ancestry: " + diagnostic);
        } else {
            check(thrown == messages.get(0), "Production logger should receive the thrown error");
        }
        rootShape(root, "");
        if (2 == scenario) {
            final var deferred = thrownBy(() -> harness.advanceTimers(1));
            check(null != deferred, "Logger failure must propagate in a new task");
            equal(
                    "logCapturedError error",
                    propertyMap(Objects.requireNonNull(deferred)).get("message"));
        }
        harness.render(root, ReactNode.of("reused"));
        rootShape(root, "text(reused)");
    }

    public static void consoleRecovery() {
        final var harness = new IsolatedReconcilerTest.Harness();
        final var root = harness.memory.createRoot(false);
        final ViewConstructorFunction type = BadUpdate::new;
        final var bad = ReactElement.createViewElement(type);
        final int[] calls = {0};
        observeLogs(
                () -> {
                    root.render(bad, null);
                    final var failure = thrownBy(() -> root.render(bad.dup(), null));
                    check(null != failure, "The update error reaches the caller even if logging throws");
                    equal("no", propertyMap(Objects.requireNonNull(failure)).get("message"));
                    root.render(host("span", null, array(ReactNode.of("Hello"))), null);
                    rootShape(root, "span(text(Hello))");
                    final var delayed = thrownBy(() -> harness.advanceTimers(1));
                    check(null != delayed, "The console error is surfaced separately after root recovery");
                    equal(
                            "Buggy console.error",
                            propertyMap(Objects.requireNonNull(delayed)).get("message"));
                },
                ignored -> {
                    calls[0]++;
                    failWith("Buggy console.error");
                });
        equal(1, calls[0]);
    }

    public static void replay(boolean hostFailure) {
        final var harness = new IsolatedReconcilerTest.Harness();
        final var root = harness.memory.createRoot(true);
        if (hostFailure) {
            final @Nullable Object[] failure = new Object[1];
            observeLogs(
                    () -> failure[0] = thrownBy(() -> harness.render(root, host("errorInBeginPhase", null, null))),
                    ignored -> {});
            check(null != failure[0], "The host exception must propagate through the root");
            check(
                    Js.asString(propertyMap(Objects.requireNonNull(failure[0])).get("message"))
                            .endsWith("Error in host config."),
                    "Host exception should retain its original message");
            rootShape(root, "");
            harness.render(root, host("div", null, null));
        } else {
            final int[] attempts = {0};
            final ViewConstructorFunction output = IsolatedReconcilerTest.Output::new;
            final var app = ReactElement.createViewElement(output).input("output", (Supplier<ReactNode>) () -> {
                if (0 == attempts[0]++) {
                    failWith("Hi");
                }
                return host("div", null, null);
            });
            harness.render(root, app);
            equal(2, attempts[0]);
        }
        rootShape(root, "div()");
    }

    private static JsPropertyMap<@Nullable Object> propertyMap(Object value) {
        return Js.uncheckedCast(value);
    }

    @JsFunction
    interface ErrorLog {
        void log(@Nullable Object message);
    }

    @JsMethod
    static native void observeLogs(Reconciler.Task action, ErrorLog log);

    @JsMethod
    private static native @Nullable Object thrownBy(Reconciler.Task action);

    @JsMethod
    private static native void failWith(String message);

    private static final class BadUpdate extends NativeView implements OnComponentDidUpdate {
        @JsConstructor
        BadUpdate(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            return null;
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousInputs) {
            failWith("no");
        }
    }

    private static final class Failing extends NativeView implements OnComponentDidMount {
        @JsConstructor
        Failing(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            fail("constructor", inputs);
        }

        private static void fail(String phase, JsPropertyMap<@Nullable Object> inputs) {
            if (phase.equals(inputs.get("phase"))) {
                failWith(phase + " error");
            }
        }

        @Override
        public ReactNode render() {
            fail("render", requireInputs());
            return host("div", null, null);
        }

        @Override
        public void componentDidMount() {
            fail("componentDidMount", requireInputs());
        }
    }

    private static final class Boundary extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        Boundary(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public @Nullable ReactNode render() {
            return null == state() ? Js.uncheckedCast(requireInputs().get("child")) : null;
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            Js.<JsError[]>uncheckedCast(requireInputs().get("caught"))[0] = error;
            setState(JsPropertyMap.of("error", error));
        }
    }

    private static final class FailedUpdate extends NativeView implements OnComponentDidMount, OnComponentWillUnmount {
        private final List<String> trace;

        @JsConstructor
        FailedUpdate(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            trace = Js.uncheckedCast(inputs.get("trace"));
        }

        private int step() {
            final var state = state();
            return null == state ? 0 : Js.asInt(state.get("step"));
        }

        @Override
        public @Nullable ReactNode render() {
            final int step = step();
            trace.add("render: " + step);
            if (0 < step) {
                failWith("oops");
            }
            return null;
        }

        @Override
        public void componentDidMount() {
            setState(JsPropertyMap.of("step", 1));
        }

        @Override
        public void componentWillUnmount() {
            trace.add("unmount: " + step());
        }
    }
}
