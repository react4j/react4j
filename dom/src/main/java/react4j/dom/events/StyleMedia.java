package react4j.dom.events;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class StyleMedia {
    @SuppressWarnings("NullAway.Init")
    private String type;

    public native boolean matchMedium(String mediaquery);

    @JsOverlay
    public final String getType() {
        return type;
    }
}
