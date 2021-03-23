package react4j.dom;

import akasha.Element;
import arez.Arez;
import arez.ArezContext;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.React;
import react4j.ReactElement;
import react4j.ReactNode;

/**
 * Core interface into React DOM library.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL )
public class ReactDOM
{
  private ReactDOM()
  {
  }

  /**
   * Interface for performing an action inside batch.
   */
  @FunctionalInterface
  @JsFunction
  public interface BatchedUpdatesFn
  {
    /**
     * Perform action while batching react changes.
     *
     * @throws Throwable if an error occurred.
     */
    void call()
      throws Throwable;
  }

  /**
   * Interface for performing an action on render complete.
   */
  @FunctionalInterface
  @JsFunction
  public interface RenderCallbackFn
  {
    /**
     * Perform action on render complete.
     */
    void call();
  }

  @JsOverlay
  @Nonnull
  public static ReactRoot createRoot( @Nonnull final Element container )
  {
    return unstable_createRoot( container );
  }

  @Nonnull
  private static native ReactRoot unstable_createRoot( @Nonnull Element container );

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
  public static native ReactPortal createPortal( @Nonnull ReactNode children, @Nonnull Element container );

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
  @JsOverlay
  public static Object render( @Nonnull final ReactNode node,
                               @Nonnull final Element container,
                               @Nullable final RenderCallbackFn onUpdate )
  {
    return _render( React.shouldCheckInvariants() ? ReactElement.createStrictMode( node ) : node, container, onUpdate );
  }

  @Nullable
  @JsMethod( name = "render" )
  private static native Object _render( @Nonnull ReactNode node,
                                        @Nonnull Element container,
                                        @Nullable RenderCallbackFn onUpdate );

  /**
   * Render a React element into the DOM in the supplied container.
   *
   * <p>If the React element was previously rendered into container, this will perform an update on it and only
   * mutate the DOM as necessary to reflect the latest React element.</p>
   *
   * @param node      the react node to render.
   * @param container the DOM element to render into.
   * @return a reference to the created React Component, DOM Node, Portal or null (stateless components).
   * @see #render(ReactNode, Element, RenderCallbackFn)
   */
  @Nullable
  @JsOverlay
  public static Object render( @Nonnull ReactNode node, @Nonnull Element container )
  {
    return render( node, container, null );
  }

  /**
   * Remove a mounted React component from the DOM and clean up its event handlers and state. If
   * no component was mounted in the container, calling this function does nothing.
   *
   * @param container the DOM container containing the react component to unmount
   * @return true if a component was unmounted and false if there was no component to unmount.
   */
  public static native boolean unmountComponentAtNode( @Nonnull Element container );

  /**
   * Batch all state updates within the action.
   * This is currently an unstable API within the React 16, mostly because it is only useful when called
   * outside an event handler (i.e. from network code) and because it is likely to be enabled by default
   * in a later version of React.
   *
   * @param action the action where all state updates are batched.
   */
  @JsOverlay
  public static void batchedUpdates( @Nonnull final BatchedUpdatesFn action )
  {
    unstable_batchedUpdates( action );
  }

  /**
   * Register an task interceptor on the current Arez context that ensures any view updates are batched.
   */
  @JsOverlay
  public static void registerBatchedArezTaskInterceptor()
  {
    registerBatchedArezTaskInterceptor( Arez.context() );
  }

  /**
   * Register an task interceptor that ensures any view updates are batched.
   *
   * @param context the context to add interceptor to.
   */
  @JsOverlay
  public static void registerBatchedArezTaskInterceptor( @Nonnull final ArezContext context )
  {
    context.setTaskInterceptor( new BatchingTaskInterceptor() );
  }

  /**
   * The native method with the unstable prefix.
   *
   * @param action the action where all state updates are batched.
   */
  @JsMethod
  private static native void unstable_batchedUpdates( @Nonnull BatchedUpdatesFn action );
}
