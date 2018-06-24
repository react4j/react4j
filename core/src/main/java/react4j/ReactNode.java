package react4j;

import elemental2.core.JsArray;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

/**
 * Union type representing possible output of render method.
 */
@SuppressWarnings( "Contract" )
@JsType( isNative = true, name = "?", namespace = JsPackage.GLOBAL )
public interface ReactNode
{
  @JsOverlay
  static ReactNode of( final byte value )
  {
    return Js.uncheckedCast( value );
  }

  @JsOverlay
  static ReactNode of( final short value )
  {
    return Js.uncheckedCast( value );
  }

  @JsOverlay
  static ReactNode of( final int value )
  {
    return Js.uncheckedCast( value );
  }

  @JsOverlay
  static ReactNode of( final long value )
  {
    return Js.uncheckedCast( value );
  }

  @JsOverlay
  static ReactNode of( final float value )
  {
    return Js.uncheckedCast( value );
  }

  @JsOverlay
  static ReactNode of( final double value )
  {
    return Js.uncheckedCast( value );
  }

  @JsOverlay
  static ReactNode of( final String string )
  {
    return Js.uncheckedCast( string );
  }

  @JsOverlay
  static ReactNode of( final JsArray<ReactNode> elements )
  {
    return Js.uncheckedCast( elements );
  }

  @JsOverlay
  static ReactNode of( final ReactNode... elements )
  {
    return Js.uncheckedCast( elements );
  }
}
