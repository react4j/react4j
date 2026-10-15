package react4j.dom.events;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
@SuppressWarnings("unused")
public class FocusEvent extends SyntheticEvent<Event> {
    private @Nullable EventTarget relatedTarget;

    @JsOverlay
    public final @Nullable EventTarget getRelatedTarget() {
        return relatedTarget;
    }
}
