package react4j.dom;

import jsinterop.annotations.JsMethod;
import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.dom.DomHost.Node;

final class Renderer {
    private static final DomHost.WeakMap<Node, Reconciler.Root> ROOTS = new DomHost.WeakMap<>();
    private static final boolean DEVELOPMENT = "development".equals(System.getProperty("react4j.environment"));

    private Renderer() {}

    static Node requireContainer(@Nullable Object value, String operation) {
        final var node = Js.<@Nullable Node>uncheckedCast(value);
        if (null == node
                || 1 != node.nodeType
                        && 9 != node.nodeType
                        && 11 != node.nodeType
                        && !(8 == node.nodeType && " react-mount-point-unstable ".equals(node.nodeValue))) {
            throw new IllegalArgumentException(operation + "Target container is not a DOM element.");
        }
        return node;
    }

    static boolean isRoot(Object container) {
        return null != ROOTS.get(Js.uncheckedCast(container));
    }

    static Reconciler.Root createRoot(Node container) {
        return new Reconciler.Root(container, true, DomReconcilerHost.INSTANCE, BrowserScheduler.INSTANCE);
    }

    static @Nullable Object render(
            @Nullable ReactNode node, Node container, ReactDOM.@Nullable RenderCallbackFn callback) {
        if (DEVELOPMENT) {
            DomNesting.checkBodyContainer(container);
        }
        var existing = ROOTS.get(container);
        if (null == existing) {
            existing = new Reconciler.Root(container, false, DomReconcilerHost.INSTANCE, BrowserScheduler.INSTANCE);
            ROOTS.set(container, existing);
        }
        final var root = existing;
        root.submit(node, null == callback ? null : () -> invokeCallback(callback, root.publicInstance()));
        return root.publicInstance();
    }

    static boolean unmount(Node container) {
        final var root = ROOTS.get(container);
        if (null == root) {
            return false;
        }
        root.remove(() -> ROOTS.delete(container));
        return true;
    }

    @JsMethod
    private static native void invokeCallback(ReactDOM.RenderCallbackFn callback, @Nullable Object instance);
}
