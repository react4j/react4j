package react4j.dom;

import static react4j.webtesting.Checks.check;

import java.util.LinkedHashMap;
import java.util.Objects;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Context;
import react4j.ReactElement;
import react4j.ReactNode;
import react4j.internal.NativeView;
import react4j.internal.OnComponentDidMount;
import react4j.internal.OnComponentDidUpdate;
import react4j.reconciler.Reconciler;
import react4j.reconciler.ReconcilerScheduler;
import react4j.reconciler.ReconcilerTestAccess;
import react4j.webtesting.Browser;
import react4j.webtesting.BrowserPromise;

@NullMarked
public final class FiberReconciliationTest {
    private FiberReconciliationTest() {}

    @JsMethod(namespace = JsPackage.GLOBAL, name = "document.createDocumentFragment")
    private static native DomHost.Node container();

    @JsMethod
    public static BrowserPromise run() {
        final var container = container();
        final var root = Renderer.createRoot(container);
        final var a = ReactElement.createFragment("a", ReactElement.createFragment("leaf"));
        final var b = ReactElement.createFragment("b");
        return Browser.callback(done -> root.render(ReactNode.of(a, b), done::run))
                .then(ignored -> {
                    final var first = ReconcilerTestAccess.children(ReconcilerTestAccess.rootFiber(root));
                    final var leaf = ReconcilerTestAccess.children(first[0])[0];
                    check(
                            ReconcilerTestAccess.parent(first[0]) == ReconcilerTestAccess.rootFiber(root)
                                    && ReconcilerTestAccess.parent(leaf) == first[0],
                            "Committed parent links");
                    check(
                            ReconcilerTestAccess.isFragment(leaf) && null == ReconcilerTestAccess.hostInstance(leaf),
                            "Logical leaf has no host instance");
                    return Browser.callback(done -> root.render(ReactNode.of(b, null, a), done::run))
                            .then(moved -> {
                                final var second = ReconcilerTestAccess.children(ReconcilerTestAccess.rootFiber(root));
                                check(
                                        2 == second.length && second[0] == first[1] && second[1] == first[0],
                                        "Sparse reorder retains both fibers");
                                check(
                                        leaf == ReconcilerTestAccess.children(second[1])[0],
                                        "A moved subtree retains its child fiber");
                                final var nested =
                                        ReactElement.createFragment("b", ReactElement.createFragment("leaf"));
                                return Browser.callback(done -> root.render(nested, done::run))
                                        .then(reparented -> {
                                            final var remaining =
                                                    ReconcilerTestAccess.children(ReconcilerTestAccess.rootFiber(root));
                                            check(
                                                    1 == remaining.length && remaining[0] == first[1],
                                                    "A keyed single child retains its fiber");
                                            final var replacement = ReconcilerTestAccess.children(remaining[0])[0];
                                            check(
                                                    leaf != replacement
                                                            && ReconcilerTestAccess.parent(replacement) == remaining[0],
                                                    "Keys are scoped to their parent");
                                            check(
                                                    null == container.firstChild,
                                                    "All commits completed without host children");
                                            return Browser.callback(done -> root.unmount(done::run));
                                        });
                            });
                })
                .thenDo(() -> check(
                        0 == ReconcilerTestAccess.children(ReconcilerTestAccess.rootFiber(root)).length,
                        "Unmount empties the root fiber"));
    }

    @JsMethod
    public static BrowserPromise orderedStateQueue() {
        final var container = container();
        final var root = ReactDOM.createRoot(container);
        final var view = new StateQueueProbe[1];
        final var trace = new StringBuilder();
        final var node = ReactElement.createViewElement(inputs -> {
                    final var instance = new StateQueueProbe(inputs);
                    view[0] = instance;
                    return instance;
                })
                .input("trace", trace);
        ReactDOM.flushSync(() -> root.render(node));
        final var instance = Objects.requireNonNull(view[0]);
        return Browser.callback(done -> {
                    ReactDOM.flushSync(() -> {
                        Reconciler.deferredUpdates(() -> {
                            instance.append("A", null);
                            instance.append("C", () -> {
                                trace.append("callback:low:")
                                        .append(container.textContent)
                                        .append(',');
                                done.run();
                            });
                        });
                        instance.append(
                                "B",
                                () -> trace.append("callback:B:")
                                        .append(container.textContent)
                                        .append(','));
                        check("".equals(container.textContent), "A synchronous batch commits after its action returns");
                    });
                    check("B".equals(container.textContent), "The higher-priority state commits first");
                    check(
                            "mount:,update:B,callback:B:B,".contentEquals(trace),
                            "Lifecycle precedes the state callback");
                })
                .thenDo(() -> {
                    check(
                            "ACB".equals(container.textContent),
                            "Later work rebases the high-priority state in enqueue order");
                    check(
                            "mount:,update:B,callback:B:B,update:ACB,callback:low:ACB,".contentEquals(trace),
                            "Replayed updates do not invoke their callbacks twice");
                    ReactDOM.flushControlled(() -> instance.setStateWithCallback(
                            (NativeView.StateUpdater) (state, inputs) -> null, () -> trace.append("callback:no-op,")));
                    check(
                            "mount:,update:B,callback:B:B,update:ACB,callback:low:ACB,callback:no-op,"
                                    .contentEquals(trace),
                            "A null updater runs its callback without another lifecycle");
                    ReactDOM.flushSync(() -> Reconciler.unbatchedUpdates(() -> {
                        instance.replaceStateWithCallback(
                                JsPropertyMap.of("text", "R"), () -> trace.append("callback:replace,"));
                        check("R".equals(container.textContent), "Unbatched replacement commits immediately");
                    }));
                    check(
                            trace.toString().endsWith("update:R,callback:replace,"),
                            "The replacement callback sees a completed update");
                    instance._afterRender = () -> instance.setState(JsPropertyMap.of("text", "T"));
                    ReactDOM.flushSync(() -> instance.append("S", () -> trace.append("callback:render-suffix,")));
                    check(
                            "T".equals(container.textContent)
                                    && trace.toString().endsWith("update:RS,callback:render-suffix,update:T,"),
                            "Updates enqueued while rendering survive committing the processed queue: "
                                    + container.textContent + " / " + trace);
                    checkNestedStateUpdater(instance, container);
                    ReactDOM.flushSync(root::unmount);
                    check("".equals(container.textContent), "The root unmounts synchronously");
                });
    }

    private static void checkNestedStateUpdater(StateQueueProbe instance, DomHost.Node container) {
        final var console =
                Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(Js.global().get("console"));
        final var previous = console.get("error");
        final var messages = new StringBuilder();
        console.set("error", (LogMessage) message -> messages.append(message));
        try {
            ReactDOM.flushSync(() -> instance.setStateWithCallback(
                    (NativeView.StateUpdater) (state, props) -> {
                        instance.append("V", null);
                        return JsPropertyMap.<@Nullable Object>of(
                                "text", Objects.requireNonNull(state).get("text") + "U");
                    },
                    null));
        } finally {
            console.set("error", previous);
        }
        check("TUV".equals(container.textContent), "An update enqueued from an updater follows it in the queue");
        if ("development".equals(System.getProperty("react4j.environment"))) {
            check(
                    messages.toString().contains("An update (setState, replaceState, or forceUpdate) was scheduled"),
                    "Development reports side-effects in the active state updater");
        } else {
            check(0 == messages.length(), "Production omits the updater diagnostic");
        }
    }

    @JsFunction
    private interface LogMessage {
        void log(String message);
    }

    @JsMethod
    public static void legacyStateCallbacks() {
        final var container = container();
        final var trace = new StringBuilder();
        final var view = new StateQueueProbe[1];
        final var node = ReactElement.createViewElement(inputs -> {
                    final var instance = new StateQueueProbe(inputs);
                    view[0] = instance;
                    return instance;
                })
                .input("trace", trace);
        ReactDOM.render(node, container);
        final var instance = Objects.requireNonNull(view[0]);
        ReactDOM.batchedUpdates(() -> {
            instance.append("A", () -> trace.append("callback:A,"));
            instance.append("B", () -> trace.append("callback:B,"));
            instance.forceUpdateWithCallback(() -> trace.append("callback:force,"));
            instance.setStateWithCallback(null, null);
        });
        check("AB".equals(container.textContent), "Legacy state patches resolve in insertion order");
        check(
                "mount:,update:AB,callback:A,callback:B,callback:force,".contentEquals(trace),
                "A batched legacy commit retains all callbacks in insertion order");
        ReactDOM.batchedUpdates(() -> {
            instance.replaceStateWithCallback(JsPropertyMap.of("text", "R"), () -> trace.append("callback:replace,"));
            instance.append("C", null);
        });
        check(
                "RC".equals(container.textContent) && trace.toString().endsWith("update:RC,callback:replace,"),
                "Legacy replacement clears accumulated state before the next updater");
        instance.replaceStateWithCallback(null, () -> trace.append("callback:clear,"));
        check(
                null == instance.state()
                        && "empty state".equals(container.textContent)
                        && trace.toString().endsWith("update:empty state,callback:clear,"),
                "Replacing legacy state with null clears it before the callback");
        ReactDOM.unmountComponentAtNode(container);
        instance.forceUpdateWithCallback(() -> trace.append("callback:detached,"));
        check(
                "".equals(container.textContent) && !trace.toString().contains("callback:detached,"),
                "An unmounted instance cannot schedule another callback");
    }

    @JsMethod
    public static void expiredRoots() {
        final var scheduler = new ManualScheduler();
        final var first = container();
        final var second = container();
        final var a = new Reconciler.Root(first, true, DomReconcilerHost.INSTANCE, scheduler);
        final var b = new Reconciler.Root(second, true, DomReconcilerHost.INSTANCE, scheduler);
        final var trace = new StringBuilder();
        a.render(ReactNode.of("A"), () -> trace.append("A,"));
        scheduler.time = 1000;
        Reconciler.flushExpired(scheduler);
        b.render(ReactNode.of("B"), () -> trace.append("B,"));
        scheduler.time = 1500;
        Reconciler.flushExpired(scheduler);
        a.render(ReactNode.of("C"), () -> trace.append("C,"));
        scheduler.time = 5249;
        Reconciler.flushExpired(scheduler);
        check(0 == trace.length(), "Ordinary work stays scheduled before its deadline");
        scheduler.time = 5250;
        Reconciler.flushExpired(scheduler);
        check("A".equals(first.textContent) && "".equals(second.textContent), "Only the expired bucket commits");
        scheduler.time = 9000;
        Reconciler.flushExpired(scheduler);
        check("A,B,C,".contentEquals(trace), "Expired work commits across roots by deadline");
        check("C".equals(first.textContent) && "B".equals(second.textContent), "Both roots show their latest updates");
        check(scheduler.tasks.isEmpty(), "A forced flush consumes the scheduled callbacks");
        a.render(ReactNode.of("deferred"));
        Reconciler.interactiveUpdates(() -> b.render(ReactNode.of("interactive")));
        scheduler.runNext();
        check(
                "C".equals(first.textContent) && "interactive".equals(second.textContent),
                "An earlier host callback selects the higher-priority root");
        scheduler.runNext();
        check("deferred".equals(first.textContent), "The displaced root keeps its pending update");
        ReactDOM.flushSync(() -> {
            a.unmount();
            b.unmount();
        });
    }

    private static final class StateQueueProbe extends NativeView implements OnComponentDidMount, OnComponentDidUpdate {
        private final StringBuilder _trace;
        private @Nullable Runnable _afterRender;

        @JsConstructor
        private StateQueueProbe(JsPropertyMap<@Nullable Object> inputs) {
            super(inputs);
            _trace = (StringBuilder) Objects.requireNonNull(inputs.get("trace"));
            applyState(JsPropertyMap.of("text", ""));
        }

        private void append(String value, @Nullable StateCallback callback) {
            setStateWithCallback(
                    (StateUpdater) (previous, props) -> JsPropertyMap.<@Nullable Object>of(
                            "text", Objects.requireNonNull(previous).get("text") + value),
                    callback);
        }

        private String text() {
            final var state = state();
            return null == state ? "empty state" : Js.asString(state.get("text"));
        }

        @Override
        public void componentDidMount() {
            _trace.append("mount:").append(text()).append(',');
        }

        @Override
        public void componentDidUpdate(JsPropertyMap<@Nullable Object> previousProps) {
            _trace.append("update:").append(text()).append(',');
        }

        @Override
        public ReactNode render() {
            final var action = _afterRender;
            _afterRender = null;
            if (null != action) {
                action.run();
            }
            return ReactNode.of(text());
        }
    }

    @JsMethod
    public static void interruptedSuspense() {
        final var container = container();
        final var scheduler = new ManualScheduler();
        final var root = new Reconciler.Root(container, true, DomReconcilerHost.INSTANCE, scheduler);
        final var trace = new StringBuilder();
        final var pending = new Pending();
        final var context = new Context<>();
        final var committedChild = context.consumer().render(ignored -> {
            if (!pending.ready) {
                return RuntimeParityBrowserTest.throwPending(pending.thenable());
            }
            trace.append("primary,");
            return ReactNode.of("primary");
        });
        final var boundary = ReactElement.createSuspense(null, ReactNode.of("fallback"), 0, committedChild)
                .input("maxDuration", 0);
        root.render(boundary);
        scheduler.runNext();
        check("fallback".equals(container.textContent), "The first boundary commits its fallback");

        final var child = context.consumer().render(ignored -> {
            if (!pending.ready) {
                scheduler.shouldYield = true;
                trace.append("suspend,");
                return RuntimeParityBrowserTest.throwPending(pending.thenable());
            }
            trace.append("ready,");
            return ReactNode.of("ready");
        });
        final var sibling = context.consumer().render(ignored -> {
            trace.append("sibling,");
            return ReactNode.of("sibling");
        });
        final var suspended = ReactElement.createSuspense(null, ReactNode.of("loading"), 1000, child, sibling);
        root.render(ReactNode.of(boundary, suspended), () -> trace.append("committed,"));
        scheduler.runNext();
        check("suspend,".contentEquals(trace), "The interrupted primary yields before the sibling");
        check(1 == pending.subscriptions, "Both boundaries share one subscription to the thrown object");
        pending.resolve();
        scheduler.shouldYield = false;
        scheduler.runNext();
        check("suspend,primary,ready,sibling,committed,".contentEquals(trace), "Retry restarts before the sibling");
        check("primaryreadysibling".equals(container.textContent), "Both boundaries reveal their primary content");
        check(scheduler.tasks.isEmpty(), "The ping schedules no extra render after reveal");

        final var forced = new Pending();
        final var forcingChild = context.consumer()
                .render(ignored -> forced.ready
                        ? ReactNode.of("ready")
                        : RuntimeParityBrowserTest.throwPending(forced.thenable()));
        final var resolver = context.consumer().render(ignored -> {
            if (!forced.ready) {
                forced.resolve();
            }
            return ReactNode.of("sibling");
        });
        final var forcingBoundary =
                ReactElement.createSuspense(null, ReactNode.of("loading"), 1000, forcingChild, resolver);
        Reconciler.interactiveUpdates(() -> root.render(forcingBoundary));
        Reconciler.flushInteractive();
        check("readysibling".equals(container.textContent), "Flushing urgent work retries an earlier render step");
        check(scheduler.tasks.isEmpty(), "A synchronous retry leaves no redundant scheduled callback");

        final var abandoned = new Pending();
        final var abandonedChild =
                context.consumer().render(ignored -> RuntimeParityBrowserTest.throwPending(abandoned.thenable()));
        root.render(ReactElement.createSuspense(null, ReactNode.of("waiting"), 0, abandonedChild)
                .input("maxDuration", 0));
        scheduler.runNext();
        root.unmount(() -> trace.append("unmounted,"));
        scheduler.runNext();
        abandoned.resolve();
        check(scheduler.tasks.isEmpty() && "".equals(container.textContent), "An unmounted boundary ignores its ping");
    }

    private static final class ManualScheduler implements ReconcilerScheduler {
        private final LinkedHashMap<Integer, Reconciler.Task> tasks = new LinkedHashMap<>();
        private boolean shouldYield;
        private double time;
        private int _nextHandle;

        @Override
        public double now() {
            return time;
        }

        @Override
        public boolean shouldYield(double deadline) {
            return shouldYield;
        }

        @Override
        public int schedule(Reconciler.Task task, int delay) {
            check(0 == delay, "Only immediate work is expected");
            final var handle = ++_nextHandle;
            tasks.put(handle, task);
            return handle;
        }

        @Override
        public void cancel(int id) {
            tasks.remove(id);
        }

        private void runNext() {
            final var iterator = tasks.values().iterator();
            final var next = iterator.next();
            iterator.remove();
            next.run();
        }
    }

    @JsFunction
    private interface Subscribe {
        void then(Reconciler.Task resolve);
    }

    private static final class Pending {
        private Reconciler.@Nullable Task _resolve;
        private boolean ready;
        private int subscriptions;
        private final BrowserPromise _thenable = Js.uncheckedCast(JsPropertyMap.of("then", (Subscribe) resolve -> {
            _resolve = resolve;
            subscriptions++;
        }));

        private BrowserPromise thenable() {
            return _thenable;
        }

        private void resolve() {
            ready = true;
            Objects.requireNonNull(_resolve).run();
        }
    }
}
