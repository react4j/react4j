package react4j.core;

import elemental2.core.JsArray;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Native interface to native runtime for creating component.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL )
public final class React
{
  private React()
  {
  }

  /**
   * Create a ReactElement for the specified React component with no props or children.
   *
   * @param type the constructor function for the native React component.
   * @return a new ReactElement.
   */
  @JsOverlay
  public static ReactElement<ComponentConstructorFunction> createElement( @Nonnull final ComponentConstructorFunction type )
  {
    return createElement( type, null );
  }

  /**
   * Create a ReactElement for the specified React component with no children.
   *
   * @param type  the constructor function for the native React component.
   * @param props the props to pass to the component.
   * @return a new ReactElement.
   */
  @JsOverlay
  public static ReactElement<ComponentConstructorFunction> createElement( @Nonnull final ComponentConstructorFunction type,
                                                                          @Nullable final JsPropertyMap<Object> props )
  {
    // Need to pass through undefined to react otherwise the debugger tool displays
    // children as null rather than omitting children
    return createElement( type, props, Js.<ReactNode>uncheckedCast( Js.undefined() ) );
  }

  /**
   * Create a ReactElement for the specified React component.
   *
   * @param type  the constructor function for the native React component.
   * @param props the props to pass to the component.
   * @param child the child of the react component.
   * @return a new ReactElement.
   */
  public static native ReactElement<ComponentConstructorFunction> createElement( @Nonnull ComponentConstructorFunction type,
                                                                                 @Nullable JsPropertyMap<Object> props,
                                                                                 @Nullable ReactNode child );

  /**
   * Create a ReactElement for the specified React component.
   *
   * @param type     the constructor function for the native React component.
   * @param props    the props to pass to the component.
   * @param children the children of the react component.
   * @return a new ReactElement.
   */
  public static native ReactElement<ComponentConstructorFunction> createElement( @Nonnull ComponentConstructorFunction type,
                                                                                 @Nullable JsPropertyMap<Object> props,
                                                                                 @Nonnull JsArray<ReactNode> children );

  /**
   * Create a ReactElement for the specified React component.
   *
   * @param type     the constructor function for the native React component.
   * @param props    the props to pass to the component.
   * @param children the children of the react component.
   * @return a new ReactElement.
   */
  public static native ReactElement<ComponentConstructorFunction> createElement( @Nonnull ComponentConstructorFunction type,
                                                                                 @Nullable JsPropertyMap<Object> props,
                                                                                 @Nonnull ReactNode[] children );

  /**
   * Clone and return a new ReactElement using element as the starting point. The resulting
   * element will have the original element's props with the new props merged in shallowly.
   * New children will replace existing children. Unlike React.addons.cloneWithProps, key and
   * ref from the original element will be preserved. There is no special behavior for merging
   * any props (unlike cloneWithProps).
   *
   * @param <T>     the type of the component.
   * @param element the element to clone
   * @param props   the props to merge
   * @return the cloned element
   */
  public static native <T>
  ReactElement<T> cloneElement( @Nonnull ReactElement<T> element, @Nullable JsPropertyMap<Object> props );
}
