package react4j.dom;

import static react4j.webtesting.Checks.check;

import java.util.Objects;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.Contexts;
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
            checkContext(container, find);
            checkObserver();
        } finally {
            ReactDOM.unmountComponentAtNode(container);
            container.remove();
            Js.global().delete("__REACT_DEVTOOLS_GLOBAL_HOOK__");
        }
    }

    private static void checkContext(final Browser.Element container, final FindFiber find) {
        Contexts.register(String.class, "devtools-context", "default");
        final var context = Contexts.get(String.class, "devtools-context");
        final var contextRecord = record(context);
        contextRecord.set("displayName", "Theme");
        ReactDOM.render(
                DOM.section(
                        context.provide(
                                "outer",
                                context.provide(
                                        "inner",
                                        context.consumer().render(value -> DOM.span(Objects.requireNonNull(value))))),
                        context.consumer().render(value -> DOM.button(Objects.requireNonNull(value)))),
                container);
        final var provided =
                record(record(find.find(Browser.query(container, "span"))).get("return"));
        final var consumerType = record(provided.get("type"));
        check(Js.isTripleEqual(symbol("react.context"), consumerType.get("$$typeof")), "Recognizable consumer type");
        check(context == consumerType.get("_context"), "Consumer points to its original context");
        check("Theme".equals(contextRecord.get("displayName")), "Context keeps its display name");
        check("default".equals(contextRecord.get("_currentValue")), "Context exposes its default value");
        final var inner = record(provided.get("return"));
        final var outer = record(inner.get("return"));
        for (final var provider : new JsPropertyMap<?>[] {inner, outer}) {
            final var providerType = record(provider.get("type"));
            check(
                    Js.isTripleEqual(symbol("react.provider"), providerType.get("$$typeof")),
                    "Recognizable provider type");
            check(context == providerType.get("_context"), "Provider shares context identity with the consumer");
        }
        check("inner".equals(record(inner.get("memoizedProps")).get("value")), "Nearest provider exposes its value");
        check("outer".equals(record(outer.get("memoizedProps")).get("value")), "Outer provider exposes its own value");
        check("inner".equals(Browser.query(container, "span").getTextContent()), "Consumer renders the nearest value");
        final var unprovided =
                record(record(find.find(Browser.query(container, "button"))).get("return"));
        check(consumerType == unprovided.get("type"), "Consumer type identity is stable outside the provider");
        check(
                "default".equals(Browser.query(container, "button").getTextContent()),
                "Unprovided consumer renders the default");
    }

    private static JsPropertyMap<@Nullable Object> record(final @Nullable Object value) {
        return Js.uncheckedCast(Objects.requireNonNull(value));
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "Symbol.for")
    private static native Object symbol(String name);

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
