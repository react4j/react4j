package react4j.webspeechdemo.browser.speech;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "SpeechSynthesisEvent")
public final class SpeechSynthesisEvent {
    private SpeechSynthesisEvent() {}

    @JsProperty(name = "type")
    public native String type();

    @JsProperty(name = "name")
    public native String name();

    @JsProperty(name = "charIndex")
    public native int charIndex();

    @JsProperty(name = "charLength")
    public native int charLength();
}
