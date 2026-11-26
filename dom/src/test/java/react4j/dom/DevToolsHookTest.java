package react4j.dom;

import static react4j.webtesting.Checks.check;

import java.util.Objects;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.webtesting.Browser;

/** Exercises renderer registration and host selection across commits through the public extension hook. */
@NullMarked
public final class DevToolsHookTest {
    private static boolean _sawFallback;
    private static @Nullable Throwable _observerFailure;

    private DevToolsHookTest() {}

    @JsFunction
    private interface Inject {
        int inject(JsPropertyMap<Object> renderer);
    }

    @JsFunction
    private interface Notify {
        void call(int renderer, JsPropertyMap<@Nullable Object> record);
    }

    @JsFunction
    private interface FindFiber {
        @Nullable
        Object find(Object host);
    }

    @JsMethod
    public static void run() {
        final var observer = new Observer();
        final var hook = JsPropertyMap.of();
        hook.set("supportsFiber", true);
        hook.set("inject", (Inject) observer::inject);
        hook.set("onCommitFiberRoot", (Notify) observer::commit);
        hook.set("onCommitFiberUnmount", (Notify) observer::unmount);
        Js.global().set("__REACT_DEVTOOLS_GLOBAL_HOOK__", hook);
        final var container = Browser.append("div");
        try {
            ReactDOM.render(DOM.span("before"), container);
            final var host = container.getFirstElementChild();
            final var renderer = Objects.requireNonNull(observer.renderer, "DevTools renderer registration");
            final var root = observer.root;
            final var find = Js.<FindFiber>uncheckedCast(renderer.get("findFiberByHostInstance"));
            final var before = find.find(host);
            check(null != before, "Mounted DOM node selects a fiber");
            final var alternate =
                    Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(before).get("alternate");
            check(Js.isTripleEqual(null, alternate), "A newly mounted fiber has an explicit null alternate");
            ReactDOM.render(DOM.span("after"), container);
            check(root == observer.root, "Updating a mounted root preserves its DevTools identity");
            final var after = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(find.find(host));
            check(before == after.get("alternate"), "Host selection follows the committed alternate");
            check(host == after.get("stateNode"), "Committed host fiber points back to the highlighted node");
            check(ReactDOM.unmountComponentAtNode(container), "Unmount removes DevTools root");
            check(null == find.find(host), "Detached DOM node no longer selects a fiber");
            check(3 == observer.commits && 0 < observer.unmounts, "DevTools receives mount, update and unmount");
            observer.throwNotifications = true;
            try {
                final int unmounts = observer.unmounts;
                ReactDOM.render(DOM.span("throwing mount"), container);
                check("throwing mount".equals(container.getTextContent()), "Observer failure leaves mount intact");
                final var mounted = container.getFirstElementChild();
                ReactDOM.render(DOM.span("throwing update"), container);
                check("throwing update".equals(container.getTextContent()), "Observer failure leaves update intact");
                check(ReactDOM.unmountComponentAtNode(container), "Observer failure leaves unmount intact");
                check(container.getTextContent().isEmpty(), "Unmount removes the observed DOM");
                check(null == find.find(mounted), "Observer failure leaves host selection cleanup intact");
                check(6 == observer.commits && unmounts < observer.unmounts, "Both throwing callbacks were invoked");
            } finally {
                observer.throwNotifications = false;
            }
            checkObserver();
        } finally {
            container.remove();
            Js.global().delete("__REACT_DEVTOOLS_GLOBAL_HOOK__");
        }
    }

    @JsMethod
    public static void checkSuspense() {
        checkObserver();
        check(_sawFallback, "DevTools received the suspended tree during the browser scenarios");
    }

    private static void checkObserver() {
        final var failure = _observerFailure;
        if (null != failure) {
            throw new AssertionError("DevTools observer assertion failed", failure);
        }
    }

    private static final class Observer {
        private @Nullable JsPropertyMap<Object> renderer;
        private int commits;
        private int unmounts;
        private boolean throwNotifications;
        private @Nullable Object root;

        private int inject(JsPropertyMap<Object> internals) {
            renderer = internals;
            return 17;
        }

        private void commit(int id, JsPropertyMap<@Nullable Object> record) {
            root = record;
            commits++;
            try {
                check(17 == id, "Commit identifies the registered renderer");
                checkTree(Js.uncheckedCast(record.get("current")));
            } catch (Throwable failure) {
                _observerFailure = failure;
            }
            throwIfRequested();
        }

        private void checkTree(@Nullable JsPropertyMap<@Nullable Object> record) {
            for (var node = record; null != node; node = Js.uncheckedCast(node.get("sibling"))) {
                final JsPropertyMap<@Nullable Object> child = Js.uncheckedCast(node.get("child"));
                if (13 == Js.asInt(node.get("tag")) && null != node.get("memoizedState")) {
                    check(null != child && 7 == Js.asInt(child.get("tag")), "Suspense retains a primary fragment");
                    final JsPropertyMap<@Nullable Object> fallback =
                            Js.uncheckedCast(Objects.requireNonNull(child).get("sibling"));
                    check(
                            7 == Js.asInt(fallback.get("tag")) && null != fallback.get("child"),
                            "The extension can traverse the visible fallback");
                    _sawFallback = true;
                }
                checkTree(child);
            }
        }

        private void unmount(int id, JsPropertyMap<@Nullable Object> fiber) {
            unmounts++;
            try {
                check(17 == id && null != fiber.get("return"), "Unmount identifies a descendant fiber");
            } catch (Throwable failure) {
                _observerFailure = failure;
            }
            throwIfRequested();
        }

        private void throwIfRequested() {
            if (throwNotifications) {
                throw new IllegalStateException("DevTools observer failed");
            }
        }
    }
}
