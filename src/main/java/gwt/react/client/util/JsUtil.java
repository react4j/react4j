package gwt.react.client.util;

import elemental2.core.JsObject;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public final class JsUtil
{
  private JsUtil()
  {
  }

  public static <O> void definePropertyValue( O o, String p, Object v )
  {
    JsObject.defineProperty( Js.cast( o ), p, Js.cast( JsPropertyMap.of( "value", v ) ) );
  }
}
