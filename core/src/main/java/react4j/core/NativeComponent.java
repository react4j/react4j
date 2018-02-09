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
 *
 * @param <S> the type of state this component maintains
 */
@JsType( isNative = true, namespace = "React", name = "Component" )
public abstract class NativeComponent<S extends BaseState>
{
  @SuppressWarnings( "unused" )
  @JsProperty
  private JsPropertyMap<Object> props;
  @JsProperty
  private S state;

  NativeComponent( @SuppressWarnings( "unused" ) @Nullable final JsPropertyMap<Object> props )
  {
  }

  @JsMethod
  @Nullable
  public abstract ReactNode render();

  @JsOverlay
  @Nullable
  final JsPropertyMap<Object> props()
  {
    return props;
  }

  @JsOverlay
  @Nullable
  final S state()
  {
    return state;
  }

  final native void setState( @Nonnull Component.SetStateCallback<S> callback );

  @JsOverlay
  final void setInitialState( @Nonnull final S state )
  {
    this.state = Objects.requireNonNull( state );
  }

  final native void forceUpdate();
}
