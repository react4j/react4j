package react4j.drumloop.browser.audio;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "GainNode")
public final class GainNode extends AudioNode {
    private GainNode() {}

    @JsProperty(name = "gain")
    public native AudioParam gain();
}
