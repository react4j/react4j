package react4j.internal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;
import react4j.ReactNode;

/**
 * The react native component.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "React.Component" )
@SuppressWarnings( "unused" )
public abstract class NativeComponent
{
  @JsProperty
  private JsPropertyMap<Object> props;
  @JsProperty
  private JsPropertyMap<Object> state;

  @JsConstructor
  protected NativeComponent( @Nullable final JsPropertyMap<Object> props )
  {
  }

  @JsMethod
  @Nullable
  protected abstract ReactNode render();

  @JsOverlay
  @Nullable
  public final JsPropertyMap<Object> props()
  {
    return props;
  }

  @JsOverlay
  @Nullable
  public final JsPropertyMap<Object> state()
  {
    return state;
  }

  @JsOverlay
  public final void setState( @Nonnull final JsPropertyMap<Object> state )
  {
    _setState( state );
  }

  @JsMethod( name = "setState" )
  protected final native void _setState( @Nonnull JsPropertyMap<Object> state );

  @JsOverlay
  public final void forceUpdate()
  {
    _forceUpdate();
  }

  @JsMethod( name = "forceUpdate" )
  protected final native void _forceUpdate();
}
