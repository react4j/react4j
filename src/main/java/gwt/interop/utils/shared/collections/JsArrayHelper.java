package gwt.interop.utils.shared.collections;

import gwt.interop.utils.shared.collections.Array;

public class JsArrayHelper {

    /**
     * Create a native javascript array
     * @param <T> The type the array will hold
     * @return The array
     */
    public static native <T extends Object> Array<T> createArray() /*-{
		return [];
	}-*/;

    /**
     * Cast an Array interface as a Java array
     * @param src The Array
     * @param <E> The type the array holds
     * @return The java array
     */
    public static native <E> E[] castAsArray(Array<?> src) /*-{
		return src;
	}-*/;

    /**
     * Set the Array value at the supplied index
     *
     * @param a The array
     * @param i The index
     * @param v The value
     * @param <T> The type of value
     */
    public static native <T> void setArrayValue(Array<T> a, int i, T v) /*-{
		a[i] = v;
	}-*/;

    /**
     * Return the Array value at the supplied index
     * @param a The array
     * @param i The index
     * @param <T> The type of value
     * @return The value
     */
    public static native <T> T getArrayValue(Array<T> a, int i) /*-{
		return a[i];
	}-*/;
}
