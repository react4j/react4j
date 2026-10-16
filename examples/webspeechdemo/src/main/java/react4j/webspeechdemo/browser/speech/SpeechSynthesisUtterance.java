package react4j.webspeechdemo.browser.speech;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jspecify.annotations.Nullable;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "SpeechSynthesisUtterance")
public final class SpeechSynthesisUtterance {
    @Nullable
    public SpeechSynthesisVoice voice;

    public float volume;
    public float pitch;
    public float rate;

    @Nullable
    public EventHandler onstart;

    @Nullable
    public EventHandler onend;

    @Nullable
    public EventHandler onerror;

    @Nullable
    public EventHandler onboundary;

    @Nullable
    public EventHandler onpause;

    @Nullable
    public EventHandler onresume;

    public SpeechSynthesisUtterance(final String text) {}
}
