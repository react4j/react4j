package react4j.dom.events;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
@SuppressWarnings("unused")
public class CompositionEvent extends SyntheticEvent<Event> {
    @SuppressWarnings("NullAway.Init")
    private String data;

    @JsOverlay
    public final String getData() {
        return data;
    }
}
