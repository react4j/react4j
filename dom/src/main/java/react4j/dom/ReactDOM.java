package react4j.dom;

import elemental2.dom.Element;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import react4j.core.Procedure;
import react4j.core.ReactNode;
import react4j.dom.proptypes.html.HtmlGlobalFields;

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
  public static native ReactPortal createPortal( ReactNode children, Element container );

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
  public static native Object render( @Nonnull ReactNode node,
                                      @Nonnull Element container,
                                      @Nullable Procedure onUpdate );

  /**
   * Render a React element into the DOM in the supplied container.
   *
   * <p>If the React element was previously rendered into container, this will perform an update on it and only
   * mutate the DOM as necessary to reflect the latest React element.</p>
   *
   * @param node      the react node to render.
   * @param container the DOM element to render into.
   * @return a reference to the created React Component, DOM Node, Portal or null (stateless components).
   * @see #render(ReactNode, Element, Procedure)
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
   * Create and return a new ReactElement of the given type.
   *
   * @param <P>  The type of the props. It should match the associated tag name.
   * @param type A HTML tag name (eg. 'div', 'span', etc)
   * @return the created DOMElement
   */
  @Nonnull
  @JsOverlay
  public static <P extends HtmlGlobalFields> DOMElement<P> createElement( @Nonnull final String type )
  {
    return createElement( type, null );
  }

  /**
   * Create and return a new ReactElement of the given type.
   *
   * @param <P>   The type of the props. It should match the associated tag name.
   * @param type  A HTML tag name (eg. 'div', 'span', etc)
   * @param props The props to pass to the element.
   * @return the created DOMElement
   */
  @Nonnull
  @JsMethod( namespace = "React" )
  public static native <P extends HtmlGlobalFields>
  DOMElement<P> createElement( @Nonnull String type, @Nullable P props );

  /**
   * Create and return a new ReactElement of the given type with specified children.
   *
   * @param <P>      The type of the props. It should match the associated tag name.
   * @param type     A HTML tag name (eg. 'div', 'span', etc)
   * @param props    The props to pass to the element.
   * @param children The child elements.
   * @return the created DOMElement
   */
  @Nonnull
  @JsMethod( namespace = "React" )
  public static native <P extends HtmlGlobalFields>
  DOMElement<P> createElement( @Nonnull String type, @Nullable P props, @Nullable ReactNode... children );
}
