package react4j.dom.events;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
@SuppressWarnings("unused")
public class SyntheticEvent<E extends Event> {
    private boolean bubbles;
    private boolean cancelable;

    @SuppressWarnings("NullAway.Init")
    private Element currentTarget;

    private boolean defaultPrevented;
    private int eventPhase;
    private boolean isTrusted;

    @SuppressWarnings("NullAway.Init")
    private E nativeEvent;

    @SuppressWarnings("NullAway.Init")
    private Element target;

    private double timeStamp;

    @SuppressWarnings("NullAway.Init")
    private String type;

    public native void preventDefault();

    public native boolean isDefaultPrevented();

    public native boolean isPropagationStopped();

    public native void stopPropagation();

    public native void persist();

    @JsOverlay
    public final boolean isBubbles() {
        return bubbles;
    }

    @JsOverlay
    public final boolean isCancelable() {
        return cancelable;
    }

    @JsOverlay
    public final Element getCurrentTarget() {
        return currentTarget;
    }

    @JsOverlay
    public final int getEventPhase() {
        return eventPhase;
    }

    @JsOverlay
    public final boolean isTrusted() {
        return isTrusted;
    }

    @JsOverlay
    public final E getNativeEvent() {
        return nativeEvent;
    }

    @JsOverlay
    public final Element getTarget() {
        return target;
    }

    @JsOverlay
    public final double getTimeStamp() {
        return timeStamp;
    }

    @JsOverlay
    public final String getType() {
        return type;
    }
}
