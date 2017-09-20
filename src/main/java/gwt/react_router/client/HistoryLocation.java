package gwt.react_router.client;

import gwt.interop.utils.client.plainobjects.JsPlainObj;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class HistoryLocation extends JsPlainObj {

    /**
     * The pathname portion of the URL, without query string
     */
    String pathname;

    /**
     * The query string portion of the URL, including the ?
     */
    String search;

    /**
     * An object of data tied to this location
     */
    JsPlainObj state;

    /**
     * One of PUSH, REPLACE, or POP
     */
    String action;

    /**
     * A unique identifier for this location
     */
    String key;

    @JsMethod(namespace = "history.location")
    public static native HistoryLocation createLocation();
}
