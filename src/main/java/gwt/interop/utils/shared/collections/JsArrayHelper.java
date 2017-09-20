package gwt.interop.utils.shared.collections;

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

  /**
   * Return the Array value at the supplied index
   *
   * @param a   The array
   * @param i   The index
   * @param <T> The type of value
   * @return The value
   */
  public static native <T> T getArrayValue( Array<T> a, int i ) /*-{
    return a[i];
  }-*/;
}
