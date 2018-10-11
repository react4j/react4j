package react4j;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsPropertyMap;

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
  NativeComponent( @Nullable final JsPropertyMap<Object> props )
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

  final native void setState( @Nonnull JsPropertyMap<Object> state );

  final native void forceUpdate();
}
