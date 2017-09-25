package gwt.react.client.util;

import elemental2.core.JsObject;
import elemental2.core.Array;
import java.util.List;
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

  /**
   * Box a native JS array in a Java List. It does not have any performance
   * penalty because we directly set the native array of the super ArrayList
   * implementation.
   */
  public static native <T> List<T> asList( Array<T> o )
    /*-{
      var l = @java.util.ArrayList::new()();
      l.@java.util.ArrayList::array = o;
      return l;
    }-*/;

  /**
   * UnBox the native JS array in a Java List. It does not have any performance
   * penalty because we directly take the native array of the super ArrayList
   * implementation.
   */
  public static native <T> Array<T> asJsArray( List<T> l )/*-{
    return l.@java.util.ArrayList::array;
  }-*/;
}
