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

  @JsOverlay
  @Nonnull
  static ReactElement create( @Nonnull final Object type,
                              @Nullable final String key,
                              @Nullable final Object ref,
                              @Nonnull final JsPropertyMap<Object> props,
                              @Nullable final Object owner )
  {
    final ReactElement element = new ReactElement();
    element.typeof = React.Element;
    element.type = Objects.requireNonNull( type );
    element.key = key;
    element.ref = ref;
    element.props = Objects.requireNonNull( props );
    element._owner = owner;

    if ( ReactConfig.shouldFreezeProps() )
    {
      JsObject.freeze( element );
      JsObject.freeze( element.props );
    }
    return element;
  }
}
