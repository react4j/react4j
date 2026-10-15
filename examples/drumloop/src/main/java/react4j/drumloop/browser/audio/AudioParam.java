package react4j.drumloop.browser.audio;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "AudioParam")
public final class AudioParam {
    public float value;

    private AudioParam() {}
}
