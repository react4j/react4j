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

/**
 * Element represents either a component or a host component.
 */
@SuppressWarnings( "unused" )
@JsType( isNative = true, name = "Object", namespace = JsPackage.GLOBAL )
class ReactElement
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
   * If {@link ReactConfig#shouldFreezeProps()} returns true this method will freeze the props and the
   * element, otherwise this method is a no-op. This method should be called before returning the element
   * to thereact runtime.
   */
  @JsOverlay
  private void complete()
  {
    if ( ReactConfig.shouldFreezeProps() )
    {
      JsObject.freeze( this );
      JsObject.freeze( props );
    }
  }

  @JsOverlay
  @Nonnull
  static ReactElement create( @Nonnull final Object type )
  {
    final ReactElement element = new ReactElement();
    element.typeof = React.Element;
    element.type = Objects.requireNonNull( type );
    element._owner = React.currentOwner();
    return element;
  }

  @JsOverlay
  @Nonnull
  static ReactElement create( @Nonnull final Object type,
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
}
