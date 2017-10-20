package react4j.core;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * The react native component.
 * This class is not part of the public API and is purely an implementation detail.
 *
 * @param <P> the type of props this component expects
 * @param <S> the type of state this component maintains
 */
@JsType( isNative = true, namespace = "React", name = "Component" )
abstract class NativeComponent<P extends BaseProps, S extends BaseState>
{
  @SuppressWarnings( "unused" )
  @JsProperty
  private P props;
  @JsProperty
  private S state;
  @SuppressWarnings( "unused" )
  @JsProperty
  private JsPropertyMap<Object> refs;

  NativeComponent( @SuppressWarnings( "unused" ) @Nonnull final P props )
  {
  }

  @JsMethod
  @Nullable
  protected abstract ReactNode render();

  @JsOverlay
  @Nullable
  final P props()
  {
    return props;
  }

  @JsOverlay
  @Nullable
  final S state()
  {
    return state;
  }

  final native void setState( @Nonnull S state );

  final native void setState( @Nonnull Component.SetStateCallback<P, S> callback );

  @JsOverlay
  final void setInitialState( @Nonnull final S state )
  {
    this.state = Objects.requireNonNull( state );
  }

  final native void forceUpdate();

  @JsOverlay
  final JsPropertyMap<Object> refs()
  {
    return refs;
  }
}
