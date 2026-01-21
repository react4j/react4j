package react4j;

import java.util.Objects;
import javaemul.internal.annotations.DoNotAutobox;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import org.jetbrains.annotations.Contract;
import react4j.internal.ViewConstructorFunction;

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

  @JsOverlay
  @Contract( pure = true )
  @Nonnull
  public final ReactElement dup()
  {
    final ReactElement element = createRawNode( typeof, type );
    element.key = key;
    element.ref = ref;
    element.inputs = JsPropertyMap.of();
    inputs.forEach( key -> element.inputs.set( key, inputs.get( key ) ) );
    return element;
  }

  @SuppressWarnings( "JavaExistingMethodCanBeUsed" )
  @JsOverlay
  @Nonnull
  @Contract( pure = true )
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
  @Contract( pure = true )
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
  @Contract( pure = true )
  private static ReactElement create( @Nonnull final Object type )
  {
    return createRawNode( React.Element, type );
  }

  @JsOverlay
  @Nonnull
  @Contract( pure = true )
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
  @Contract( pure = true )
  private static ReactElement createRawElement( @Nonnull final Object type,
                                                @Nullable final String key,
                                                @Nullable final Object ref,
                                                @Nonnull final JsPropertyMap<Object> inputs )
  {
    final ReactElement element = create( type );
    element.key = key;
    element.ref = ref;
    element.inputs = Objects.requireNonNull( inputs );
    return element;
  }

  @JsOverlay
  @Nonnull
  @Contract( pure = true )
  public static ReactElement createFragment( @Nullable final String key, @Nonnull final ReactNode... children )
  {
    final ReactElement element = createRawNode( React.Element, React.Fragment );
    element.key = key;
    element.ref = null;
    element.inputs = JsPropertyMap.of( "children", Objects.requireNonNull( children ) );
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
  @Contract( pure = true )
  public static ReactNode createStrictMode( @Nonnull final ReactNode... children )
  {
    return ReactElement.createRawElement( React.StrictMode,
                                          null,
                                          null,
                                          JsPropertyMap.of( "children", Objects.requireNonNull( children ) ) );
  }

  @JsOverlay
  @Nonnull
  @Contract( pure = true )
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
    return element;
  }

  @JsOverlay
  @Nonnull
  @Contract( pure = true )
  public static ReactElement createHostElement( @Nonnull final String type,
                                                @Nullable final String key,
                                                @Nullable final Object ref,
                                                @Nonnull final JsPropertyMap<Object> inputs )
  {
    return createRawElement( type, key, ref, inputs );
  }

  @JsOverlay
  @Nullable
  @Contract( pure = true )
  public final String key()
  {
    return key;
  }

  @JsOverlay
  public final void setKey( @Nullable final String key )
  {
    this.key = key;
  }

  @JsOverlay
  @Nonnull
  @Contract( pure = true )
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
