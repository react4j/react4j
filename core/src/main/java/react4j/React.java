package react4j;

import elemental2.core.JsArray;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import static org.realityforge.braincheck.Guards.*;

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
   * The Symbol type for Fragments.
   */
  @SuppressWarnings( "unused" )
  @JsProperty( name = "Fragment" )
  public static ComponentConstructorFunction Fragment;
  /**
   * The Symbol type for StrictMode.
   */
  @JsProperty( name = "StrictMode" )
  public static ComponentConstructorFunction StrictMode;
  /**
   * The Symbol type for ConcurrentMode.
   */
  @JsProperty( name = "unstable_ConcurrentMode" )
  public static ComponentConstructorFunction ConcurrentMode;
  /**
   * The Symbol type for Suspense.
   */
  @JsProperty( name = "Suspense" )
  public static ComponentConstructorFunction Suspense;
  /**
   * The Symbol type for Profiler.
   */
  @JsProperty( name = "unstable_Profiler" )
  public static ComponentConstructorFunction Profiler;
  /**
   * The Symbol type for Portal.
   */
  @JsProperty( name = "Portal" )
  public static ComponentConstructorFunction Portal;
  /**
   * The Symbol type for Element.
   */
  @JsProperty( name = "Element" )
  public static ComponentConstructorFunction Element;
  /**
   * The Symbol type for Provider.
   */
  @JsProperty( name = "Provider" )
  public static ComponentConstructorFunction Provider;
  /**
   * The Symbol type for Consumer.
   */
  @JsProperty( name = "Consumer" )
  public static ComponentConstructorFunction Consumer;
  /**
   * The Symbol type for ForwardRef.
   */
  @JsProperty( name = "ForwardRef" )
  public static ComponentConstructorFunction ForwardRef;
  /**
   * The Symbol type for Memo.
   */
  @JsProperty( name = "Memo" )
  public static ComponentConstructorFunction Memo;
  /**
   * The Symbol type for Lazy.
   */
  @JsProperty( name = "Lazy" )
  public static ComponentConstructorFunction Lazy;

  /**
   * Return true if the specified node is a ReactElement.
   *
   * @param node the node to test.
   * @return true if the specified node is a ReactElement.
   */
  public static native boolean isValidElement( @Nonnull ReactNode node );

  /**
   * Create a ReactElement for the specified React component with no props or children.
   *
   * @param type the constructor function for the native React component.
   * @return a new ReactElement.
   */
  @JsOverlay
  public static ReactNode createElement( @Nonnull final ComponentConstructorFunction type )
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
  public static native ReactNode createElement( @Nonnull final ComponentConstructorFunction type,
                                                @Nullable final JsPropertyMap<Object> props );

  /**
   * Create a ReactElement for the specified React component.
   *
   * @param type     the constructor function for the native React component.
   * @param props    the props to pass to the component.
   * @param children the children of the react component.
   * @return a new ReactElement.
   */
  public static native ReactNode createElement( @Nonnull ComponentConstructorFunction type,
                                                @Nullable JsPropertyMap<Object> props,
                                                @Nullable JsArray<ReactNode> children );

  /**
   * Create a ReactElement for the specified React component.
   *
   * @param type     the constructor function for the native React component.
   * @param props    the props to pass to the component.
   * @param children the children of the react component.
   * @return a new ReactElement.
   */
  public static native ReactNode createElement( @Nonnull ComponentConstructorFunction type,
                                                @Nullable JsPropertyMap<Object> props,
                                                @Nonnull ReactNode... children );

  /**
   * Create and return a new ReactElement of the given type.
   *
   * @param type  A HTML tag name (eg. 'div', 'span', etc)
   * @param props The props to pass to the element.
   * @return the created ReactNode
   */
  @Nonnull
  public static native ReactNode createElement( @Nonnull String type, @Nullable Object props );

  /**
   * Create and return a new ReactElement of the given type with specified children.
   *
   * @param type     A HTML tag name (eg. 'div', 'span', etc)
   * @param props    The props to pass to the element.
   * @param children The child elements.
   * @return the created DOMElement
   */
  @Nonnull
  @JsMethod
  public static native ReactNode createElement( @Nonnull String type,
                                                @Nullable Object props,
                                                @Nullable ReactNode... children );

  /**
   * Create and return a new ReactElement of the given type with specified children.
   *
   * @param type     A HTML tag name (eg. 'div', 'span', etc)
   * @param props    The props to pass to the element.
   * @param children The child elements.
   * @return the created ReactElement
   */
  @JsOverlay
  @Nonnull
  public static ReactElement createElement( @Nonnull final String type,
                                            @Nullable final JsPropertyMap<Object> props,
                                            @Nullable final JsArray<ReactNode> children )
  {
    return createElement( type, props, (Object) children );
  }

  /**
   * Create a StrictMode component with the specified children.
   *
   * @param children the child nodes.
   * @return a new React.Fragment object.
   */
  @JsOverlay
  public static ReactNode createStrictMode( @Nonnull final ReactNode... children )
  {
    return createElement( StrictMode, null, children );
  }

  /**
   * Create a Fragment with the specified children.
   *
   * @param children the child nodes.
   * @return a new React.Fragment object.
   */
  @JsOverlay
  public static ReactNode createFragment( @Nonnull final ReactNode... children )
  {
    return createElement( Fragment, null, children );
  }

  /**
   * Create a Fragment with the specified children.
   *
   * @param children the child nodes.
   * @return a new React.Fragment object.
   * @see #createElement(ComponentConstructorFunction, JsPropertyMap, ReactNode...)
   */
  @JsOverlay
  public static ReactNode createFragment( @Nonnull final List<? extends ReactNode> children )
  {
    return createFragment( children.stream() );
  }

  /**
   * Create a Fragment with the specified children.
   *
   * @param children the child nodes.
   * @return a new React.Fragment object.
   * @see #createElement(ComponentConstructorFunction, JsPropertyMap, ReactNode...)
   */
  @JsOverlay
  public static ReactNode createFragment( @Nonnull final Stream<? extends ReactNode> children )
  {
    /*
     * The GWT compiler does not handle method refs in this context. Not sure why
     */
    @SuppressWarnings( "Convert2MethodRef" )
    final IntFunction<ReactNode[]> intFunction = v -> new ReactNode[ v ];
    return createFragment( children.toArray( intFunction ) );
  }

  /**
   * Creates a context with specified default value.
   *
   * @param <T>          the type of the context.
   * @param defaultValue the default value.
   * @return the context.
   */
  @Nonnull
  public static native <T> Context<T> createContext( T defaultValue );

  /**
   * Clone and return a new ReactElement using element as the starting point. The resulting
   * element will have the original element's props with the new props merged in shallowly.
   * New children will replace existing children. Unlike React.addons.cloneWithProps, key and
   * ref from the original element will be preserved. There is no special behavior for merging
   * any props (unlike cloneWithProps).
   *
   * @param element the element to clone
   * @param props   the props to merge
   * @return the cloned element
   */
  @JsOverlay
  public static ReactNode cloneElement( @Nonnull final ReactNode element, @Nullable JsPropertyMap<Object> props )
  {
    if ( ReactConfig.shouldCheckInvariants() )
    {
      invariant( () -> isValidElement( element ), () -> "React.cloneElement() passed a non ReactElement" );
    }
    return cloneElement0( element, props );
  }

  /**
   * Internal method that delegates to reacts cloneElement method.
   *
   * @param element the element to clone
   * @param props   the props to merge
   * @return the cloned element
   */
  @JsMethod( name = "cloneElement" )
  private static native ReactNode cloneElement0( @Nonnull ReactNode element, @Nullable JsPropertyMap<Object> props );
}
