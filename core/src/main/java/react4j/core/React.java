package react4j.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

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
   * @param <P>  the type of the component props.
   * @param <C>  the type of the component context.
   * @param type the constructor function for the native React component.
   * @return a new ReactElement.
   */
  @JsOverlay
  public static <
    P extends BaseProps,
    C extends BaseContext
    >
  ReactElement<P, ComponentConstructorFunction<P, C>> createElement( @Nonnull final ComponentConstructorFunction<P, C> type )
  {
    return createElement( type, null );
  }

  /**
   * Create a ReactElement for the specified React component with no children.
   *
   * @param <P>   the type of the component props.
   * @param <C>   the type of the component context.
   * @param type  the constructor function for the native React component.
   * @param props the props to pass to the component.
   * @return a new ReactElement.
   */
  @JsOverlay
  public static <
    P extends BaseProps,
    C extends BaseContext
    >
  ReactElement<P, ComponentConstructorFunction<P, C>> createElement( @Nonnull final ComponentConstructorFunction<P, C> type,
                                                                     @Nullable final P props )
  {
    // Need to pass through undefined to react otherwise the debugger tool displays
    // children as null rather than omitting children
    return createElement( type, props, Js.uncheckedCast( Js.undefined() ) );
  }

  /**
   * Create a ReactElement for the specified React component.
   *
   * @param <P>   the type of the component props.
   * @param <C>   the type of the component context.
   * @param type  the constructor function for the native React component.
   * @param props the props to pass to the component.
   * @param child the child/children of the react component.
   * @return a new ReactElement.
   */
  public static native <
    P extends BaseProps,
    C extends BaseContext
    >
  ReactElement<P, ComponentConstructorFunction<P, C>> createElement( @Nonnull ComponentConstructorFunction<P, C> type,
                                                                     @Nullable P props,
                                                                     @Nullable ReactNode child );

  /**
   * Clone and return a new ReactElement using element as the starting point. The resulting
   * element will have the original element's props with the new props merged in shallowly.
   * New children will replace existing children. Unlike React.addons.cloneWithProps, key and
   * ref from the original element will be preserved. There is no special behavior for merging
   * any props (unlike cloneWithProps).
   *
   * @param <P>     the type of the component props.
   * @param <T>     the type of the component.
   * @param element the element to clone
   * @param props   the props to merge
   * @return the cloned element
   */
  public static native <P extends BaseProps, T>
  ReactElement<P, T> cloneElement( @Nonnull ReactElement<P, T> element, @Nullable P props );
}
