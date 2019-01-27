package react4j;

import elemental2.core.JsArray;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Native interface to native runtime for creating component.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "React" )
public final class React
{
  private React()
  {
  }

  /**
   * Return true if components should have human readable names specified.
   * Useful if you want to interact via DevTools or other tool chains.
   *
   * @return true to enable human readable names for components.
   */
  @JsOverlay
  public static boolean enableComponentNames()
  {
    return ReactConfig.enableComponentNames();
  }

  /**
   * Return true if the prop keys should be minimized.
   * This will significantly reduce the size of the compiled output but will make inspecting the props
   * in DevTools difficult if not impossible.
   *
   * @return true to minimize prop keys.
   */
  @JsOverlay
  public static boolean shouldMinimizePropKeys()
  {
    return ReactConfig.shouldMinimizePropKeys();
  }

  /**
   * Return true if the prop value should be validated when initially set or when changed.
   *
   * @return true to validate prop values.
   */
  @JsOverlay
  public static boolean shouldValidatePropValues()
  {
    return ReactConfig.shouldValidatePropValues();
  }

  /**
   * Return true if react state should be used to store debug data.
   * Useful if you want to inspect the debug data via DevTools. This feature is resource intensive
   * and should not be enabled in production.
   *
   * @return true if react state should be used to store debug data.
   */
  @JsOverlay
  public static boolean shouldStoreDebugDataAsState()
  {
    return ReactConfig.shouldStoreDebugDataAsState();
  }

  /**
   * Return true if invariants will be checked.
   *
   * @return true if invariants will be checked.
   */
  @JsOverlay
  public static boolean shouldCheckInvariants()
  {
    return ReactConfig.shouldCheckInvariants();
  }

  /**
   * Return true if props should be frozen before being passed to react.
   *
   * @return true if props should be frozen before being passed to react.
   */
  @JsOverlay
  public static boolean shouldFreezeProps()
  {
    return ReactConfig.shouldFreezeProps();
  }

  /*
   * WARNING: The following symbols are all imported from react which involves manually patching the js files
   * for each react release. We do this rather than trying to redefine them as Elemental2 does not correctly
   * model Symbol and we would still have to use primitive integer abstraction for IE11 anyway.
   */
  /**
   * The Symbol type for Fragments.
   */
  @SuppressWarnings( "unused" )
  @JsProperty( name = "Fragment" )
  public static Object Fragment;
  /**
   * The Symbol type for StrictMode.
   */
  @JsProperty( name = "StrictMode" )
  public static Object StrictMode;
  /**
   * The Symbol type for ConcurrentMode.
   */
  @JsProperty( name = "unstable_ConcurrentMode" )
  public static Object ConcurrentMode;
  /**
   * The Symbol type for Suspense.
   */
  @JsProperty( name = "Suspense" )
  public static Object Suspense;
  /**
   * The Symbol type for Profiler.
   */
  @JsProperty( name = "unstable_Profiler" )
  public static Object Profiler;
  /**
   * The Symbol type for Portal.
   */
  @JsProperty( name = "Portal" )
  public static Object Portal;
  /**
   * The Symbol type for Element.
   */
  @JsProperty( name = "Element" )
  public static Object Element;
  /**
   * The Symbol type for Provider.
   */
  @JsProperty( name = "Provider" )
  public static Object Provider;
  /**
   * The Symbol type for Consumer.
   */
  @JsProperty( name = "Consumer" )
  public static Object Consumer;
  /**
   * The Symbol type for ForwardRef.
   */
  @JsProperty( name = "ForwardRef" )
  public static Object ForwardRef;
  /**
   * The Symbol type for Memo.
   */
  @SuppressWarnings( "unused" )
  @JsProperty( name = "Memo" )
  public static Object Memo;
  /**
   * The Symbol type for Lazy.
   */
  @SuppressWarnings( "unused" )
  @JsProperty( name = "Lazy" )
  public static Object Lazy;

  /**
   * Create and return a new ReactElement of the given type with specified children.
   *
   * @param type  A HTML tag name (eg. 'div', 'span', etc)
   * @param props The props to pass to the element.
   * @return the created ReactElement
   */
  @JsOverlay
  @Nonnull
  public static ReactElement createElement( @Nonnull final String type, @Nullable final Object props )
  {
    return _createElement( type, Js.asPropertyMap( props ), null );
  }

  /**
   * Create and return a new ReactElement of the given type with no children.
   *
   * @param type  A HTML tag name (eg. 'div', 'span', etc)
   * @param props The props to pass to the element.
   * @return the created ReactElement
   */
  @JsOverlay
  @Nonnull
  public static ReactElement createElement( @Nonnull final String type, @Nullable final JsPropertyMap<Object> props )
  {
    return _createElement( type, props, null );
  }

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
                                            @Nullable final Object props,
                                            @Nullable final ReactNode... children )
  {
    return createElement( type, Js.asPropertyMap( props ), children );
  }

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
                                            @Nullable final ReactNode... children )
  {
    return _createElement( type, props, children );
  }

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
    return _createElement( type, props, Js.cast( children ) );
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
    return ReactElement.createRawElement( StrictMode,
                                          null,
                                          null,
                                          JsPropertyMap.of( PropNames.CHILDREN_PROP_NAME, children ) );
  }

  /**
   * Create a Fragment with the specified key and children.
   *
   * @param children the child nodes.
   * @return a new React.Fragment object.
   */
  @JsOverlay
  public static ReactNode createFragment( @Nullable final String key, @Nonnull final ReactNode... children )
  {
    return ReactElement.createFragment( key, children );
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
    return createFragment( null, children );
  }

  /**
   * Create a Fragment with the specified children.
   *
   * @param children the child nodes.
   * @return a new React.Fragment object.
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
   * Return the fiber for current component.
   * This can return null when not called from within the render method of a component.
   */
  @JsProperty( name = "React.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner.current", namespace = JsPackage.GLOBAL )
  @Nullable
  static native Object currentOwner();

  /**
   * Create a ReactElement of either component or host type.
   */
  @JsOverlay
  @Nonnull
  private static ReactElement _createElement( @Nonnull final String type,
                                              @Nullable final JsPropertyMap<Object> props,
                                              @Nullable final ReactNode[] children )
  {
    final JsPropertyMap<Object> actual = JsPropertyMap.of();
    String key = null;
    Object ref = null;
    if ( null != props )
    {
      key = props.has( PropNames.KEY_PROP_NAME ) ? Js.asString( props.get( PropNames.KEY_PROP_NAME ) ) : null;
      ref = props.has( PropNames.REF_PROP_NAME ) ? props.get( PropNames.REF_PROP_NAME ) : null;
      props.forEach( p -> {
        // In future we can probably remove this check when/if components are creating elements directly
        // and can thus guarantee that these keys are not part of props.
        if ( !p.equals( PropNames.KEY_PROP_NAME ) && !p.equals( PropNames.REF_PROP_NAME ) )
        {
          actual.set( p, props.get( p ) );
        }
      } );
    }
    // In a future iteration if we re-write the reconciler we should probably ensure that shape of ReactElement
    // is always consistent and is either null or always an array element.
    if ( null != children && children.length > 0 )
    {
      if ( 1 == children.length )
      {
        actual.set( PropNames.CHILDREN_PROP_NAME, children[ 0 ] );
      }
      else
      {
        actual.set( PropNames.CHILDREN_PROP_NAME, children );
      }
    }
    return ReactElement.createHostElement( type, key, ref, actual );
  }
}
