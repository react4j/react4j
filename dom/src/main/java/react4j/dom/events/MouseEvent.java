package react4j.dom.events;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
@SuppressWarnings("unused")
public class MouseEvent extends SyntheticEvent<Event> {
    private boolean altKey;
    private int button;
    private int buttons;
    private int clientX;
    private int clientY;
    private boolean ctrlKey;
    private boolean metaKey;
    private int pageX;
    private int pageY;
    private @Nullable EventTarget relatedTarget;
    private int screenX;
    private int screenY;
    private boolean shiftKey;

    public native boolean getModifierState(String key);

    @JsOverlay
    public final boolean isAltKey() {
        return altKey;
    }

    @JsOverlay
    public final int getButton() {
        return button;
    }

    @JsOverlay
    public final int getButtons() {
        return buttons;
    }

    @JsOverlay
    public final int getClientX() {
        return clientX;
    }

    @JsOverlay
    public final int getClientY() {
        return clientY;
    }

    @JsOverlay
    public final boolean isCtrlKey() {
        return ctrlKey;
    }

    @JsOverlay
    public final boolean isMetaKey() {
        return metaKey;
    }

    @JsOverlay
    public final int getPageX() {
        return pageX;
    }

    @JsOverlay
    public final int getPageY() {
        return pageY;
    }

    @JsOverlay
    public final @Nullable EventTarget getRelatedTarget() {
        return relatedTarget;
    }

    @JsOverlay
    public final int getScreenX() {
        return screenX;
    }

    @JsOverlay
    public final int getScreenY() {
        return screenY;
    }

    @JsOverlay
    public final boolean isShiftKey() {
        return shiftKey;
    }
}
