package react4j.drumloop.browser.audio;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "AudioNode")
public class AudioNode {
    protected AudioNode() {}

    public native AudioNode connect(AudioNode destinationNode);

    public native void disconnect();
}
