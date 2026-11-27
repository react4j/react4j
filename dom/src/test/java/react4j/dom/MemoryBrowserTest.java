package react4j.dom;

import static react4j.webtesting.Checks.check;
import static react4j.webtesting.Checks.equal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
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
import react4j.internal.ViewConstructorFunction;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class MemoryBrowserTest {
    private MemoryBrowserTest() {}

    @JsMethod
    public static void run() {
        final var scheduler = new ControlledScheduler();
        final var memory = new MemoryRenderer(scheduler);
        persistentSnapshots(memory);
        persistentVisibility(memory);
        persistentCommitRecovery(memory);
        iterableChildren(memory);
        offscreenWorkResumes(memory, scheduler);
        realUserTiming(memory, scheduler);
        profiledWork();
    }

    private static void profiledWork() {
        final var scheduler = new ControlledScheduler();
        scheduler.profiling = true;
        scheduler.time = 10;
        final var memory = new MemoryRenderer(scheduler);
        final var legacy = memory.createRoot(false);
        final var samples = new ArrayList<ProfileSample>();
        final ProfileCallback callback = (id, phase, actual, base, start, commit) ->
                samples.add(new ProfileSample(phase, actual, base, start, commit));
        final var broken = ReactElement.createFragment(
                null,
                timed(() -> {
                    scheduler.time += 7;
                    return ReactNode.of("discarded");
                }),
                timed(() -> {
                    scheduler.time += 3;
                    throw new IllegalStateException("Profiled rendering failed");
                }));
        final var recovered = timed(() -> {
            scheduler.time += 20;
            return ReactNode.of("recovered");
        });
        final var boundary = ReactElement.createViewElement(TimedBoundary::new)
                .input("time", (Runnable) () -> scheduler.time += 2)
                .input("initial", broken)
                .input("recovered", recovered);
        OriginalHostCasesBrowserTest.captureWarnings(() -> legacy.render(profiler(boundary, callback), null));
        check(
                samples.equals(List.of(
                        new ProfileSample("mount", 12, 2, 10, 22), new ProfileSample("update", 22, 22, 22, 44))),
                "A Profiler counts discarded render time only in the actual duration: " + samples);
        equal("recovered", text(children(legacy.getContainer())[0]));
        legacy.unmount(null);
        assertBadChild(
                legacy,
                WrappedViewsBrowserTest.element(React.Profiler, null, JsPropertyMap.of("children", "invalid")),
                "onRender is not a function");

        final var concurrent = memory.createRoot(true);
        final var submitted = new ArrayList<ProfileSample>();
        final TracingProfileCallback traced = (id, phase, actual, base, start, commit, interactions) -> {
            check(
                    interactions.has("first")
                            && interactions.has("second")
                            && interactions.has("shared")
                            && 3 == interactions.size(),
                    "Batched work merges and deduplicates submitted interactions");
            equal("new", text(children(concurrent.getContainer())[0]));
            submitted.add(new ProfileSample(phase, actual, base, start, commit));
        };
        scheduler.interactions = interactions("first");
        concurrent.render(profiler(ReactNode.of("old"), traced), null);
        scheduler.interactions = interactions("second");
        concurrent.render(
                profiler(
                        timed(() -> {
                            scheduler.time += 3;
                            return ReactNode.of("new");
                        }),
                        traced),
                null);
        scheduler.interactions = null;
        check(submitted.isEmpty(), "A pending render leaves its Profiler callback untouched");
        scheduler.flush();
        check(
                submitted.equals(List.of(new ProfileSample("mount", 3, 3, 44, 47))),
                "A Profiler reports one commit for the latest work in the batch: " + submitted);
        concurrent.unmount(null);
        scheduler.flush();
    }

    private static Reconciler.ProfilerInteractions interactions(String name) {
        final var interactions = new Reconciler.ProfilerInteractions();
        interactions.add("shared");
        interactions.add(name);
        return interactions;
    }

    private static ReactElement profiler(ReactNode child, Object callback) {
        return WrappedViewsBrowserTest.element(
                React.Profiler, null, JsPropertyMap.of("id", "profile", "onRender", callback, "children", child));
    }

    private static ReactElement timed(Supplier<ReactNode> render) {
        return ReactElement.createViewElement(TimedView::new).input("render", render);
    }

    private record ProfileSample(String phase, double actual, double base, double start, double commit) {}

    @JsFunction
    private interface ProfileCallback {
        void report(String id, String phase, double actual, double base, double start, double commit);
    }

    @JsFunction
    private interface TracingProfileCallback {
        void report(
                String id,
                String phase,
                double actual,
                double base,
                double start,
                double commit,
                BrowserInteractions interactions);
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Set")
    private interface BrowserInteractions {
        boolean has(String name);

        @JsProperty(name = "size")
        int size();
    }

    private static final class TimedView extends NativeView {
        @JsConstructor
        TimedView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return Js.<Supplier<ReactNode>>uncheckedCast(requireInputs().get("render"))
                    .get();
        }
    }

    private static final class TimedBoundary extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        TimedBoundary(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            final var inputs = requireInputs();
            Js.<Runnable>uncheckedCast(inputs.get("time")).run();
            return Js.uncheckedCast(Objects.requireNonNull(inputs.get(null == state() ? "initial" : "recovered")));
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            setState(JsPropertyMap.of("recovered", true));
        }
    }

    private static void persistentVisibility(MemoryRenderer memory) {
        final var root = memory.createPersistentRoot(false);
        final var label = ReactElement.createHostElement("label", null, null, JsPropertyMap.of());
        final var context = new Context<String>();
        final BrowserPromise pending = Js.uncheckedCast(JsPropertyMap.of("then", (Reconciler.Task) () -> {}));
        final var ready = context.consumer().render(value -> ReactNode.of("ready"));
        final var waiting = context.consumer().render(value -> RuntimeParityBrowserTest.throwPending(pending));
        final var spinner = ReactElement.createHostElement("spinner", null, null, JsPropertyMap.of());
        final var primary =
                hostWithChildren(ReactElement.createSuspense(null, spinner, 100, label, ReactNode.of("text"), ready));
        root.render(primary, null);
        final var visible = children(children(root.getContainer())[0]);
        root.render(
                hostWithChildren(ReactElement.createSuspense(null, spinner, 100, label, ReactNode.of("text"), waiting)),
                null);
        final var fallback = children(children(root.getContainer())[0]);
        check(3 == fallback.length, "Persistent fallback preserves both hidden primary host children");
        check(hidden(fallback[0]) && hidden(fallback[1]), "Persistent visibility clones both element and text");
        check(!hidden(visible[0]) && !hidden(visible[1]), "A prior snapshot keeps visible children");
        root.render(primary, null);
        final var recovered = children(children(root.getContainer())[0]);
        check(
                recovered[0] == visible[0] && recovered[1] == visible[1],
                "Revealed children reuse original host records");
        check(hidden(fallback[0]) && hidden(fallback[1]), "A prior fallback snapshot keeps hidden children");
        root.unmount(null);
    }

    private static void persistentCommitRecovery(MemoryRenderer memory) {
        final var root = memory.createPersistentRoot(false);
        final ViewConstructorFunction boundary = CommitBoundary::new;
        final var seen = new StringBuilder();
        final var good = ReactElement.createHostElement("leaf", "good", null, JsPropertyMap.of("children", "good"));
        root.render(
                hostWithChildren(ReactElement.createViewElement(boundary)
                        .input("child", good)
                        .input("seen", seen)),
                null);
        final var original = children(children(root.getContainer())[0]);
        final var bad = ReactElement.createHostElement(
                "leaf",
                "bad",
                (HostRef) value -> {
                    if (null != value) {
                        throw new IllegalStateException("Host ref failed");
                    }
                },
                JsPropertyMap.of());
        OriginalHostCasesBrowserTest.captureWarnings(() -> root.render(
                hostWithChildren(ReactElement.createViewElement(boundary)
                        .input("child", bad)
                        .input("seen", seen)),
                null));
        check("caught".contentEquals(seen), "Boundary receives a persistent host ref failure");
        check(0 == children(children(root.getContainer())[0]).length, "Commit failure publishes the cleared branch");
        equal("good", text(original[0]));
        root.unmount(null);
    }

    private static void iterableChildren(MemoryRenderer memory) {
        final var root = memory.createRoot(false);
        final var keyed = ReactElement.createHostElement("keyed", "same", null, JsPropertyMap.of());
        final var warnings = OriginalHostCasesBrowserTest.captureWarnings(
                () -> root.render(hostWithChildren(iterable(iterable("first"), keyed, keyed)), null));
        final var rendered = children(children(root.getContainer())[0]);
        check(3 == rendered.length, "Nested iterable reconciles its child slots");
        equal("first", text(rendered[0]));
        final var development = "development".equals(System.getProperty("react4j.environment"));
        check(
                !development
                        || 1 == warnings.length && warnings[0].contains("Encountered two children with the same key"),
                "Development warns about duplicate keys from iterables");

        final IteratorMethod function = () -> {
            throw new AssertionError("A function child must never be iterated");
        };
        Js.<JsPropertyMap<Object>>uncheckedCast(function).set("@@iterator", function);
        final var functionWarnings = OriginalHostCasesBrowserTest.captureWarnings(() -> {
            root.render(hostWithChildren(function), null);
            root.render(hostWithChildren(function), null);
        });
        check(0 == children(children(root.getContainer())[0]).length, "Function children are ignored");
        check(
                !development || 1 == functionWarnings.length && functionWarnings[0].contains("Functions are not valid"),
                "Development deduplicates repeated function children warnings");

        final IteratorMethod missingIterator = () -> null;
        assertBadChild(root, JsPropertyMap.of("@@iterator", missingIterator), "provided no iterator");
        final IteratorMethod malformedIterator = () -> JsPropertyMap.of("next", (IteratorMethod) () -> "wrong");
        assertBadChild(root, JsPropertyMap.of("@@iterator", malformedIterator), "Iterator result is not an object");
        assertBadChild(root, JsPropertyMap.of("unknown", "value"), "object with keys {unknown}");
        root.render(hostWithChildren(iterable("recovered")), null);
        equal("recovered", text(children(children(root.getContainer())[0])[0]));
        root.unmount(null);
    }

    private static void assertBadChild(MemoryRenderer.Root root, Object child, String diagnostic) {
        OriginalHostCasesBrowserTest.captureWarnings(() -> {
            try {
                root.render(hostWithChildren(child), null);
            } catch (Throwable error) {
                final var message = error.getMessage();
                check(
                        null != message && message.contains(diagnostic),
                        "Unexpected invalid child diagnostic: " + message);
                return;
            }
            throw new AssertionError("Invalid children should fail");
        });
        check(0 == children(root.getContainer()).length, "A failed child commit clears the root");
    }

    private static Object iterable(Object... values) {
        final IteratorMethod iterator = () -> {
            final int[] index = {0};
            final IteratorMethod next = () -> index[0] == values.length
                    ? JsPropertyMap.of("done", true)
                    : JsPropertyMap.of("value", values[index[0]++], "done", false);
            return JsPropertyMap.of("next", next);
        };
        return JsPropertyMap.of("@@iterator", iterator);
    }

    private static ReactElement hostWithChildren(Object children) {
        return ReactElement.createHostElement("parent", null, null, JsPropertyMap.of("children", children));
    }

    private static boolean hidden(Object instance) {
        return Boolean.TRUE.equals(
                Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(instance).get("hidden"));
    }

    @JsFunction
    private interface IteratorMethod {
        @Nullable
        Object invoke();
    }

    @JsFunction
    private interface HostRef {
        void accept(@Nullable Object value);
    }

    private static void persistentSnapshots(MemoryRenderer memory) {
        final var root = memory.createPersistentRoot(false);
        final var target = memory.createPersistentRoot(false);
        final var nestedTarget = memory.createPersistentRoot(false);
        final var stable = ReactElement.createHostElement("stable", "stable", null, JsPropertyMap.of());
        final var oldBranch =
                ReactElement.createHostElement("branch", "branch", null, JsPropertyMap.of("children", "old"));
        final var newBranch =
                ReactElement.createHostElement("branch", "branch", null, JsPropertyMap.of("children", "new"));
        final var portal = memory.createPortal(
                ReactElement.createFragment(null, oldBranch, memory.createPortal(oldBranch, nestedTarget, "nested")),
                target,
                null);
        root.render(ReactElement.createFragment(null, stable, oldBranch, portal), null);
        final var original = children(root.getContainer());
        final var originalPortal = children(target.getContainer());
        final var originalNestedPortal = children(nestedTarget.getContainer());

        final var nextPortal = memory.createPortal(
                ReactElement.createFragment(null, newBranch, memory.createPortal(newBranch, nestedTarget, "nested")),
                target,
                null);
        final var next = ReactElement.createFragment(null, newBranch, stable, nextPortal);
        root.render(next, null);
        final var updated = children(root.getContainer());
        check(
                original != updated && original[0] == updated[1],
                "Persistent child reordering preserves the reused host");
        equal("old", text(original[1]));
        equal("new", text(updated[0]));
        equal("old", text(originalPortal[0]));
        equal("new", text(children(target.getContainer())[0]));
        equal("old", text(originalNestedPortal[0]));
        equal("new", text(children(nestedTarget.getContainer())[0]));
        root.render(next, null);
        check(updated == children(root.getContainer()), "A persistent bailout retains the committed child array");
        root.unmount(null);
        check(0 == children(root.getContainer()).length, "Persistent root unmount publishes an empty child set");
        check(0 == children(target.getContainer()).length, "Persistent portal deletion publishes an empty child set");
        check(
                0 == children(nestedTarget.getContainer()).length,
                "Deleting a persistent portal also clears nested portals");
        equal("new", text(updated[0]));
    }

    private static void offscreenWorkResumes(MemoryRenderer memory, ControlledScheduler scheduler) {
        final var root = memory.createRoot(true);
        final ViewConstructorFunction factory = OffscreenView::new;
        Js.<JsPropertyMap<Object>>uncheckedCast(factory).set("displayName", "OffscreenView");
        final var content =
                ReactElement.createViewElement(factory).input("yield", (Reconciler.Task) () -> scheduler.paused = true);
        final var shell = ReactElement.createHostElement(
                "shell", null, null, JsPropertyMap.of("hidden", true, "children", content));
        final var observer = new PerformanceObserver(() -> {});
        observer.observe(JsPropertyMap.of("entryTypes", new String[] {"measure"}));
        try {
            memory.flushSync(() -> root.render(shell, null));
            final var instance = children(root.getContainer())[0];
            check(0 == children(instance).length, "The hidden host shell commits before its descendants");
            scheduler.runNext();
            check(0 == children(instance).length, "Yielded offscreen work stays out of the committed host");
            scheduler.paused = false;
            scheduler.flush();
            equal("offscreen", text(children(instance)[0]));
            var spans = 0;
            var yielded = false;
            for (final var event : observer.takeRecords()) {
                spans += "⚛ OffscreenView [mount]".equals(event.name()) ? 1 : 0;
                yielded |= "⚛ (React Tree Reconciliation: Yielded)".equals(event.name());
            }
            check(yielded && 2 == spans, "A yielded fiber is measured again when its offscreen work resumes");
            root.unmount(null);
            scheduler.flush();
        } finally {
            observer.disconnect();
        }
    }

    private static void realUserTiming(MemoryRenderer memory, ControlledScheduler scheduler) {
        final var observer = new PerformanceObserver(() -> {});
        observer.observe(JsPropertyMap.of("entryTypes", new String[] {"measure"}));
        try {
            final var root = memory.createRoot(true);
            root.render(
                    ReactElement.createHostElement("item", null, null, JsPropertyMap.of("children", "content")), null);
            scheduler.time = 6_000;
            scheduler.flush();
            final var names = new ArrayList<String>();
            for (final var event : observer.takeRecords()) {
                names.add(event.name());
            }
            check(
                    names.equals(List.of(
                            "⛔ (Waiting for async callback... will force flush in 5250 ms) Warning: React was blocked"
                                    + " by main thread",
                            "⚛ (React Tree Reconciliation: Completed Root)",
                            "⚛ (Committing Snapshot Effects: 0 Total)",
                            "⚛ (Committing Host Effects: 1 Total)",
                            "⚛ (Calling Lifecycle Methods: 0 Total)",
                            "⚛ (Committing Changes)")),
                    "Unexpected User Timing entries: " + names);
            for (final var name : names) {
                check(0 == performanceEntries(name).length, "User Timing leaves no buffered measure: " + name);
            }
            root.unmount(null);
            scheduler.flush();
            observer.takeRecords();

            final var legacy = memory.createRoot(false);
            final ViewConstructorFunction component = CascadingView::new;
            Js.<JsPropertyMap<Object>>uncheckedCast(component).set("displayName", "CascadingView");
            legacy.render(ReactElement.createViewElement(component), null);
            final var lifecycleNames = new ArrayList<String>();
            for (final var event : observer.takeRecords()) {
                lifecycleNames.add(event.name());
            }
            check(lifecycleNames.contains("⚛ CascadingView [mount]"), "User Timing measures the initial view");
            check(lifecycleNames.contains("⚛ CascadingView [update]"), "User Timing measures the cascading update");
            check(
                    lifecycleNames.contains("⛔ CascadingView.componentDidMount Warning: Scheduled a cascading update"),
                    "User Timing attributes the cascading update to the lifecycle");
            legacy.unmount(null);
        } finally {
            observer.disconnect();
        }
    }

    private static Object[] children(Object container) {
        return Js.uncheckedCast(
                Js.<JsPropertyMap<Object>>uncheckedCast(container).get("children"));
    }

    private static String text(Object instance) {
        return Js.uncheckedCast(
                Js.<JsPropertyMap<Object>>uncheckedCast(instance).get("text"));
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "performance.getEntriesByName")
    private static native PerformanceEntry[] performanceEntries(String name);

    @JsFunction
    private interface Observe {
        void receive();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "PerformanceEntry")
    private interface PerformanceEntry {
        @JsProperty(name = "name")
        String name();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "PerformanceObserver")
    private static final class PerformanceObserver {
        private PerformanceObserver(Observe callback) {}

        native void observe(Object options);

        native PerformanceEntry[] takeRecords();

        native void disconnect();
    }

    private static final class ControlledScheduler implements MemoryRenderer.SchedulerCallbacks {
        private Reconciler.@Nullable Task pending;
        private double time;
        private boolean paused;
        private boolean profiling;
        private Reconciler.@Nullable ProfilerInteractions interactions;

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
            return true;
        }

        @Override
        public boolean replayFailedRender() {
            return false;
        }

        @Override
        public boolean profilerEnabled() {
            return profiling;
        }

        @Override
        public Reconciler.@Nullable ProfilerInteractions profilerInteractions() {
            return interactions;
        }

        @Override
        public boolean shouldYield(double deadline) {
            return paused;
        }

        @Override
        public int schedule(Reconciler.Task task, int delay) {
            check(0 == delay, "Unexpected timeout in the in-memory scheduler");
            check(null == pending, "Concurrent root scheduled a duplicate callback");
            pending = task;
            return 1;
        }

        @Override
        public void cancel(int handle) {
            check(1 == handle, "Unrecognized callback handle");
            pending = null;
        }

        private void runNext() {
            final var task = pending;
            if (null == task) {
                throw new AssertionError("In-memory scheduler should have work");
            }
            pending = null;
            task.run();
        }

        private void flush() {
            runNext();
            check(null == pending, "In-memory scheduler did not settle");
        }
    }

    private static final class OffscreenView extends NativeView {
        @JsConstructor
        OffscreenView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactElement render() {
            Js.<Reconciler.Task>uncheckedCast(
                            Objects.requireNonNull(requireInputs().get("yield")))
                    .run();
            return ReactElement.createHostElement("leaf", null, null, JsPropertyMap.of("children", "offscreen"));
        }
    }

    private static final class CascadingView extends NativeView implements OnComponentDidMount {
        @JsConstructor
        CascadingView(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactElement render() {
            final var state = state();
            return ReactElement.createHostElement(
                    "item", null, null, JsPropertyMap.of("children", null == state ? "initial" : state.get("message")));
        }

        @Override
        public void componentDidMount() {
            setState(JsPropertyMap.of("message", "updated"));
        }
    }

    private static final class CommitBoundary extends NativeView implements OnComponentDidCatch {
        @JsConstructor
        CommitBoundary(@Nullable JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
        }

        @Override
        public ReactNode render() {
            return Js.uncheckedCast(Objects.requireNonNull(requireInputs().get("child")));
        }

        @Override
        public void componentDidCatch(JsError error, ReactErrorInfo info) {
            final var seen =
                    (StringBuilder) Objects.requireNonNull(requireInputs().get("seen"));
            seen.append("caught");
        }
    }
}
