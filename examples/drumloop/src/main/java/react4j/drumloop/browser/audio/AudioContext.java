package react4j.drumloop.browser.audio;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;
import react4j.drumloop.browser.core.ArrayBuffer;
import react4j.drumloop.browser.promise.Promise;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "AudioContext")
public final class AudioContext {
    public AudioContext() {}

    @JsProperty(name = "destination")
    public native AudioNode destination();

    @JsProperty(name = "state")
    public native String state();

    public native Promise<@Nullable Void> resume();

    public native GainNode createGain();

    public native AudioBufferSourceNode createBufferSource();

    public native Promise<AudioBuffer> decodeAudioData(ArrayBuffer audioData);
}
