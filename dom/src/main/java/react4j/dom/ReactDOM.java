package react4j.dom;

import arez.Arez;
import arez.ArezContext;
import jsinterop.annotations.JsFunction;
import org.jspecify.annotations.Nullable;
import react4j.React;
import react4j.ReactElement;
import react4j.Renderable;

/** Entry points for rendering React4j nodes into the DOM. */
public final class ReactDOM {
    private ReactDOM() {}

    /**
     * Interface for performing an action inside batch.
     */
    @FunctionalInterface
    @JsFunction
    public interface BatchedUpdatesFn {
        /**
         * Perform action while batching react changes.
         *
         * @throws Throwable if an error occurred.
         */
        void call() throws Throwable;
    }

    /**
     * Interface for performing an action on render complete.
     */
    @FunctionalInterface
    @JsFunction
    public interface RenderCallbackFn {
        /**
         * Perform action on render complete.
         */
        void call();
    }

    public static ReactRoot createRoot(final Object container) {
        return Renderer.createRoot(Renderer.requireContainer(container));
    }

    /**
     * Portals provide a first-class way to render children into a DOM node that exists outside the DOM hierarchy
     * of the parent component.
     *
     * <p>Even though a portal can be anywhere in the DOM tree, it behaves like a normal React child in every
     * other way. Features like context work exactly the same regardless of whether the child is a portal, as
     * the portal still exists in the React tree regardless of position in the DOM tree.</p>
     *
     * <p>This includes event bubbling. An event fired from inside a portal will propagate to ancestors in
     * the containing React tree, even if those elements are not ancestors in the DOM tree.</p>
     *
     * @param children  the react node to render.
     * @param container the DOM element to render into.
     * @return the new portal.
     */
    public static ReactPortal createPortal(final Renderable children, final Object container) {
        return ReactPortal.create(Renderable.toReactNode(children), Renderer.requireContainer(container));
    }

    /**
     * Render a React element into the DOM in the supplied container.
     *
     * <p>If the React element was previously rendered into container, this will perform an update on it and only
     * mutate the DOM as necessary to reflect the latest React element.</p>
     *
     * <p>If the optional callback is provided, it will be executed after the component is rendered or updated.</p>
     *
     * @param node      the react node to render.
     * @param container the DOM element to render into.
     * @param onUpdate  the callback invoked when rendering is complete.
     * @return a reference to the created React Component, DOM Node, Portal or null (stateless components).
     */
    @Nullable
    public static Object render(
            @Nullable final Renderable node, final Object container, @Nullable final RenderCallbackFn onUpdate) {
        final var actual = Renderable.toReactNode(node);
        return Renderer.render(
                React.shouldCheckInvariants() ? ReactElement.createStrictMode(actual) : actual,
                Renderer.requireContainer(container),
                onUpdate);
    }

    /**
     * Render a React element into the DOM in the supplied container.
     *
     * <p>If the React element was previously rendered into container, this will perform an update on it and only
     * mutate the DOM as necessary to reflect the latest React element.</p>
     *
     * @param node      the react node to render.
     * @param container the DOM element to render into.
     * @return a reference to the created React Component, DOM Node, Portal or null (stateless components).
     * @see #render(Renderable, Object, RenderCallbackFn)
     */
    @Nullable
    public static Object render(@Nullable Renderable node, Object container) {
        return render(node, container, null);
    }

    /**
     * Remove a mounted React component from the DOM and clean up its event handlers and state. If
     * no component was mounted in the container, calling this function does nothing.
     *
     * @param container the DOM container containing the react component to unmount
     * @return true if a component was unmounted and false if there was no component to unmount.
     */
    public static boolean unmountComponentAtNode(Object container) {
        return Renderer.unmount(Renderer.requireContainer(container));
    }

    /**
     * Batch all state updates within the action.
     *
     * @param action the action where all state updates are batched.
     */
    public static void batchedUpdates(final BatchedUpdatesFn action) {
        Renderer.batchedUpdates(action);
    }

    /**
     * Register an task interceptor on the current Arez context that ensures any view updates are batched.
     */
    public static void registerBatchedArezTaskInterceptor() {
        registerBatchedArezTaskInterceptor(Arez.context());
    }

    /**
     * Register an task interceptor that ensures any view updates are batched.
     *
     * @param context the context to add interceptor to.
     */
    public static void registerBatchedArezTaskInterceptor(final ArezContext context) {
        context.setTaskInterceptor(new BatchingTaskInterceptor());
    }
}
