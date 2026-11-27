package react4j.dom;

import static react4j.dom.IsolatedReconcilerTest.captureWarnings;
import static react4j.dom.IsolatedReconcilerTest.equal;
import static react4j.dom.IsolatedReconcilerTest.props;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
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
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing", name = "IsolatedStrictClassTest")
public final class IsolatedStrictClassTest {
    private IsolatedStrictClassTest() {}

    public static void phases(boolean strict, boolean enabled, boolean development) {
        final var driver = new Driver(strict && enabled, !strict && enabled);
        final var trace = new ArrayList<String>();
        final var fixture = new Probe[1];
        final ViewConstructorFunction type = Probe::new;
        props(type).set("componentClass", type);
        props(type).set("getDerivedStateFromProps", (Derive) () -> {
            trace.add("derive");
            return null;
        });
        final var input = ReactElement.createViewElement(type)
                .input("trace", trace)
                .input("probe", fixture)
                .input("update", false);
        final var root = new MemoryRenderer(driver).createTestRoot(false);
        final var twice = development && enabled;
        root.render(wrap(strict, input), null);
        equal(
                twice
                        ? List.of("constructor", "constructor", "derive", "derive", "render", "render", "mount")
                        : List.of("constructor", "derive", "render", "mount"),
                trace);
        trace.clear();
        root.render(wrap(strict, input.dup().input("update", true)), null);
        equal(
                twice
                        ? List.of("derive", "derive", "predicate", "render", "render", "update")
                        : List.of("derive", "predicate", "render", "update"),
                trace);
        trace.clear();
        root.render(wrap(strict, input.dup()), null);
        equal(twice ? List.of("derive", "derive", "predicate") : List.of("derive", "predicate"), trace);
        root.unmount(null);
    }

    public static void stateUpdater(boolean strict, boolean enabled, boolean development) {
        final var driver = new Driver(strict && enabled, !strict && enabled);
        final var probe = new Probe[1];
        final ViewConstructorFunction type = Probe::new;
        props(type).set("componentClass", type);
        final var root = new MemoryRenderer(driver).createTestRoot(false);
        root.render(
                wrap(
                        strict,
                        ReactElement.createViewElement(type)
                                .input("trace", new ArrayList<String>())
                                .input("probe", probe)
                                .input("update", true)),
                null);
        final var view = Objects.requireNonNull(probe[0]);
        final var prior = new ArrayList<Integer>();
        final var callback = new int[1];
        view.setStateWithCallback(
                (NativeView.StateUpdater) (state, inputs) -> {
                    final int count =
                            Js.coerceToInt(props(Objects.requireNonNull(state)).get("count"));
                    prior.add(count);
                    return JsPropertyMap.<@Nullable Object>of("count", count + 1);
                },
                () -> callback[0]++);
        equal(development && enabled ? List.of(1, 1) : List.of(1), prior);
        equal(2, Js.coerceToInt(props(Objects.requireNonNull(view.state())).get("count")));
        equal(1, callback[0]);
        root.unmount(null);
    }

    public static void subtreeWarnings(boolean development) {
        final var root = new MemoryRenderer(new Driver(false, false)).createTestRoot(false);
        final var allHooks = diagnosticView("AllHooks").input("allHooks", true);
        final var mount = diagnosticView("OnlyMount");
        final var late = diagnosticView("LateMount");
        final var legacy = diagnosticView("LegacyProvider");
        final var legacyChild = diagnosticView("LegacyConsumer");
        props(legacy.type()).set("childContextTypes", JsPropertyMap.of());
        props(legacyChild.type()).set("contextTypes", JsPropertyMap.of());
        legacy.input("children", legacyChild);
        final var first = region("first", React.ConcurrentMode, allHooks);
        final var second = region("second", React.StrictMode, mount);
        final var third = region("third", React.StrictMode, legacy);
        final var tree = ReactElement.createHostElement(
                "div", null, null, JsPropertyMap.of("children", new Object[] {first, second, third}));
        final var initial = captureWarnings(() -> root.render(tree, null));
        if (development) {
            contains(initial, "strict-mode tree:\n    in ConcurrentMode\n    in div");
            contains(
                    initial,
                    "componentWillMount: Please update the following components to use componentDidMount instead:"
                            + " AllHooks");
            contains(
                    initial,
                    "componentWillReceiveProps: Please update the following components to use static"
                            + " getDerivedStateFromProps instead: AllHooks");
            contains(
                    initial,
                    "componentWillUpdate: Please update the following components to use componentDidUpdate instead:"
                            + " AllHooks");
            contains(
                    initial,
                    "strict-mode tree:\n"
                            + "    in StrictMode\n"
                            + "    in div\n\n"
                            + "componentWillMount: Please update the following components to use componentDidMount"
                            + " instead: OnlyMount");
            contains(
                    initial,
                    "Legacy context API has been detected within a strict-mode tree: \n"
                            + "    in StrictMode\n"
                            + "    in div\n\n"
                            + "Please update the following components: LegacyConsumer, LegacyProvider");
        } else {
            equal("", initial);
        }
        final var later = captureWarnings(() -> root.render(
                tree.dup().input("children", new Object[] {first, second.dup().input("children", late), third}), null));
        if (development) {
            contains(later, "componentDidMount instead: LateMount");
            equal(false, later.contains("AllHooks"));
            equal(false, later.contains("OnlyMount"));
            equal(false, later.contains("Legacy context API"));
        } else {
            equal("", later);
        }
        root.unmount(null);
        equal("", captureWarnings(() -> root.render(tree, null)));
        root.unmount(null);
    }

    public static void stringRefWarnings(boolean development) {
        final var root = new MemoryRenderer(new Driver(false, false)).createRoot(false);
        final var owner = new DiagnosticView[1];
        final var parent = diagnosticView("RefOwner").input("owner", owner);
        final var tree = ReactElement.createStrictMode(parent);
        final var warning = captureWarnings(() -> root.render(tree, null));
        if (development) {
            contains(warning, "A string ref, \"strictRef\", has been found within a strict mode tree.");
            contains(warning, "We recommend using createRef() instead.\n\n    in RefOwner\n    in StrictMode");
            contains(warning, "https://fb.me/react-strict-mode-string-ref");
        } else {
            equal("", warning);
        }
        final var refs =
                Objects.requireNonNull(props(Objects.requireNonNull(owner[0])).get("refs"));
        equal(true, props(refs).has("strictRef"));
        equal("", captureWarnings(() -> root.render(ReactElement.createStrictMode(parent.dup()), null)));
        root.unmount(null);
        equal(false, props(refs).has("strictRef"));
    }

    public static void reconciliation(boolean previousStrict, boolean nextStrict) {
        final ViewConstructorFunction type = Counter::new;
        props(type).set("componentClass", type);
        props(type).set("getDerivedStateFromProps", (DeriveCounter) (ignored, state) ->
                JsPropertyMap.of("count", 1 + Js.coerceToInt(props(state).get("count"))));
        final var root = new MemoryRenderer(new Driver(false, false)).createTestRoot(false);
        final var first = ReactElement.createViewElement(type);
        root.render(mode(previousStrict, first), null);
        equal("count:1", renderedText(root));
        root.render(mode(nextStrict, first.dup()), null);
        equal(previousStrict == nextStrict ? "count:2" : "count:1", renderedText(root));
        root.unmount(null);
    }

    private static ReactNode mode(boolean strict, ReactElement child) {
        return strict ? ReactElement.createStrictMode(child) : ReactElement.createFragment(null, child);
    }

    private static @Nullable Object renderedText(MemoryRenderer.Root root) {
        return props(Js.<Object[]>uncheckedCast(root.getChildren())[0]).get("text");
    }

    @JsFunction
    private interface DeriveCounter {
        Object update(Object inputs, Object state);
    }

    private static final class Counter extends NativeView {
        @JsConstructor
        Counter(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            applyState(JsPropertyMap.of("count", 0));
        }

        @Override
        public ReactNode render() {
            return ReactNode.of("count:"
                    + Js.coerceToInt(props(Objects.requireNonNull(state())).get("count")));
        }
    }

    private static void contains(String warning, String text) {
        if (!warning.contains(text)) {
            throw new AssertionError("Expected " + text + " in " + warning);
        }
    }

    private static ReactElement diagnosticView(String name) {
        final ViewConstructorFunction type = DiagnosticView::new;
        props(type).set("displayName", name);
        props(type).set("componentClass", type);
        return ReactElement.createViewElement(type).input("name", name);
    }

    private static ReactElement region(String key, Object type, ReactNode child) {
        final var element = ReactElement.createHostElement("div", key, null, JsPropertyMap.of("children", child));
        props(element).set("type", type);
        return element;
    }

    @JsFunction
    private interface Hook {
        void invoke();
    }

    private static final class DiagnosticView extends NativeView {
        @JsConstructor
        DiagnosticView(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            final var name = Js.asString(inputs.get("name"));
            if ("RefOwner".equals(name)) {
                ((DiagnosticView[]) Objects.requireNonNull(inputs.get("owner")))[0] = this;
            } else if (name.startsWith("Legacy")) {
                if ("LegacyProvider".equals(name)) {
                    props(this).set("getChildContext", (Derive) JsPropertyMap::of);
                }
            } else {
                props(this).set("UNSAFE_componentWillMount", (Hook) () -> {});
                if (Boolean.TRUE.equals(inputs.get("allHooks"))) {
                    props(this).set("UNSAFE_componentWillReceiveProps", (Hook) () -> {});
                    props(this).set("UNSAFE_componentWillUpdate", (Hook) () -> {});
                }
            }
        }

        @Override
        public @Nullable ReactNode render() {
            if (requireInputs().has("owner")) {
                return ReactElement.createHostElement("span", null, "strictRef", JsPropertyMap.of());
            }
            final var children = requireInputs().get("children");
            return null == children ? null : Js.uncheckedCast(children);
        }
    }

    private static ReactNode wrap(boolean strict, ReactElement element) {
        return strict ? ReactElement.createStrictMode(element) : element;
    }

    @JsFunction
    private interface Derive {
        @Nullable
        Object derive();
    }

    @JsFunction
    private interface Predicate {
        boolean shouldUpdate(Object inputs);
    }

    private static final class Probe extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        private final List<String> trace;

        @JsConstructor
        Probe(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            trace = Js.uncheckedCast(Objects.requireNonNull(inputs.get("trace")));
            trace.add("constructor");
            ((Probe[]) Objects.requireNonNull(inputs.get("probe")))[0] = this;
            applyState(JsPropertyMap.of("count", 1));
            props(this).set("shouldComponentUpdate", (Predicate) next -> {
                trace.add("predicate");
                return Boolean.TRUE.equals(props(next).get("update"));
            });
        }

        @Override
        public @Nullable ReactNode render() {
            trace.add("render");
            return null;
        }

        @Override
        public void componentDidMount() {
            trace.add("mount");
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previous) {
            trace.add("update");
        }
    }

    private record Driver(boolean strictReplay, boolean outsideStrict) implements MemoryRenderer.SchedulerCallbacks {
        Driver {
            props(this).set("replayOutsideStrictMode", outsideStrict);
        }

        @Override
        public double now() {
            return 0;
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
            return false;
        }

        @Override
        public int schedule(Reconciler.Task task, int delay) {
            throw new AssertionError("Synchronous host");
        }

        @Override
        public void cancel(int handle) {}
    }
}
