package react4j.reconciler;

import static react4j.reconciler.IsolatedReconcilerTest.array;
import static react4j.reconciler.IsolatedReconcilerTest.captureWarnings;
import static react4j.reconciler.IsolatedReconcilerTest.check;
import static react4j.reconciler.IsolatedReconcilerTest.equal;
import static react4j.reconciler.IsolatedReconcilerTest.host;
import static react4j.reconciler.IsolatedReconcilerTest.props;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentWillUnmount;
import react4j.internal.OnShouldComponentUpdate;
import react4j.internal.ViewConstructorFunction;

@NullMarked
@JsType(namespace = "react4j.testing", name = "IsolatedProfilerDevToolsTest")
public final class IsolatedProfilerDevToolsTest {
    private static final String HOOK = "__REACT_DEVTOOLS_GLOBAL_HOOK__";
    private static final ViewConstructorFunction APP = App::new;
    private static final ViewConstructorFunction TIMED = Timed::new;
    private static final ViewConstructorFunction REF_OWNER = RefOwner::new;

    private IsolatedProfilerDevToolsTest() {}

    public static void autoProfile() {
        withHook(clock -> {
            final var root = clock.memory.createTestRoot(false);
            final var app = ReactElement.createViewElement(APP).input("clock", clock);
            root.render(app.dup().input("multiplier", 1), null);
            equal(1, clock.commits.size());
            equal(List.of(new Sample("Profiler", "mount", 10, 10, 2, 12, 0)), clock.samples);
            equal(12, metric(child(root), "treeBaseDuration"));
            clock.samples.clear();
            root.render(app.dup().input("multiplier", 2), null);
            equal(List.of(new Sample("Profiler", "update", 6, 13, 14, 20, 0)), clock.samples);
            equal(15, metric(child(root), "treeBaseDuration"));
        });
    }

    public static void recoverTimingAfterError() {
        withHook(clock -> {
            final var root = clock.memory.createTestRoot(false);
            clock.time += 20;
            root.render(host("div", null, timed(clock, 2, true)), null);
            clock.time += 20;
            captureWarnings(() -> {
                try {
                    root.render(
                            ReactElement.createHostElement(
                                    "div",
                                    null,
                                    "invalid-string-ref",
                                    JsPropertyMap.of("children", timed(clock, 3, true))),
                            null);
                    throw new AssertionError("Expected the invalid string ref to throw");
                } catch (RuntimeException expected) {
                    check(Objects.requireNonNull(expected.getMessage()).contains("ref"), "Preserve the render error");
                }
            });
            clock.time += 20;
            root.render(host("div", null, timed(clock, 7, true)), null);
            equal(7, metric(child(root), "treeBaseDuration"));
        });
    }

    public static void committedInteractions() {
        withHook(clock -> {
            final var root = clock.memory.createTestRoot(false);
            root.render(host("div", null, null), null);
            final var state = props(Objects.requireNonNull(
                    props(Objects.requireNonNull(child(root).get("return"))).get("stateNode")));
            equal(0, metric(props(Objects.requireNonNull(state.get("memoizedInteractions"))), "size"));

            clock.time += 10;
            final var event = JsPropertyMap.of("name", "some event", "timestamp", clock.time);
            clock.interactions.add(event);
            root.render(host("div", null, null), null);
            final var committed = Objects.requireNonNull(state.get("memoizedInteractions"));
            equal(1, metric(props(committed), "size"));
            check(Js.<NativeSet>uncheckedCast(committed).has(event), "The exact interaction was committed");
            check(state == clock.commits.get(1), "The committed root record remains stable across updates");
        });
    }

    public static void foreignPortal(boolean production) {
        final var root = new Clock(new ArrayList<>()).memory.createTestRoot(false);
        final var container = JsPropertyMap.of("children", JsPropertyMap.of("length", 0));
        final var portal = Portal.create(host("div", null, null), container);
        final var warnings = captureWarnings(() -> {
            try {
                root.render(portal, null);
                throw new AssertionError("Expected the test renderer to reject the foreign portal container");
            } catch (RuntimeException expected) {
                check(
                        Objects.requireNonNull(expected.getMessage())
                                .contains("An invalid container has been provided."),
                        "The commit failed on the foreign container");
            }
        });
        equal(!production, warnings.contains("Warning: An invalid container has been provided."));
        equal(!production, warnings.contains("ReactDOM.createPortal inside of a ReactTestRenderer tree"));
    }

    public static void nativeRefDefault() {
        final var root = new Clock(new ArrayList<>()).memory.createTestRoot(false);
        final var refs = new ArrayList<@Nullable Object>();
        root.render(refHost("div", (Ref) refs::add), null);
        check(1 == refs.size() && null == refs.get(0), "A native test ref defaults to null");
    }

    public static void nodeMocks() {
        final var memory = new Clock(new ArrayList<>()).memory;
        final var refs = new ArrayList<@Nullable Object>();
        final var div = new Object();
        final var input = new Object();
        final var li = new Object();
        final var anchor = new Object();
        final Mock mock = description -> switch ((String)
                Objects.requireNonNull(props(description).get("type"))) {
            case "div" -> div;
            case "input" -> input;
            case "li" -> li;
            case "a" -> anchor;
            default -> new Object();
        };
        final Ref collect = refs::add;
        testRoot(memory, mock).render(refHost("div", collect), null);
        testRoot(memory, mock).render(refHost("input", collect), null);
        testRoot(memory, mock)
                .render(
                        host(
                                "div",
                                null,
                                host(
                                        "span",
                                        null,
                                        array(
                                                host("ul", null, refHost("li", collect)),
                                                host(
                                                        "ul",
                                                        null,
                                                        array(refHost("li", collect), refHost("li", collect)))))),
                        null);
        testRoot(memory, mock)
                .render(
                        ReactElement.createViewElement(REF_OWNER)
                                .input("tag", "a")
                                .input("collect", collect),
                        null);
        testRoot(memory, null).render(refHost("div", collect), null);
        testRoot(memory, null).render(refHost("div", collect), null);
        check(
                8 == refs.size()
                        && refs.get(0) == div
                        && refs.get(1) == input
                        && refs.get(2) == li
                        && refs.get(3) == li
                        && refs.get(4) == li
                        && refs.get(5) == anchor
                        && null == refs.get(6)
                        && null == refs.get(7),
                "Distinct mocks reach callback and string refs in commit order: " + refs);
    }

    public static void mockedRootInstance() {
        final var memory = new Clock(new ArrayList<>()).memory;
        final var expected = new Object();
        final var refs = new Object[1];
        final var root = testRoot(memory, description -> expected);
        root.render(refHost("div", (Ref) value -> refs[0] = Objects.requireNonNull(value)), null);
        check(
                expected == refs[0]
                        && expected
                                == root.getPublicHost(
                                        Objects.requireNonNull(child(root).get("stateNode"))),
                "Root inspection and the host ref use the same mock");
    }

    public static void mockedUpdatesAndUnmount() {
        final var memory = new Clock(new ArrayList<>()).memory;
        final var types = new ArrayList<@Nullable Object>();
        final var mock = (Mock) description -> {
            final var type = props(description).get("type");
            types.add(type);
            return type;
        };
        final var root = testRoot(memory, mock);
        final var owner = ReactElement.createViewElement(REF_OWNER);
        root.render(owner.dup().input("tag", "div"), null);
        root.render(owner.dup().input("tag", "span"), null);
        equal(List.of("div", "span"), types);
        root.unmount(null);

        final var unmounts = new int[1];
        final var nested = testRoot(memory, mock);
        nested.render(
                host("div", null, owner.dup().input("tag", "div").input("unmount", (Runnable) () -> unmounts[0]++)),
                null);
        nested.unmount(null);
        equal(1, unmounts[0]);
    }

    public static void secondaryContext(boolean production) {
        final var context = new Context<Object>();
        final var memory = new Clock(new ArrayList<>()).memory;
        final var primary = memory.createRoot(false);
        final var secondary = memory.createTestRoot(false);
        final var content = host("div", null, null);
        primary.render(context.provide("primary", content), null);
        final var diagnostic = captureWarnings(() -> secondary.render(context.provide("secondary", content), null));
        equal("", diagnostic);
        if (!production) {
            check(
                    null != props(context).get("_currentRenderer")
                            && null != props(context).get("_currentRenderer2"),
                    "The two host types reserve independent development context slots");
        }
    }

    public static void interleavedRootTiming() {
        withHook(first -> {
            final var second = new Clock(first.commits);
            final var one = first.memory.createTestRoot(true);
            final var two = second.memory.createTestRoot(true);
            first.time = 50;
            one.render(array(timed(first, 3, true), timed(first, 3, true)), null);
            first.turn(true);
            check(null == one.getTestTree(), "The first paused render has no committed duration");
            second.time = 150;
            two.render(array(timed(second, 7, true), timed(second, 7, true)), null);
            second.turn(true);
            first.time = 300;
            first.turn(false);
            equal(6, metric(props(Objects.requireNonNull(one.getTestTree())), "actualDuration"));
            check(null == two.getTestTree(), "The second paused render still has no committed duration");
            second.time = 500;
            second.turn(false);
            equal(14, metric(props(Objects.requireNonNull(two.getTestTree())), "actualDuration"));
        });
    }

    private static MemoryRenderer.Root testRoot(MemoryRenderer memory, @Nullable Mock mock) {
        final var root = memory.createTestRoot(false);
        props(root.getContainer()).set("createNodeMock", mock);
        return root;
    }

    private static ReactElement refHost(String tag, Object ref) {
        return ReactElement.createHostElement(tag, null, ref, JsPropertyMap.of());
    }

    private static void withHook(Consumer<Clock> action) {
        final var previous = Js.global().get(HOOK);
        final var commits = new ArrayList<Object>();
        final var hook = JsPropertyMap.of(
                "supportsFiber", true,
                "inject", (Inject) renderer -> 1,
                "onCommitFiberRoot",
                        (Commit) (id, root) -> {
                            equal(1, id);
                            commits.add(root);
                        });
        hook.set("onCommitFiberUnmount", (Commit) (id, fiber) -> {});
        Js.global().set(HOOK, hook);
        try {
            action.accept(new Clock(commits));
        } finally {
            if (null == previous) {
                Js.global().delete(HOOK);
            } else {
                Js.global().set(HOOK, previous);
            }
        }
    }

    private static JsPropertyMap<@Nullable Object> child(MemoryRenderer.Root root) {
        return props(Objects.requireNonNull(
                props(Objects.requireNonNull(root.getTestTree())).get("child")));
    }

    private static int metric(JsPropertyMap<@Nullable Object> record, String name) {
        return Js.coerceToInt(record.get(name));
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Set")
    private static final class NativeSet {
        native boolean has(Object value);
    }

    private static ReactElement timed(Clock clock, int duration, boolean shouldUpdate) {
        return ReactElement.createViewElement(TIMED)
                .input("clock", clock)
                .input("duration", duration)
                .input("shouldUpdate", shouldUpdate);
    }

    @JsFunction
    private interface Inject {
        int inject(Object renderer);
    }

    @JsFunction
    private interface Ref {
        void attach(@Nullable Object value);
    }

    @JsFunction
    private interface Mock {
        @Nullable
        Object create(Object description);
    }

    @JsFunction
    private interface Commit {
        void commit(int id, Object node);
    }

    @JsFunction
    private interface Report {
        void report(
                String id, String phase, double actual, double base, double start, double commit, Object interactions);
    }

    private record Sample(String id, String phase, double actual, double base, double start, double commit, int size) {}

    private static final class Clock implements MemoryRenderer.SchedulerCallbacks {
        private final List<Object> commits;
        private final List<Sample> samples = new ArrayList<>();
        private final Reconciler.ProfilerInteractions interactions = new Reconciler.ProfilerInteractions();
        private final MemoryRenderer memory = new MemoryRenderer(this);
        private final LinkedHashMap<Integer, Reconciler.Task> tasks = new LinkedHashMap<>();
        private int nextId;
        private int time;
        private boolean pauseAfterRender;
        private boolean yielded;

        Clock(List<Object> commits) {
            this.commits = commits;
        }

        @Override
        public double now() {
            return time;
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
            return true;
        }

        @Override
        public Reconciler.ProfilerInteractions profilerInteractions() {
            return interactions;
        }

        @Override
        public boolean shouldYield(double deadline) {
            return yielded;
        }

        @Override
        public int schedule(Reconciler.Task task, int delay) {
            check(0 == delay, "No delayed work in the Profiler fixtures");
            final int id = ++nextId;
            tasks.put(id, task);
            return id;
        }

        @Override
        public void cancel(int handle) {
            tasks.remove(handle);
        }

        void turn(boolean pause) {
            pauseAfterRender = pause;
            yielded = false;
            final var pending = tasks.values().iterator();
            check(pending.hasNext(), "One scheduled turn must be available");
            final var task = pending.next();
            pending.remove();
            task.run();
        }
    }

    private static final class App extends NativeView {
        @JsConstructor
        App(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var clock = (Clock) Objects.requireNonNull(requireInputs().get("clock"));
            final var multiplier = metric(requireInputs(), "multiplier");
            clock.time += 2;
            final var profiler = ReactElement.createHostElement(
                    "div",
                    null,
                    null,
                    JsPropertyMap.of(
                            "id",
                            "Profiler",
                            "children",
                            array(timed(clock, 3 * multiplier, true), timed(clock, 7 * multiplier, false)),
                            "onRender",
                            (Report) (id, phase, actual, base, start, commit, interactions) ->
                                    clock.samples.add(new Sample(
                                            id,
                                            phase,
                                            actual,
                                            base,
                                            start,
                                            commit,
                                            metric(props(interactions), "size")))));
            props(profiler).set("type", React.Profiler);
            return profiler;
        }
    }

    private static final class Timed extends NativeView implements OnShouldComponentUpdate {
        @JsConstructor
        Timed(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public boolean shouldComponentUpdate(JsPropertyMap<@Nullable Object> inputs) {
            return Boolean.TRUE.equals(inputs.get("shouldUpdate"));
        }

        @Override
        public @Nullable ReactNode render() {
            final var clock = (Clock) Objects.requireNonNull(requireInputs().get("clock"));
            clock.time += metric(requireInputs(), "duration");
            clock.yielded = clock.pauseAfterRender;
            return null;
        }
    }

    private static final class RefOwner extends NativeView implements OnComponentDidMount, OnComponentWillUnmount {
        @JsConstructor
        RefOwner(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return refHost((String) Objects.requireNonNull(requireInputs().get("tag")), "mock");
        }

        @Override
        public void componentDidMount() {
            final var collect = (Ref) requireInputs().get("collect");
            if (null != collect) {
                collect.attach(
                        props(Objects.requireNonNull(props(this).get("refs"))).get("mock"));
            }
        }

        @Override
        public void componentWillUnmount() {
            final var unmount = (Runnable) requireInputs().get("unmount");
            if (null != unmount) {
                unmount.run();
            }
        }
    }
}
