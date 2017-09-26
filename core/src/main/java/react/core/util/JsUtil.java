package react.core.util;

import elemental2.core.Array;
import elemental2.core.JsObject;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
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
   * Box a native JS array into a Java List. It does not have any significant performance
   * penalty because we directly set the native array of the super ArrayList
   * implementation.
   *
   * @param <T> the type of array elements.
   * @param input the native array.
   * @return the java list instance.
   */
  public static native <T> ArrayList<T> asList( @Nonnull final Array<T> input )
    /*-{
      var l = @java.util.ArrayList::new()();
      l.@java.util.ArrayList::array = input;
      return l;
    }-*/;

  /**
   * UnBox the native JS array into a Java List. It does not have any significant performance
   * penalty because we directly take the native array of the super ArrayList
   * implementation.
   *
   * @param <T> the type of array elements.
   * @param input the java list instance.
   * @return the native array.
   */
  public static native <T> Array<T> asJsArray( @Nonnull final List<T> input )/*-{
    return input.@java.util.ArrayList::array;
  }-*/;
}
