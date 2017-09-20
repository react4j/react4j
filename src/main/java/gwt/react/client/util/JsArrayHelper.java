package gwt.react.client.util;

public class JsArrayHelper
{
  /**
   * Create a native javascript array
   *
   * @param <T> The type the array will hold
   * @return The array
   */
  public static native <T extends Object> Array<T> createArray() /*-{
    return [];
  }-*/;
}
