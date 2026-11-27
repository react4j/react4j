package react4j.dom;

import static react4j.webtesting.Checks.check;

import java.util.ArrayDeque;
import java.util.Objects;
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
                    final var first = root.fiber.children;
                    final var leaf = first[0].children[0];
                    check(first[0].parent == root.fiber && leaf.parent == first[0], "Committed parent links");
                    check(
                            Reconciler.Kind.FRAGMENT == leaf.kind && null == leaf.hostInstance,
                            "Logical leaf has no host instance");
                    return Browser.callback(done -> root.render(ReactNode.of(b, null, a), done::run))
                            .then(moved -> {
                                final var second = root.fiber.children;
                                check(
                                        2 == second.length && second[0] == first[1] && second[1] == first[0],
                                        "Sparse reorder retains both fibers");
                                check(leaf == second[1].children[0], "A moved subtree retains its child fiber");
                                final var nested =
                                        ReactElement.createFragment("b", ReactElement.createFragment("leaf"));
                                return Browser.callback(done -> root.render(nested, done::run))
                                        .then(reparented -> {
                                            final var remaining = root.fiber.children;
                                            check(
                                                    1 == remaining.length && remaining[0] == first[1],
                                                    "A keyed single child retains its fiber");
                                            final var replacement = remaining[0].children[0];
                                            check(
                                                    leaf != replacement && replacement.parent == remaining[0],
                                                    "Keys are scoped to their parent");
                                            check(
                                                    null == container.firstChild,
                                                    "All commits completed without host children");
                                            return Browser.callback(done -> root.unmount(done::run));
                                        });
                            });
                })
                .thenDo(() -> check(0 == root.fiber.children.length, "Unmount empties the root fiber"));
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
        scheduler.runNext();

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
        private final ArrayDeque<Reconciler.Task> tasks = new ArrayDeque<>();
        private boolean shouldYield;

        @Override
        public double now() {
            return 0;
        }

        @Override
        public boolean shouldYield(double deadline) {
            return shouldYield;
        }

        @Override
        public int schedule(Reconciler.Task task, int delay) {
            check(0 == delay, "Only immediate work is expected");
            tasks.addLast(task);
            return tasks.size();
        }

        @Override
        public void cancel(int id) {
            throw new IllegalStateException("No delayed work is expected");
        }

        private void runNext() {
            tasks.removeFirst().run();
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
