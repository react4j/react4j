package react4j;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

/**
 * The react native component.
 */
@JsType( isNative = true, namespace = "React", name = "Component" )
public abstract class NativeComponent
{
  @SuppressWarnings( "unused" )
  @JsProperty
  private JsPropertyMap<Object> props;
  @JsProperty
  private JsPropertyMap<Object> state;

  @JsConstructor
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
  final JsPropertyMap<Object> state()
  {
    return state;
  }

  final native void setState( @Nonnull Component.SetStateCallback callback, @Nullable Procedure onStateUpdateComplete );

  @JsOverlay
  final void setInitialState( @Nonnull final JsPropertyMap<Object> state )
  {
    this.state = Objects.requireNonNull( state );
  }

  final native void forceUpdate();
}
