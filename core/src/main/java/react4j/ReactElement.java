package react4j;

import elemental2.core.JsObject;
import java.util.Objects;
import javaemul.internal.annotations.DoNotAutobox;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import react4j.internal.ViewConstructorFunction;
import static org.realityforge.braincheck.Guards.*;

/**
 * Element represents either a view or a host component.
 */
@SuppressWarnings( "unused" )
@JsType( isNative = true, name = "Object", namespace = JsPackage.GLOBAL )
public class ReactElement
  implements ReactNode
{
  @JsProperty( name = "$$typeof" )
  private Object typeof;
  private Object type;
  private String key;
  private Object ref;
  @JsProperty( name = "props" )
  private JsPropertyMap<Object> inputs;
  // The view responsible for creating this element.
  // can be null if create happens outside of a render method (i.e. at the top level).
  @Nullable
  private Object _owner;

  /**
   * Complete the element.
   * If {@link React#shouldFreezeInputs()} returns true this method will freeze the inputs and the
   * element, otherwise this method is a no-op. This method should be called before returning the element
   * to the react runtime.
   */
  @JsOverlay
  public final void complete()
  {
    if ( React.shouldFreezeInputs() )
    {
      JsObject.freeze( this );
      JsObject.freeze( inputs );
    }
  }

  @JsOverlay
  public final ReactElement dup()
  {
    final ReactElement element = createRawNode( typeof, type );
    element.key = key;
    element.ref = ref;
    element.inputs = JsPropertyMap.of();
    // Need to use an unchecked cast here otherwise it is cast to a java object array breaks assign
    JsObject.assign( element.inputs, Js.uncheckedCast( this.inputs ) );
    return element;
  }

  @JsOverlay
  @Nonnull
  public static ReactElement createViewElement( @Nonnull final ViewConstructorFunction type )
  {
    final ReactElement element = create( type );
    element.inputs = JsPropertyMap.of();
    element.key = null;
    element.ref = null;
    return element;
  }

  @JsOverlay
  @Nonnull
  static ReactElement createContextElement( @Nonnull final Object type )
  {
    final ReactElement element = create( type );
    element.inputs = JsPropertyMap.of();
    element.key = null;
    element.ref = null;
    return element;
  }

  @JsOverlay
  @Nonnull
  private static ReactElement create( @Nonnull final Object type )
  {
    return createRawNode( React.Element, type );
  }

  @JsOverlay
  @Nonnull
  private static ReactElement createRawNode( @Nonnull final Object typeof, @Nonnull final Object type )
  {
    final ReactElement element = new ReactElement();
    element.typeof = Objects.requireNonNull( typeof );
    element.type = Objects.requireNonNull( type );
    element._owner = React.currentOwner();
    return element;
  }

  @JsOverlay
  @Nonnull
  private static ReactElement createRawElement( @Nonnull final Object type,
                                                @Nullable final String key,
                                                @Nullable final Object ref,
                                                @Nonnull final JsPropertyMap<Object> inputs )
  {
    final ReactElement element = create( type );
    element.key = key;
    element.ref = ref;
    element.inputs = Objects.requireNonNull( inputs );

    element.complete();
    return element;
  }

  @JsOverlay
  @Nonnull
  public static ReactElement createFragment( @Nullable final String key, @Nonnull final ReactNode... children )
  {
    final ReactElement element = createRawNode( React.Element, React.Fragment );
    element.key = key;
    element.ref = null;
    element.inputs = JsPropertyMap.of( "children", Objects.requireNonNull( children ) );

    element.complete();
    return element;
  }

  /**
   * Create a StrictMode component with the specified children.
   *
   * @param children the child nodes.
   * @return a new React.StrictMode component.
   */
  @JsOverlay
  @Nonnull
  public static ReactNode createStrictMode( @Nonnull final ReactNode... children )
  {
    return ReactElement.createRawElement( React.StrictMode,
                                          null,
                                          null,
                                          JsPropertyMap.of( "children", Objects.requireNonNull( children ) ) );
  }

  @JsOverlay
  @Nonnull
  public static ReactElement createSuspense( @Nullable final String key,
                                             @Nullable final ReactNode fallback,
                                             final int maxTimeToFallback,
                                             @Nonnull final ReactNode... children )
  {
    final ReactElement element = createRawNode( React.Element, React.Suspense );
    element.key = key;
    element.ref = null;
    element.inputs = JsPropertyMap.of( "children", Objects.requireNonNull( children ),
                                       "fallback", fallback,
                                       "ms", maxTimeToFallback );

    element.complete();
    return element;
  }

  @JsOverlay
  @Nonnull
  public static ReactElement createHostElement( @Nonnull final String type,
                                                @Nullable final String key,
                                                @Nullable final Object ref,
                                                @Nonnull final JsPropertyMap<Object> inputs )
  {
    return createRawElement( type, key, ref, inputs );
  }

  @JsOverlay
  @Nullable
  public final String key()
  {
    return key;
  }

  @JsOverlay
  public final void setKey( @Nullable final String key )
  {
    if ( React.shouldCheckInvariants() && React.shouldFreezeInputs() )
    {
      invariant( () -> !JsObject.isFrozen( this ),
                 () -> "Attempting to modify key of ReactElement after it has been frozen" );
    }
    this.key = key;
  }

  @JsOverlay
  @Nonnull
  public final JsPropertyMap<Object> inputs()
  {
    return inputs;
  }

  @JsOverlay
  @Nonnull
  public final ReactElement input( @Nonnull final String key, @DoNotAutobox final Object value )
  {
    inputs.set( key, value );
    return this;
  }

  @JsOverlay
  protected final void setInputs( @Nonnull final JsPropertyMap<Object> inputs )
  {
    this.inputs = inputs;
  }
}
