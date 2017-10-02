package react.core.util;

import elemental2.core.Array;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * A collection of utilities for interoperability between Java and Javascript.
 */
public final class JsUtil
{
  private JsUtil()
  {
  }

  /**
   * Box a native JS array into a Java List. It does not have any significant performance
   * penalty because we directly set the native array of the super ArrayList
   * implementation.
   *
   * @param <T>   the type of array elements.
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
   * @param <T>   the type of array elements.
   * @param input the java list instance.
   * @return the native array.
   */
  public static native <T> Array<T> asJsArray( @Nonnull final List<T> input )/*-{
    return input.@java.util.ArrayList::array;
  }-*/;
}
