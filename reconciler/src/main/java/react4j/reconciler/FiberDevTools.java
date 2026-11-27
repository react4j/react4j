package react4j.reconciler;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.jspecify.annotations.Nullable;
import react4j.React;

/** Publishes committed trees in the React 16.6 Fiber protocol. */
public final class FiberDevTools {
    private static boolean _loggedError;
    private final @Nullable Hook _hook;
    private final int _id;
    private final Reconciler.WeakKeys<Reconciler.Root, JsPropertyMap<@Nullable Object>> _roots =
            new Reconciler.WeakKeys<>();
    private final Reconciler.WeakKeys<Reconciler.Fiber, JsPropertyMap<@Nullable Object>> _fibers =
            new Reconciler.WeakKeys<>();
    private final Reconciler.WeakKeys<Object, JsPropertyMap<@Nullable Object>> _hosts;

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    private static class Hook {
        boolean supportsFiber;
        boolean isDisabled;

        native int inject(JsPropertyMap<Object> renderer);

        native void onCommitFiberRoot(int id, JsPropertyMap<@Nullable Object> root);

        native void onCommitFiberUnmount(int id, JsPropertyMap<@Nullable Object> fiber);
    }

    @JsFunction
    private interface FindFiber {
        @Nullable
        Object find(Object node);
    }

    private FiberDevTools(
            @Nullable Hook hook, int id, Reconciler.WeakKeys<Object, JsPropertyMap<@Nullable Object>> hosts) {
        _hook = hook;
        _id = id;
        _hosts = hosts;
    }

    public static FiberDevTools connect(String rendererPackageName) {
        final var hosts = new Reconciler.WeakKeys<Object, JsPropertyMap<@Nullable Object>>();
        final Hook hook = Js.uncheckedCast(Js.global().get("__REACT_DEVTOOLS_GLOBAL_HOOK__"));
        if (null == hook || !hook.supportsFiber || hook.isDisabled) {
            return new FiberDevTools(null, 0, hosts);
        }
        final var renderer = JsPropertyMap.of();
        renderer.set("version", "16.6.0");
        renderer.set("rendererPackageName", rendererPackageName);
        renderer.set("bundleType", "production".equals(System.getProperty("react4j.environment")) ? 0 : 1);
        renderer.set("findFiberByHostInstance", (FindFiber) hosts::get);
        final int id;
        try {
            id = hook.inject(renderer);
        } catch (Throwable error) {
            logError(error);
            return new FiberDevTools(null, 0, hosts);
        }
        return new FiberDevTools(hook, id, hosts);
    }

    private static void logError(Throwable error) {
        if (!_loggedError && !"production".equals(System.getProperty("react4j.environment"))) {
            _loggedError = true;
            warn("Warning: React DevTools encountered an error: " + error);
        }
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "console.error")
    private static native void warn(String message);

    public boolean isActive() {
        return null != _hook;
    }

    @Nullable
    Object tree(Reconciler.Root root) {
        final var record = _roots.get(root);
        return null == record ? null : record.get("current");
    }

    public void onCommitRoot(Reconciler.Root root) {
        final var record = publish(root);
        if (null != _hook) {
            try {
                _hook.onCommitFiberRoot(_id, record);
            } catch (Throwable error) {
                logError(error);
            }
        }
    }

    private JsPropertyMap<@Nullable Object> publish(Reconciler.Root root) {
        var record = _roots.get(root);
        if (null == record) {
            record = JsPropertyMap.of();
            record.set("containerInfo", root.container);
            _roots.set(root, record);
        }
        record.set("memoizedInteractions", root.profilerInteractions());
        final var current = snapshot(root.fiber, null, record);
        record.set("current", current);
        return record;
    }

    private static int viewTag(Reconciler.Fiber fiber) {
        final var descriptor = Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(fiber.type);
        final var marker = descriptor.get("$$typeof");
        if (React.ForwardRef == marker) {
            return 11;
        }
        if (React.Memo == marker) {
            return 14;
        }
        if (React.Lazy == marker) {
            return 16;
        }
        return null != fiber.view && fiber.isFunctionComponent(fiber.view) ? 0 : 1;
    }

    private JsPropertyMap<@Nullable Object> snapshot(
            Reconciler.Fiber fiber, @Nullable Object parent, JsPropertyMap<@Nullable Object> root) {
        final var old = _fibers.get(fiber);
        final var next = alternate(old);
        _fibers.set(fiber, next);
        next.set("return", parent);
        final int tag =
                switch (fiber.kind) {
                    case VIEW -> viewTag(fiber);
                    case ROOT -> 3;
                    case PORTAL -> 4;
                    case HOST -> 5;
                    case TEXT -> 6;
                    case FRAGMENT ->
                        React.Suspense == fiber.type
                                ? 13
                                : React.Profiler == fiber.type
                                        ? 12
                                        : React.StrictMode == fiber.type || React.ConcurrentMode == fiber.type ? 8 : 7;
                    case CONSUMER -> 9;
                    case PROVIDER -> 10;
                };
        next.set("tag", tag);
        next.set("type", fiber.type);
        next.set("elementType", fiber.type);
        next.set("key", fiber.key);
        next.set("ref", fiber.ref);
        next.set(
                "stateNode",
                switch (fiber.kind) {
                    case ROOT -> root;
                    case HOST, TEXT -> fiber.hostInstance;
                    default -> fiber.publicInstance();
                });
        if (null != fiber.hostInstance) {
            _hosts.set(fiber.hostInstance, next);
        }
        next.set(
                "memoizedProps",
                Reconciler.Kind.TEXT == fiber.kind
                        ? fiber.devToolsText()
                        : null == fiber.node ? null : fiber.node.inputs());
        if (Reconciler.Kind.ROOT == fiber.kind) {
            final var state = JsPropertyMap.<@Nullable Object>of();
            state.set("element", 0 == fiber.children.length ? null : fiber.children[0].type);
            next.set("memoizedState", state);
        } else {
            next.set("memoizedState", null == fiber.view ? null : fiber.view.state());
        }
        final int mode = 4 | (fiber.strict ? 2 : 0) | (fiber.concurrentUpdates ? 1 : 0);
        next.set("mode", mode);
        next.set("effectTag", fiber.performedWork() ? 1 : 0);
        next.set("actualStartTime", fiber.actualStartTime());
        next.set("actualDuration", fiber.actualDuration());
        next.set("treeBaseDuration", fiber.treeBaseDuration);
        next.set("selfBaseDuration", fiber.selfBaseDuration);
        final var owner = null == fiber.node ? null : fiber.node.owner();
        next.set("_debugOwner", owner instanceof Reconciler.Fiber owningFiber ? _fibers.get(owningFiber) : null);
        next.set("sibling", null);
        final int count = fiber.suspendedPrimaryCount();
        if (count >= 0) {
            // React 16.6 keeps the hidden primary tree and visible fallback under sibling fragments.
            final JsPropertyMap<@Nullable Object> oldPrimary =
                    null == old || null == old.get("memoizedState") ? null : Js.uncheckedCast(old.get("child"));
            final var primary = fragment(oldPrimary, next, mode);
            final var fallback =
                    fragment(null == oldPrimary ? null : Js.uncheckedCast(oldPrimary.get("sibling")), next, mode);
            next.set("memoizedState", JsPropertyMap.of("didTimeout", true));
            next.set("child", primary);
            primary.set("sibling", fallback);
            children(fiber, primary, root, 0, count);
            children(fiber, fallback, root, count, fiber.children.length);
        } else {
            children(fiber, next, root, 0, fiber.children.length);
        }
        return next;
    }

    private void children(
            Reconciler.Fiber fiber,
            JsPropertyMap<@Nullable Object> next,
            JsPropertyMap<@Nullable Object> root,
            int start,
            int end) {
        JsPropertyMap<@Nullable Object> sibling = null;
        for (int i = end - 1; i >= start; i--) {
            final var child = snapshot(fiber.children[i], next, root);
            child.set("sibling", sibling);
            child.set("index", i - start);
            sibling = child;
        }
        next.set("child", sibling);
    }

    private static JsPropertyMap<@Nullable Object> alternate(@Nullable JsPropertyMap<@Nullable Object> old) {
        JsPropertyMap<@Nullable Object> next = null == old ? null : Js.uncheckedCast(old.get("alternate"));
        if (null == next) {
            next = JsPropertyMap.of();
        }
        if (null != old) {
            old.set("alternate", next);
        }
        // WeakMap.get returns undefined for a new fiber. DevTools tests alternate against null.
        next.set("alternate", null == old ? null : old);
        return next;
    }

    private static JsPropertyMap<@Nullable Object> fragment(
            @Nullable JsPropertyMap<@Nullable Object> old, JsPropertyMap<@Nullable Object> parent, int mode) {
        final var record = alternate(old);
        record.set("tag", 7);
        record.set("mode", mode);
        record.set("return", parent);
        record.set("sibling", null);
        record.set("treeBaseDuration", 0);
        return record;
    }

    public void onCommitUnmount(ReconcilerHost.HostFiber info) {
        final var fiber = (Reconciler.Fiber) info;
        final var current = _fibers.get(fiber);
        if (null != current) {
            if (null != _hook) {
                try {
                    _hook.onCommitFiberUnmount(_id, current);
                } catch (Throwable error) {
                    logError(error);
                }
            }
            current.set("_unmounted", true);
            final var alternate = current.get("alternate");
            if (null != alternate) {
                Js.<JsPropertyMap<@Nullable Object>>uncheckedCast(alternate).set("_unmounted", true);
            }
            _fibers.delete(fiber);
            if (null != fiber.hostInstance) {
                _hosts.delete(fiber.hostInstance);
            }
        }
    }
}
