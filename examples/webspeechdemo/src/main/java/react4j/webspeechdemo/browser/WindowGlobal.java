package react4j.webspeechdemo.browser;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import react4j.webspeechdemo.browser.speech.SpeechSynthesis;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "window")
public final class WindowGlobal {
    private WindowGlobal() {}

    @JsProperty(name = "document")
    public static native Document document();

    @JsProperty(name = "speechSynthesis")
    public static native SpeechSynthesis speechSynthesis();

    @JsOverlay
    public static boolean isSpeechSynthesisSupported() {
        return Js.global().has("speechSynthesis");
    }
}
