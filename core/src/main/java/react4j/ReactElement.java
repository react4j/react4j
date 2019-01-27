package react4j;

import elemental2.core.JsObject;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import react4j.internal.ComponentConstructorFunction;
import static org.realityforge.braincheck.Guards.*;

/**
 * Element represents either a component or a host component.
 */
@SuppressWarnings( "unused" )
@JsType( isNative = true, name = "Object", namespace = JsPackage.GLOBAL )
public class ReactElement
  implements ReactNode
{
  @JsProperty( name = "$$typeof" )
  private Object typeof;
  @Nonnull
  private Object type;
  @Nullable
  private String key;
  @Nullable
  private Object ref;
  @Nonnull
  private JsPropertyMap<Object> props;
  // The component responsible for creating this element.
  // can be null if create happens outside of a render method (i.e. at the top level).
  @Nullable
  private Object _owner;

  /**
   * Complete the element.
   * If {@link React#shouldFreezeProps()} returns true this method will freeze the props and the
   * element, otherwise this method is a no-op. This method should be called before returning the element
   * to the react runtime.
   */
  @JsOverlay
  public final void complete()
  {
    if ( React.shouldFreezeProps() )
    {
      JsObject.freeze( this );
      JsObject.freeze( props );
    }
  }

  @JsOverlay
  @Nonnull
  public static ReactElement createComponentElement( @Nonnull final ComponentConstructorFunction type )
  {
    final ReactElement element = create( type );
    element.props = JsPropertyMap.of();
    element.key = null;
    element.ref = null;
    return element;
  }

  @JsOverlay
  @Nonnull
  static ReactElement createContextElement( @Nonnull final Object type )
  {
    final ReactElement element = create( type );
    element.props = JsPropertyMap.of();
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
  public static ReactElement createRawElement( @Nonnull final Object type,
                                               @Nullable final String key,
                                               @Nullable final Object ref,
                                               @Nonnull final JsPropertyMap<Object> props )
  {
    final ReactElement element = create( type );
    element.key = key;
    element.ref = ref;
    element.props = Objects.requireNonNull( props );

    element.complete();
    return element;
  }

  @JsOverlay
  @Nonnull
  public static ReactElement createFragment( @Nonnull final ReactNode... children )
  {
    final ReactElement element = createRawNode( React.Element, React.Fragment );
    element.key = null;
    element.ref = null;
    element.props = JsPropertyMap.of( PropNames.CHILDREN_PROP_NAME, children );

    element.complete();
    return element;
  }

  @JsOverlay
  @Nonnull
  public static ReactElement createHostElement( @Nonnull final String type,
                                                @Nullable final String key,
                                                @Nullable final Object ref,
                                                @Nonnull final JsPropertyMap<Object> props )
  {
    return createRawElement( type, key, ref, props );
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
    if ( React.shouldCheckInvariants() && React.shouldFreezeProps() )
    {
      invariant( () -> !JsObject.isFrozen( this ),
                 () -> "Attempting to modify key of ReactElement after it has been frozen" );
    }
    this.key = key;
  }

  @JsOverlay
  @Nonnull
  public final JsPropertyMap<Object> props()
  {
    return props;
  }

  @JsOverlay
  protected final void setProps( @Nonnull final JsPropertyMap<Object> props )
  {
    this.props = props;
  }
}
