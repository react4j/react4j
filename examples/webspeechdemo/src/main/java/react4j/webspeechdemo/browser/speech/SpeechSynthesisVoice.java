package react4j.webspeechdemo.browser.speech;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "SpeechSynthesisVoice")
public final class SpeechSynthesisVoice {
    private SpeechSynthesisVoice() {}

    @JsProperty(name = "default")
    public native boolean default_();

    @JsProperty(name = "lang")
    public native String lang();

    @JsProperty(name = "name")
    public native String name();

    @JsProperty(name = "voiceURI")
    public native String voiceURI();
}
