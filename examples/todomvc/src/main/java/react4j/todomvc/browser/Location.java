package react4j.todomvc.browser;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Location")
public final class Location {
    private Location() {}

    @JsProperty(name = "hash")
    public native String hash();

    @JsProperty(name = "hash")
    public native void setHash(String hash);

    @JsProperty(name = "pathname")
    public native String pathname();

    @JsProperty(name = "search")
    public native String search();
}
