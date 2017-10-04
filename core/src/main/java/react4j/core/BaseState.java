package react4j.core;

import elemental2.core.JsObject;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Base class for state field.
 */
@JsType( isNative = true, namespace = JsPackage.GLOBAL, name = "Object" )
public class BaseState
{
  @JsOverlay
  public final <T> T dup()
  {
    return Js.uncheckedCast( JsObject.assign( Js.uncheckedCast( JsPropertyMap.of() ),
                                              Js.uncheckedCast( JsPropertyMap.of( this ) ) ) );
  }
}
