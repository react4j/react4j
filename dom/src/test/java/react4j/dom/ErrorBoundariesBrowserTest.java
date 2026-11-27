package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.JsError;
import react4j.ReactElement;
import react4j.ReactErrorInfo;
import react4j.ReactNode;
import react4j.internal.CurrentOwner;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidCatch;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnGetSnapshotBeforeUpdate;
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.Browser;

@NullMarked
public final class ErrorBoundariesBrowserTest {
    private ErrorBoundariesBrowserTest() {}

    @JsMethod
    public static void mountWithoutBoundary() {
        for (final var phase : List.of("render", "componentWillMount", "componentDidMount")) {
            try (final var scenario = new Scenario(false)) {
                failure(() -> scenario.render(scenario.broken(phase)));
            }
        }
    }

    @JsMethod
    public static void updateWithoutBoundary() {
        for (final var phase : List.of("componentWillUpdate", "componentWillReceiveProps", "componentDidUpdate")) {
            try (final var scenario = new Scenario(false)) {
                final var broken = scenario.broken(phase);
                scenario.render(broken);
                failure(() -> scenario.render(broken.dup()));
            }
        }
    }

    @JsMethod
    public static void unmountWithoutBoundary() {
        try (final var scenario = new Scenario(false)) {
            scenario.render(scenario.broken("componentWillUnmount"));
            failure(scenario::unmount);
        }
    }

    @JsMethod
    public static void isolatedRoots(boolean derived) {
        try (final var first = new Scenario(derived);
                final var second = new Scenario(derived);
                final var third = new Scenario(derived)) {
            first.render(host("span", ReactNode.of("Before 1")));
            failure(() -> second.render(second.broken("render")));
            final var boundary = third.boundary(third.broken("render"));
            third.render(boundary);
            first.firstChildText("Before 1");
            second.empty();
            third.firstChildText("Caught an error: Hello.");

            first.render(host("span", ReactNode.of("After 1")));
            second.render(host("span", ReactNode.of("After 2")));
            third.render(boundary.dup().input("forceRetry", true).input("children", ReactNode.of("After 3")));
            first.firstChildText("After 1");
            second.firstChildText("After 2");
            third.firstChildText("After 3");
            first.unmount();
            second.unmount();
            third.unmount();
            first.empty();
            second.empty();
            third.empty();
        }
    }

    @JsMethod
    public static void recoverMountError(boolean derived, boolean production, String phase) {
        try (final var scenario = new Scenario(derived)) {
            scenario.render(scenario.boundary(scenario.broken(phase)));
            scenario.firstChildText("Caught an error: Hello.");
            final var entries = new ArrayList<>(List.of(
                    "ErrorBoundary constructor", "ErrorBoundary componentWillMount", "ErrorBoundary render success"));
            switch (phase) {
                case "render" -> {
                    entries.add("BrokenRender constructor");
                    entries.add("BrokenRender componentWillMount");
                    entries.add("BrokenRender render [!]");
                    if (!production) {
                        entries.add("BrokenRender render [!]");
                    }
                }
                case "constructor" -> entries.add("BrokenConstructor constructor [!]");
                case "componentWillMount" -> {
                    entries.add("BrokenComponentWillMount constructor");
                    entries.add("BrokenComponentWillMount componentWillMount [!]");
                }
                default -> throw new AssertionError(phase);
            }
            entries.addAll(
                    derived
                            ? List.of(
                                    "ErrorBoundary static getDerivedStateFromError",
                                    "ErrorBoundary componentWillMount",
                                    "ErrorBoundary render error",
                                    "ErrorBoundary componentDidMount")
                            : List.of(
                                    "ErrorBoundary componentDidMount",
                                    "ErrorBoundary componentDidCatch",
                                    "ErrorBoundary componentWillUpdate",
                                    "ErrorBoundary render error",
                                    "ErrorBoundary componentDidUpdate"));
            scenario.trace(entries);
            scenario.unmount();
            scenario.trace(List.of("ErrorBoundary componentWillUnmount"));
        }
    }

    @JsMethod
    public static void captureContextProviderMount(boolean derived, boolean modulePattern) {
        try (final var scenario = new Scenario(derived)) {
            final ViewConstructorFunction provider = inputs -> new ContextProvider(inputs, modulePattern);
            final var descriptor = ClassComponent.properties(provider);
            descriptor.set("childContextTypes", JsPropertyMap.of("foo", true));
            descriptor.set("functionComponent", modulePattern);
            scenario.render(scenario.boundary(ReactElement.createViewElement(provider)));
            scenario.firstChildText("Caught an error: Hello.");
        }
    }

    @JsMethod
    public static void mountErrorMessage(boolean derived, boolean production) {
        try (final var scenario = new Scenario(derived)) {
            final var boundary = scenario.boundary(scenario.broken("render"));
            scenario.render(boundary.input("fallback", scenario.component("ErrorMessage", "", ReactNode.of("Hello"))));
            final var entries = new ArrayList<>(List.of(
                    "ErrorBoundary constructor",
                    "ErrorBoundary componentWillMount",
                    "ErrorBoundary render success",
                    "BrokenRender constructor",
                    "BrokenRender componentWillMount",
                    "BrokenRender render [!]"));
            if (!production) {
                entries.add("BrokenRender render [!]");
            }
            entries.addAll(
                    derived
                            ? List.of(
                                    "ErrorBoundary static getDerivedStateFromError", "ErrorBoundary componentWillMount")
                            : List.of(
                                    "ErrorBoundary componentDidMount",
                                    "ErrorBoundary componentDidCatch",
                                    "ErrorBoundary componentWillUpdate"));
            entries.addAll(List.of(
                    "ErrorBoundary render error",
                    "ErrorMessage constructor",
                    "ErrorMessage componentWillMount",
                    "ErrorMessage render",
                    "ErrorMessage componentDidMount",
                    derived ? "ErrorBoundary componentDidMount" : "ErrorBoundary componentDidUpdate"));
            scenario.trace(entries);
            scenario.unmount();
            scenario.trace(List.of("ErrorBoundary componentWillUnmount", "ErrorMessage componentWillUnmount"));
        }
    }

    @JsMethod
    public static void successfulMount(boolean derived) {
        try (final var scenario = new Scenario(derived)) {
            scenario.render(scenario.boundary(host("div", ReactNode.of("Mounted successfully."))));
            scenario.firstChildText("Mounted successfully.");
            scenario.trace(List.of(
                    "ErrorBoundary constructor",
                    "ErrorBoundary componentWillMount",
                    "ErrorBoundary render success",
                    "ErrorBoundary componentDidMount"));
            scenario.unmount();
            scenario.trace(List.of("ErrorBoundary componentWillUnmount"));
        }
    }

    @JsMethod
    public static void nestedMountError(boolean derived, boolean production, int number) {
        try (final var scenario = new Scenario(derived)) {
            final var broken = scenario.broken("render");
            final var inner =
                    switch (number) {
                        case 10 ->
                            scenario.specialBoundary("RetryErrorBoundary", broken)
                                    .input("ignoreError", true);
                        case 11 ->
                            scenario.specialBoundary("BrokenComponentWillMountErrorBoundary", null)
                                    .input("fail", "componentWillMount");
                        case 12 ->
                            scenario.specialBoundary("BrokenRenderErrorBoundary", broken)
                                    .input("fail", "render error");
                        case 13 -> ReactNode.of(scenario.normal(), broken, scenario.normal());
                        default -> throw new AssertionError(number);
                    };
            scenario.render(scenario.boundary(inner));
            scenario.firstChildText("Caught an error: Hello.");
            scenario.verifyTrace(derived, production, number, 0);
            scenario.unmount();
            scenario.verifyTrace(derived, production, number, 1);
        }
    }

    @JsMethod
    public static void abortedRefs(boolean derived, boolean production, boolean objectRefs) {
        try (final var scenario = new Scenario(derived)) {
            final Object childRef = objectRefs ? JsPropertyMap.of("current", null) : scenario.ref("Child");
            final Object fallbackRef = objectRefs ? JsPropertyMap.of("current", null) : scenario.ref("Error message");
            final var child = ReactElement.createHostElement("div", null, childRef, JsPropertyMap.of());
            scenario.render(scenario.boundary(ReactNode.of(child, scenario.broken("render")))
                    .input("fallbackRef", fallbackRef));
            scenario.text("Caught an error: Hello.");
            final int number = objectRefs ? 15 : 14;
            scenario.verifyTrace(derived, production, number, 0);
            if (objectRefs) {
                final var current = ClassComponent.properties(fallbackRef).get("current");
                equal("[object HTMLDivElement]", nativeString(current));
            }
            scenario.unmount();
            scenario.verifyTrace(derived, production, number, 1);
            if (objectRefs) {
                check(null == ClassComponent.properties(fallbackRef).get("current"), "Fallback ref is cleared");
            }
        }
    }

    @JsMethod
    public static void updateRenderError(boolean derived, boolean production, int number) {
        try (final var scenario = new Scenario(derived)) {
            final var phase =
                    switch (number) {
                        case 17 -> "constructor";
                        case 18 -> "componentWillMount";
                        case 19 -> "componentWillReceiveProps";
                        case 20 -> "componentWillUpdate";
                        case 21 -> "render";
                        default -> throw new AssertionError(number);
                    };
            final var normal = scenario.normal();
            final var broken = scenario.broken(phase);
            final boolean existing = 19 == number || 20 == number;
            scenario.render(scenario.boundary(existing ? ReactNode.of(normal, broken) : normal));
            scenario.clearTrace();
            scenario.render(scenario.boundary(
                    existing
                            ? ReactNode.of(normal.dup(), broken.dup())
                            : ReactNode.of(normal.dup(), scenario.component("Normal2", "", null), broken)));
            scenario.text("Caught an error: Hello.");
            scenario.verifyTrace(derived, production, number, 0);
            scenario.unmount();
            scenario.verifyTrace(derived, production, number, 1);
        }
    }

    @JsMethod
    public static void updateRefs(boolean derived, boolean production) {
        try (final var scenario = new Scenario(derived)) {
            final var first = ReactElement.createHostElement("div", null, scenario.ref("Child1"), JsPropertyMap.of());
            final var second = ReactElement.createHostElement("div", null, scenario.ref("Child2"), JsPropertyMap.of());
            final var fallbackRef = scenario.ref("Error message");
            scenario.render(scenario.boundary(first).input("fallbackRef", fallbackRef));
            scenario.verifyTrace(derived, production, 22, 0);
            scenario.render(scenario.boundary(ReactNode.of(first.dup(), second, scenario.broken("render")))
                    .input("fallbackRef", fallbackRef));
            scenario.text("Caught an error: Hello.");
            scenario.verifyTrace(derived, production, 22, 1);
            scenario.unmount();
            scenario.verifyTrace(derived, production, 22, 2);
        }
    }

    @JsMethod
    public static void unmountErrorDuringUpdate(boolean derived, boolean production, boolean nested) {
        try (final var scenario = new Scenario(derived)) {
            final var first = scenario.broken("componentWillUnmount");
            final var second = scenario.broken("componentWillUnmount");
            final var normal = scenario.normal();
            scenario.render(scenario.boundary(
                    nested
                            ? ReactNode.of(normal.dup().input("children", first), second)
                            : ReactNode.of(first, second, normal)));
            scenario.clearTrace();
            scenario.render(scenario.boundary(nested ? normal.dup().input("children", first.dup()) : first.dup()));
            scenario.text("Caught an error: Hello.");
            final int number = nested ? 24 : 23;
            scenario.verifyTrace(derived, production, number, 0);
            scenario.unmount();
            scenario.verifyTrace(derived, production, number, 1);
        }
    }

    @JsMethod
    public static void nestedUnmountBoundary(boolean derived, boolean production) {
        try (final var scenario = new Scenario(derived)) {
            final var inner = scenario.boundary(scenario.broken("componentWillUnmount"))
                    .input("name", "InnerErrorBoundary")
                    .input("errorPrefix", "Caught an inner error: ");
            final var outer = scenario.boundary(inner)
                    .input("name", "OuterErrorBoundary")
                    .input("errorPrefix", "Caught an outer error: ");
            scenario.render(outer);
            scenario.clearTrace();
            scenario.render(outer.dup().input("children", inner.dup().input("children", null)));
            scenario.text("Caught an inner error: Hello.");
            scenario.verifyTrace(derived, production, 25, 0);
            scenario.unmount();
            scenario.verifyTrace(derived, production, 25, 1);
        }
    }

    @JsMethod
    public static void repeatAndRetry(boolean derived, boolean production, boolean retry) {
        try (final var scenario = new Scenario(derived)) {
            scenario.render(scenario.boundary(scenario.broken("render")));
            if (retry) {
                scenario.render(scenario.boundary(scenario.normal()));
                scenario.text("Caught an error: Hello.");
                scenario.clearTrace();
                scenario.render(scenario.boundary(scenario.normal()).input("forceRetry", true));
                check(!scenario._host.getTextContent().contains("Caught an error"), "Retry clears the fallback");
                scenario.verifyTrace(derived, production, 26, 0);
                scenario.unmount();
                scenario.verifyTrace(derived, production, 26, 1);
            } else {
                scenario.text("Caught an error: Hello.");
                scenario.render(scenario.boundary(scenario.broken("render")));
                scenario.text("Caught an error: Hello.");
                scenario.render(host("div", ReactNode.of("Other screen")));
                scenario.text("Other screen");
            }
        }
    }

    @JsMethod
    public static void consistentChildren(boolean derived, boolean production, int number) {
        try (final var scenario = new Scenario(derived)) {
            switch (number) {
                case 28 -> {
                    scenario.render(scenario.boundary(ReactNode.of(
                            scenario.normal(), scenario.broken("componentWillUnmount"), scenario.normal())));
                    scenario.render(scenario.boundary(null));
                }
                case 29 -> {
                    scenario.render(scenario.boundary(null));
                    scenario.render(scenario.boundary(
                            ReactNode.of(scenario.normal(), scenario.broken("render"), scenario.normal())));
                }
                case 30 -> {
                    final var elements = new ReactElement[101];
                    for (int i = 0; i < 100; i++) {
                        elements[i] = scenario.normal();
                        elements[i].setKey(String.valueOf(i));
                    }
                    final var maybeBroken = scenario.component("MaybeBrokenRender", "", null);
                    maybeBroken.setKey("100");
                    elements[100] = maybeBroken;
                    scenario.render(scenario.boundary(ReactNode.of(elements)));
                    check(!scenario._host.getTextContent().contains("Caught an error"), "Initial render succeeds");
                    final var reversed = new ReactElement[101];
                    reversed[0] = maybeBroken.dup().input("fail", "render");
                    for (int i = 1; i < 101; i++) {
                        reversed[i] = elements[100 - i].dup();
                    }
                    scenario.render(scenario.boundary(ReactNode.of(reversed)));
                }
                default -> throw new AssertionError(number);
            }
            scenario.text("Caught an error: Hello.");
            scenario.clearTrace();
            scenario.unmount();
            scenario.verifyTrace(derived, production, number, 0);
        }
    }

    @JsMethod
    public static void downstreamAndCommitError(boolean derived, boolean production, int number) {
        try (final var scenario = new Scenario(derived)) {
            switch (number) {
                case 31 -> {
                    final @Nullable NativeView[] current = {null};
                    final boolean[] broken = {false};
                    final ViewConstructorFunction type = inputs -> {
                        final var result = new Downstream(inputs);
                        result._failure = () -> {
                            if (broken[0]) {
                                scenario._trace.add("Stateful render [!]");
                                fail("Hello");
                            }
                        };
                        return result;
                    };
                    final var child = ReactElement.createViewElement(type);
                    withRef(child, (ViewRef) view -> current[0] = view);
                    scenario.render(scenario.boundary(child));
                    scenario.clearTrace();
                    broken[0] = true;
                    Objects.requireNonNull(current[0]).forceUpdate();
                }
                case 32 ->
                    scenario.render(scenario.boundary(ReactNode.of(
                            scenario.broken("componentWillUnmount").input("children", scenario.normal()),
                            scenario.broken("componentDidMount"),
                            scenario.component("LastChild", "", null))));
                case 33 -> {
                    final var child = scenario.broken("componentDidUpdate");
                    scenario.render(scenario.boundary(child));
                    scenario.clearTrace();
                    scenario.render(scenario.boundary(child.dup()));
                }
                case 34 -> {
                    final var child = scenario.specialBoundary("BrokenComponentDidMountErrorBoundary", null)
                            .input("fail", "componentDidMount")
                            .input("errorPrefix", "We should never catch our own error: ");
                    scenario.render(scenario.boundary(child));
                    scenario.firstChildText("Caught an error: Hello.");
                }
                case 35 -> {
                    final var unmount = scenario.boundary(ReactNode.of(
                                    scenario.broken("componentWillUnmount").input("errorText", "E1"),
                                    scenario.broken("componentWillUnmount").input("errorText", "E2")))
                            .input("name", "InnerUnmountBoundary")
                            .input("errorPrefix", "Caught an unmounting error: ");
                    final var third = scenario.broken("componentDidUpdate").input("errorText", "E3");
                    final var fourth = scenario.broken("componentDidUpdate").input("errorText", "E4");
                    final var update = scenario.boundary(ReactNode.of(third, fourth))
                            .input("name", "InnerUpdateBoundary")
                            .input("errorPrefix", "Caught an updating error: ");
                    final var outer =
                            scenario.boundary(ReactNode.of(unmount, update)).input("name", "OuterErrorBoundary");
                    scenario.render(outer);
                    scenario.clearTrace();
                    scenario.render(outer.dup()
                            .input(
                                    "children",
                                    ReactNode.of(
                                            unmount.dup().input("children", null),
                                            update.dup().input("children", ReactNode.of(third.dup(), fourth.dup())))));
                    scenario.firstChildText("Caught an unmounting error: E2.Caught an updating error: E4.");
                }
                default -> throw new AssertionError(number);
            }
            scenario.verifyTrace(derived, production, number, 0);
            scenario.unmount();
            scenario.verifyTrace(derived, production, number, 1);
        }
    }

    @JsFunction
    private interface ViewRef {
        void accept(@Nullable NativeView view);
    }

    private static void withRef(ReactElement element, Object ref) {
        ClassComponent.properties(element).set("ref", ref);
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "String")
    private static native String nativeString(@Nullable Object value);

    private static final class Downstream extends NativeView {
        private @Nullable Hook _failure;

        @JsConstructor
        Downstream(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        Downstream withFailure(Hook failure) {
            _failure = failure;
            return this;
        }

        @Override
        public ReactNode render() {
            Objects.requireNonNull(_failure).run();
            return host("div", null);
        }
    }

    @JsMethod
    public static void rejectsBadRoot() {
        try (final var scenario = new Scenario(true)) {
            final var bad = host("div", null);
            ClassComponent.properties(bad).set("type", null);
            check(caughtMessage(() -> scenario.render(bad)).contains("got: null"), "Runtime reports null type");
            scenario.empty();
            final var undefined = host("div", null);
            ClassComponent.properties(undefined).set("type", Js.undefined());
            check(
                    caughtMessage(() -> scenario.render(undefined)).contains("got: undefined"),
                    "Runtime reports undefined type");
            scenario.empty();
        }
    }

    @JsMethod
    public static void unhandledBoundary(boolean derived, boolean production) {
        try (final var scenario = new Scenario(derived)) {
            final var noop = scenario.specialBoundary("NoopErrorBoundary", scenario.broken("render"))
                    .input("ignoreError", true);
            final var root = host("div", ReactNode.of(ReactNode.of("Sibling"), noop));
            if (derived) {
                failure(() -> scenario.render(root));
                scenario.empty();
            } else {
                final var warnings = captureWarnings(() -> scenario.render(root));
                check(
                        production || warnings.contains("should implement getDerivedStateFromError()"),
                        "Legacy boundary without a state update warns");
                scenario.firstChildText("Sibling");
            }
            scenario.verifyTrace(derived, production, 37, 0);
            if (!derived) {
                scenario.unmount();
                scenario.verifyTrace(false, production, 37, 1);
            }
        }
    }

    @JsMethod
    public static void firstCommitError(boolean snapshot) {
        try (final var scenario = new Scenario(true)) {
            if (snapshot) {
                final ViewConstructorFunction type = inputs -> new SnapshotFailure(inputs);
                final var child = ReactElement.createViewElement(type)
                        .input("trace", scenario._trace)
                        .input("name", "child");
                final var parent = ReactElement.createViewElement(type)
                        .input("trace", scenario._trace)
                        .input("name", "parent")
                        .input("children", child);
                scenario.render(parent.input("value", 1));
                equal(
                        "child sad",
                        caughtMessage(() -> scenario.render(parent.dup()
                                .input("value", 2)
                                .input("children", child.dup().input("value", 2)))));
                scenario.trace(List.of("child sad", "parent sad"));
            } else {
                final ViewConstructorFunction type = inputs -> new MountFailure(inputs);
                final var child = ReactElement.createViewElement(type)
                        .input("trace", scenario._trace)
                        .input("name", "child");
                final var parent = ReactElement.createViewElement(type)
                        .input("trace", scenario._trace)
                        .input("name", "parent")
                        .input("children", child);
                equal("child sad", caughtMessage(() -> scenario.render(parent)));
                scenario.trace(List.of("child sad", "parent sad"));
            }
        }
    }

    @JsMethod
    public static void unbatchedInitialMountError() {
        try (final var scenario = new Scenario(true)) {
            final ViewConstructorFunction type = inputs -> new Downstream(inputs).withFailure(() -> fail("foo error"));
            equal(
                    "foo error",
                    caughtMessage(() ->
                            ReactDOM.batchedUpdates(() -> scenario.render(ReactElement.createViewElement(type)))));
        }
    }

    @JsMethod
    public static void warnsLegacyWithoutState(boolean production) {
        try (final var scenario = new Scenario(false)) {
            final var boundary = scenario.specialBoundary("InvalidErrorBoundary", scenario.broken("render"))
                    .input("ignoreError", true)
                    .input("silentCatch", true);
            final var warnings = captureWarnings(() -> scenario.render(boundary));
            check(
                    production
                            || warnings.contains("InvalidErrorBoundary: Error boundaries should implement"
                                    + " getDerivedStateFromError(). In that method, return a state update to display an"
                                    + " error message or fallback UI."),
                    "Development warns when componentDidCatch does not update state");
            scenario.text("");
        }
    }

    @JsMethod
    public static void invokesBothCatchMethods() {
        try (final var scenario = new Scenario(true)) {
            final var thrown = newError("expected");
            final var errors = new JsError[2];
            final ViewConstructorFunction type = inputs -> new CatchingProbe(inputs);
            ClassComponent.properties(type).set("getDerivedStateFromError", (Derive) error -> {
                errors[0] = error;
                return JsPropertyMap.<@Nullable Object>of("error", error);
            });
            final ViewConstructorFunction childType =
                    inputs -> new Downstream(inputs).withFailure(() -> throwError(thrown));
            final var boundary = ReactElement.createViewElement(type)
                    .input("trace", scenario._trace)
                    .input("name", "ErrorBoundary")
                    .input("boundary", true)
                    .input("children", ReactElement.createViewElement(childType))
                    .input("fallback", ReactNode.of("ErrorBoundary"))
                    .input("observeCatch", (ObserveCatch) error -> errors[1] = error);
            scenario.render(boundary);
            scenario.text("ErrorBoundary");
            check(Js.isTripleEqual(errors[1], thrown), "componentDidCatch receives the thrown Error");
            check(Js.isTripleEqual(errors[0], thrown), "getDerivedStateFromError receives the thrown Error");
        }
    }

    @JsMethod
    private static native String captureWarnings(Hook action);

    @JsMethod
    private static native JsError newError(String message);

    @JsMethod
    private static native void throwError(JsError error);

    @JsFunction
    private interface ObserveCatch {
        void accept(JsError error);
    }

    @JsMethod
    private static native String caughtMessage(Hook action);

    private static class CommitFailure extends NativeView {
        @JsConstructor
        CommitFailure(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var child = requireInputs().get("children");
            return null == child ? host("div", null) : Js.uncheckedCast(child);
        }

        void throwAtCommit() {
            final var inputs = requireInputs();
            final var message = inputs.get("name") + " sad";
            Js.<List<String>>uncheckedCast(inputs.get("trace")).add(message);
            fail(message);
        }
    }

    private static final class MountFailure extends CommitFailure implements OnComponentDidMount {
        @JsConstructor
        MountFailure(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidMount() {
            throwAtCommit();
        }
    }

    private static final class SnapshotFailure extends CommitFailure
            implements OnComponentDidUpdate, OnGetSnapshotBeforeUpdate {
        @JsConstructor
        SnapshotFailure(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {}

        @Override
        public @Nullable Object getSnapshotBeforeUpdate(
                JsPropertyMap<@Nullable Object> previous, JsPropertyMap<@Nullable Object> state) {
            throwAtCommit();
            return null;
        }
    }

    private static ReactElement host(String type, @Nullable ReactNode children) {
        return ReactElement.createHostElement(type, null, null, JsPropertyMap.of("children", children));
    }

    private static void failure(Runnable action) {
        equal("Hello", caughtMessage(action::run));
    }

    @JsMethod
    private static native void fail(String message);

    @JsFunction
    private interface Hook {
        void run();
    }

    @JsFunction
    private interface RefHook {
        void accept(@Nullable Object value);
    }

    @JsFunction
    private interface ChildContext {
        Object get();
    }

    @JsFunction
    private interface Derive {
        @Nullable
        JsPropertyMap<@Nullable Object> derive(JsError error);
    }

    private static final class Scenario implements AutoCloseable {
        private final Browser.Element _host = Browser.append("div");
        private final ArrayList<String> _trace = new ArrayList<>();
        private final HashMap<String, ViewConstructorFunction> _components = new HashMap<>();
        private final ViewConstructorFunction _boundary;
        private final boolean _derived;

        Scenario(boolean derived) {
            _derived = derived;
            _boundary = boundaryType("ErrorBoundary", false);
        }

        private ViewConstructorFunction boundaryType(String name, boolean ignoreError) {
            final ViewConstructorFunction factory = _derived
                    ? (JsPropertyMap<@Nullable Object> inputs) -> new Probe(inputs)
                    : (JsPropertyMap<@Nullable Object> inputs) -> new CatchingProbe(inputs);
            final var type = ClassComponent.properties(factory);
            type.set("childContextTypes", JsPropertyMap.of());
            type.set("displayName", name);
            if (_derived) {
                type.set("getDerivedStateFromError", (Derive) error -> {
                    _trace.add(name + " static getDerivedStateFromError"
                            + ("RetryErrorBoundary".equals(name) ? " [!]" : ""));
                    if ("RetryErrorBoundary".equals(name)) {
                        return JsPropertyMap.<@Nullable Object>of();
                    }
                    return ignoreError ? null : JsPropertyMap.<@Nullable Object>of("error", error);
                });
            }
            return factory;
        }

        ReactElement specialBoundary(String name, @Nullable ReactNode children) {
            return ReactElement.createViewElement(
                            boundaryType(name, "RetryErrorBoundary".equals(name) || "NoopErrorBoundary".equals(name)))
                    .input("name", name)
                    .input("trace", _trace)
                    .input("children", children)
                    .input("boundary", true)
                    .input("mountOnly", true);
        }

        ReactElement boundary(@Nullable ReactNode children) {
            return ReactElement.createViewElement(_boundary)
                    .input("children", children)
                    .input("name", "ErrorBoundary")
                    .input("trace", _trace)
                    .input("boundary", true);
        }

        ReactElement component(String name, String failAt, @Nullable ReactNode children) {
            final var type = _components.computeIfAbsent(
                    "Normal2".equals(name) || "LastChild".equals(name) ? "Normal" : name, ignored -> {
                        final ViewConstructorFunction constructor = Probe::new;
                        ClassComponent.properties(constructor).set("childContextTypes", JsPropertyMap.of());
                        return constructor;
                    });
            return ReactElement.createViewElement(type)
                    .input("name", name)
                    .input("fail", failAt)
                    .input("trace", _trace)
                    .input("children", children);
        }

        ReactElement normal() {
            return component("Normal", "", null);
        }

        RefHook ref(String name) {
            return value -> _trace.add(name + " ref is set to " + nativeString(value));
        }

        ReactElement broken(String failAt) {
            return component(failureName(failAt), failAt, null);
        }

        void render(ReactNode node) {
            Renderer.render(node, Js.uncheckedCast(_host), null);
        }

        void unmount() {
            ReactDOM.unmountComponentAtNode(_host);
        }

        void firstChildText(String expected) {
            equal(expected, Objects.requireNonNull(_host.getFirstElementChild()).getTextContent());
        }

        void empty() {
            check(null == Js.<DomHost.Node>uncheckedCast(_host).firstChild, "Container is empty");
        }

        void text(String expected) {
            equal(expected, _host.getTextContent());
        }

        void clearTrace() {
            _trace.clear();
        }

        void verifyTrace(boolean derived, boolean production, int scenario, int assertion) {
            final var expected = ErrorBoundaryTraces.expected(derived, scenario, assertion);
            final var lines = new ArrayList<String>();
            for (final var line : expected.split("\n")) {
                lines.add(line);
                if (!production && (line.endsWith(" render [!]") || !derived && line.endsWith(" render error [!]"))) {
                    lines.add(line);
                }
            }
            trace(lines);
        }

        void trace(List<String> expected) {
            equal(String.join("\n", expected), String.join("\n", _trace));
            _trace.clear();
        }

        @Override
        public void close() {
            try {
                unmount();
            } finally {
                _host.remove();
            }
        }
    }

    private static String failureName(String phase) {
        return "Broken" + Character.toUpperCase(phase.charAt(0)) + phase.substring(1);
    }

    private static class Probe extends NativeView
            implements OnComponentDidMount, OnComponentDidUpdate, OnComponentWillUnmount {
        @JsConstructor
        Probe(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            event("constructor");
            final var methods = ClassComponent.properties(this);
            methods.set("UNSAFE_componentWillMount", (Hook) () -> event("componentWillMount"));
            if (!Boolean.TRUE.equals(inputs.get("mountOnly"))) {
                methods.set("UNSAFE_componentWillReceiveProps", (Hook) () -> event("componentWillReceiveProps"));
                methods.set("UNSAFE_componentWillUpdate", (Hook) () -> event("componentWillUpdate"));
            }
        }

        final void event(String phase) {
            final var inputs = requireInputs();
            final var failed = phase.equals(inputs.get("fail"));
            final var trace = Js.<List<String>>uncheckedCast(inputs.get("trace"));
            trace.add(inputs.get("name") + " " + phase + (failed ? " [!]" : ""));
            if (failed) {
                fail(Objects.requireNonNullElse(Js.<@Nullable String>uncheckedCast(inputs.get("errorText")), "Hello"));
            }
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            final var children = Js.<@Nullable ReactNode>uncheckedCast(inputs.get("children"));
            if (Boolean.TRUE.equals(inputs.get("boundary"))) {
                if (Boolean.TRUE.equals(inputs.get("ignoreError"))) {
                    event("render");
                    return children;
                }
                final var state = state();
                final var error = null == state ? null : Js.<JsError>uncheckedCast(state.get("error"));
                if (null != error && !Boolean.TRUE.equals(inputs.get("forceRetry"))) {
                    event("render error");
                    final var fallback = inputs.get("fallback");
                    return null == fallback
                            ? ReactElement.createHostElement(
                                    "div",
                                    null,
                                    inputs.get("fallbackRef"),
                                    JsPropertyMap.of(
                                            "children",
                                            ReactNode.of(Objects.requireNonNullElse(
                                                            Js.<@Nullable String>uncheckedCast(
                                                                    inputs.get("errorPrefix")),
                                                            "Caught an error: ")
                                                    + Js.asString(ClassComponent.properties(error)
                                                            .get("message")) + ".")))
                            : Js.uncheckedCast(fallback);
                }
                event("render success");
            } else {
                event("render");
            }
            if ("ErrorMessage".equals(inputs.get("name"))) {
                return host("div", ReactNode.of("Caught an error: Hello."));
            }
            return host("div", children);
        }

        @Override
        public void componentDidMount() {
            event("componentDidMount");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            event("componentDidUpdate");
        }

        @Override
        public void componentWillUnmount() {
            event("componentWillUnmount");
        }
    }

    private static final class CatchingProbe extends Probe implements OnComponentDidCatch {
        @JsConstructor
        CatchingProbe(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            final var inputs = requireInputs();
            final var observer = inputs.get("observeCatch");
            if (null != observer) {
                Js.<ObserveCatch>uncheckedCast(observer).accept(error);
                return;
            }
            if (Boolean.TRUE.equals(inputs.get("silentCatch"))) {
                return;
            }
            if ("NoopErrorBoundary".equals(inputs.get("name"))) {
                event("componentDidCatch");
                return;
            }
            if ("RetryErrorBoundary".equals(inputs.get("name"))) {
                event("componentDidCatch [!]");
                setState(JsPropertyMap.of());
            } else {
                event("componentDidCatch");
                setState(JsPropertyMap.of("error", error));
            }
        }
    }

    private static final class ContextProvider extends NativeView {
        private final boolean _modulePattern;

        @SuppressWarnings("unusable-by-js")
        @JsConstructor
        ContextProvider(JsPropertyMap<@Nullable Object> inputs, boolean modulePattern) {
            super(inputs);
            _modulePattern = modulePattern;
            if (!modulePattern) {
                installLegacyHooks();
            }
        }

        private void installLegacyHooks() {
            final var props = ClassComponent.properties(this);
            props.set("getChildContext", (ChildContext) () -> JsPropertyMap.of("foo", 42));
            props.set("UNSAFE_componentWillMount", (Hook) () -> fail("Hello"));
        }

        @Override
        public ReactNode render() {
            if (_modulePattern) {
                installLegacyHooks();
                final var owner = CurrentOwner.get();
                Js.<Reconciler.Fiber>uncheckedCast(Objects.requireNonNull(owner))
                        .initializeModuleComponent(this);
            }
            return host("div", Js.uncheckedCast(requireInputs().get("children")));
        }
    }
}
