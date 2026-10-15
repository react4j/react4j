package react4j.webspeechdemo.browser.speech;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.JsArrayLike;
import react4j.webspeechdemo.browser.EventHandler;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "SpeechSynthesis")
public final class SpeechSynthesis {
    private SpeechSynthesis() {}

    @JsProperty(name = "speaking")
    public native boolean speaking();

    @JsProperty(name = "paused")
    public native boolean paused();

    public native JsArrayLike<SpeechSynthesisVoice> getVoices();

    public native void addEventListener(String type, EventHandler listener);

    public native void removeEventListener(String type, EventHandler listener);

    public native void speak(SpeechSynthesisUtterance utterance);

    public native void cancel();

    public native void pause();

    public native void resume();
}
