package react4j.drumloop.browser.audio;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;
import react4j.drumloop.browser.EventHandler;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "AudioBufferSourceNode")
public final class AudioBufferSourceNode extends AudioNode {
    @Nullable
    public AudioBuffer buffer;

    public boolean loop;

    @Nullable
    public EventHandler onended;

    private AudioBufferSourceNode() {}

    public native void start();

    public native void start(double when);

    public native void stop();
}
