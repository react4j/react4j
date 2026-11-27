package react4j.dom;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
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
            if (DEVELOPMENT && hasManagedChild(container)) {
                warning("Warning: render(...): Replacing React-rendered children with a new root component. If you"
                        + " intended to update the children of this node, you should instead have the existing"
                        + " children update their state and render the new components instead of calling"
                        + " ReactDOM.render.");
            }
            existing = new Reconciler.Root(container, false, DomReconcilerHost.INSTANCE, BrowserScheduler.INSTANCE);
            ROOTS.set(container, existing);
            markRoot(container, () -> unmount(container));
        } else if (DEVELOPMENT && 8 != container.nodeType) {
            final var host = firstHost(existing.fiber);
            if (null != host && host.parentNode != container) {
                warning("Warning: render(...): It looks like the React-rendered content of this container was removed "
                        + "without using React. This is not supported and will cause errors. Instead, call "
                        + "ReactDOM.unmountComponentAtNode to empty a container.");
            }
        }
        final var root = existing;
        root.submit(node, null == callback ? null : () -> invokeCallback(callback, root.publicInstance()));
        return root.publicInstance();
    }

    static boolean unmount(Node container) {
        final var root = ROOTS.get(container);
        if (null == root) {
            final var foreignUnmount = rootUnmount(container);
            if (null != foreignUnmount) {
                if (DEVELOPMENT && null != container.firstChild) {
                    warning("Warning: unmountComponentAtNode(): The node you're attempting to unmount "
                            + "was rendered by another copy of React.");
                }
                return foreignUnmount.call();
            }
            if (DEVELOPMENT && hasManagedChild(container)) {
                final var parent = container.parentNode;
                final String advice = 1 == container.nodeType && null != parent && isRoot(parent)
                        ? "You may have accidentally passed in a React root node instead of its container."
                        : "Instead, have the parent component update its state and rerender in order to remove this"
                                + " component.";
                warning("Warning: unmountComponentAtNode(): The node you're attempting to unmount was rendered by "
                        + "React and is not a top-level container. " + advice);
            }
            return false;
        }
        root.remove(() -> {
            ROOTS.delete(container);
            unmarkRoot(container);
        });
        return true;
    }

    @JsFunction
    private interface Unmount {
        boolean call();
    }

    @JsMethod
    private static native void markRoot(Node container, Unmount unmount);

    @JsMethod
    private static native @Nullable Unmount rootUnmount(Node container);

    @JsMethod
    private static native void unmarkRoot(Node container);

    private static boolean hasManagedChild(Node container) {
        final var first = container.firstChild;
        return null != first && 1 == first.nodeType && null != DomEvents.state(Js.uncheckedCast(first));
    }

    private static @Nullable Node firstHost(Reconciler.Fiber fiber) {
        if (Reconciler.Kind.PORTAL == fiber.kind) {
            return null;
        }
        if (null != fiber.hostInstance) {
            return Js.uncheckedCast(fiber.hostInstance);
        }
        for (final var child : fiber.children) {
            final var host = firstHost(child);
            if (null != host) {
                return host;
            }
        }
        return null;
    }

    @JsMethod(namespace = JsPackage.GLOBAL, name = "console.error")
    private static native void warning(String message);

    @JsMethod
    private static native void invokeCallback(ReactDOM.RenderCallbackFn callback, @Nullable Object instance);
}
