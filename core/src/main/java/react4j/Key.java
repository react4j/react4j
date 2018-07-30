package react4j;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType( isNative = true, name = "?", namespace = JsPackage.GLOBAL )
public interface Key
{
  @JsOverlay
  static Key of( final int value )
  {
    return Js.asAny( value ).cast();
  }

  @JsOverlay
  static Key of( @Nonnull final String value )
  {
    return Js.asAny( value ).cast();
  }
}
