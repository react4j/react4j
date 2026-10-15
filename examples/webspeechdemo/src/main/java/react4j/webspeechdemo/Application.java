package react4j.webspeechdemo;

import static react4j.dom.DOM.*;

import arez.annotations.CascadeDispose;
import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.events.FormEvent;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.CssProps;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.dom.proptypes.html.InputProps;
import react4j.dom.proptypes.html.LabelProps;
import react4j.dom.proptypes.html.OptionProps;
import react4j.dom.proptypes.html.SelectProps;
import react4j.dom.proptypes.html.TextAreaProps;
import react4j.dom.proptypes.html.attributeTypes.ButtonType;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.webspeechdemo.browser.DOMRect;
import react4j.webspeechdemo.browser.Document;
import react4j.webspeechdemo.browser.Element;
import react4j.webspeechdemo.browser.HTMLInputElement;
import react4j.webspeechdemo.browser.HTMLSelectElement;
import react4j.webspeechdemo.browser.HTMLTextAreaElement;
import react4j.webspeechdemo.browser.Node;
import react4j.webspeechdemo.browser.Range;
import react4j.webspeechdemo.browser.WindowGlobal;
import react4j.webspeechdemo.browser.speech.SpeechSynthesisVoice;

@View(type = View.Type.TRACKING)
abstract class Application {
    private static final float DEFAULT_PITCH = 0.5F;
    private static final float DEFAULT_RATE = 0.0F;
    private static final float DEFAULT_VOLUME = 1.0F;
    private static final String DEFAULT_TEXT = "Call me Ishmael. Some years ago, never mind how long precisely, having "
            + "little or no money in my purse, and nothing particular to interest me on shore, I thought "
            + "I would sail about a little and see the watery part of the world.";

    @CascadeDispose
    final SpeechData _speechData = SpeechData.create(DEFAULT_PITCH, DEFAULT_RATE, DEFAULT_VOLUME, DEFAULT_TEXT);

    @Nullable
    private Element _textContent;

    @PostMount
    void postMount() {
        // In case speech synthesis is still running from a previous run of the application
        _speechData.stopSpeaking();
    }

    @Render
    ReactNode render() {
        final SpeechSynthesisVoice voice = _speechData.getVoice();
        return div(
                new HtmlProps().className(_speechData.isSpeaking() ? "speaking" : null),
                h1("Web Speech Synthesis Demo"),
                form(
                        div(
                                new HtmlProps().id("textarea"),
                                _speechData.isSpeaking()
                                        ? fragment(
                                                div(
                                                        new HtmlProps()
                                                                .ref(e -> _textContent = Js.uncheckedCast(e))
                                                                .id("textbeingspoken"),
                                                        _speechData.getText()),
                                                div(new HtmlProps().style(markerCssProps())))
                                        : textarea(new TextAreaProps()
                                                .value(_speechData.getText())
                                                .disabled(_speechData.isSpeaking())
                                                .onChange(e -> _speechData.setText(
                                                        Js.<HTMLTextAreaElement>uncheckedCast(e.getTarget())
                                                                .value())))),
                        div(
                                new HtmlProps().className("speecharg"),
                                label(new LabelProps().htmlFor("pitch"), "Pitch"),
                                input(new InputProps()
                                        .id("pitch")
                                        .type(InputType.range)
                                        .value(String.valueOf(_speechData.getPitch()))
                                        .min("0")
                                        .max("1")
                                        .step("0.05")
                                        .disabled(_speechData.isSpeaking())
                                        .onChange(e -> _speechData.setPitch(
                                                Float.parseFloat(Js.<HTMLInputElement>uncheckedCast(e.getTarget())
                                                        .value())))),
                                button(
                                        new BtnProps()
                                                .type(ButtonType.button)
                                                .prop("aria-label", Js.asAny("Reset pitch"))
                                                .title("Reset pitch")
                                                .disabled(_speechData.isSpeaking())
                                                .onClick(e -> _speechData.setPitch(DEFAULT_PITCH)),
                                        "\u21b6")),
                        div(
                                new HtmlProps().className("speecharg"),
                                label(new LabelProps().htmlFor("rate"), "Rate"),
                                input(new InputProps()
                                        .id("rate")
                                        .type(InputType.range)
                                        .value(String.valueOf(_speechData.getRate()))
                                        .min("-3")
                                        .max("3")
                                        .step("0.25")
                                        .disabled(_speechData.isSpeaking())
                                        .onChange(e -> _speechData.setRate(
                                                Float.parseFloat(Js.<HTMLInputElement>uncheckedCast(e.getTarget())
                                                        .value())))),
                                button(
                                        new BtnProps()
                                                .type(ButtonType.button)
                                                .prop("aria-label", Js.asAny("Reset rate"))
                                                .title("Reset rate")
                                                .disabled(_speechData.isSpeaking())
                                                .onClick(e -> _speechData.setRate(DEFAULT_RATE)),
                                        "\u21b6")),
                        div(
                                new HtmlProps().className("speecharg"),
                                label(new LabelProps().htmlFor("volume"), "Volume"),
                                input(new InputProps()
                                        .id("volume")
                                        .type(InputType.range)
                                        .value(String.valueOf(_speechData.getVolume()))
                                        .min("0")
                                        .max("1")
                                        .step("0.05")
                                        .disabled(_speechData.isSpeaking())
                                        .onChange(e -> _speechData.setVolume(
                                                Float.parseFloat(Js.<HTMLInputElement>uncheckedCast(e.getTarget())
                                                        .value())))),
                                button(
                                        new BtnProps()
                                                .type(ButtonType.button)
                                                .prop("aria-label", Js.asAny("Reset volume"))
                                                .title("Reset volume")
                                                .disabled(_speechData.isSpeaking())
                                                .onClick(e -> _speechData.setVolume(DEFAULT_VOLUME)),
                                        "\u21b6")),
                        div(
                                new HtmlProps().className("speecharg"),
                                label(new LabelProps().htmlFor("voice"), "Voice"),
                                select(
                                        new SelectProps()
                                                .onChange(this::onVoiceChange)
                                                .disabled(_speechData.isSpeaking())
                                                .value(null == voice ? "" : voice.voiceURI()),
                                        _speechData.getVoices().stream().map(this::renderVoiceOption)),
                                button(
                                        new BtnProps()
                                                .type(ButtonType.button)
                                                .prop("aria-label", Js.asAny("Reset voice"))
                                                .title("Reset voice")
                                                .disabled(_speechData.isSpeaking())
                                                .onClick(e -> _speechData.resetVoice()),
                                        "\u21b6")),
                        div(
                                new HtmlProps().className("bottom"),
                                button(
                                        new BtnProps()
                                                .type(ButtonType.button)
                                                .className("small")
                                                .prop("aria-label", Js.asAny("Speak"))
                                                .title("Speak")
                                                .disabled(_speechData.isSpeaking())
                                                .onClick(e -> _speechData.startSpeaking()),
                                        "Speak"),
                                button(
                                        new BtnProps()
                                                .type(ButtonType.button)
                                                .className("small")
                                                .prop(
                                                        "aria-label",
                                                        Js.asAny(_speechData.isPaused() ? "Resume" : "Pause"))
                                                .title(_speechData.isPaused() ? "Resume" : "Pause")
                                                .disabled(!_speechData.isSpeaking())
                                                .onClick(e -> {
                                                    if (_speechData.isPaused()) {
                                                        _speechData.resume();
                                                    } else {
                                                        _speechData.pause();
                                                    }
                                                }),
                                        _speechData.isPaused() ? "Resume" : "Pause"),
                                button(
                                        new BtnProps()
                                                .type(ButtonType.button)
                                                .className("small")
                                                .prop("aria-label", Js.asAny("Stop"))
                                                .title("Stop")
                                                .disabled(!_speechData.isSpeaking())
                                                .onClick(e -> _speechData.stopSpeaking()),
                                        "Stop"))));
    }

    private ReactNode renderVoiceOption(final SpeechSynthesisVoice voice) {
        return option(new OptionProps().value(voice.voiceURI()), voice.name() + " (" + voice.lang() + ")");
    }

    void onVoiceChange(final FormEvent e) {
        _speechData.setVoiceByVoiceURI(
                Js.<HTMLSelectElement>uncheckedCast(e.getTarget()).value());
    }

    @Nullable
    private DOMRect deriveUtteranceBounds() {
        final int length = _speechData.utteredWordLength();
        if (0 != length && null != _textContent) {
            final Document document = WindowGlobal.document();
            final Range range = document.createRange();
            final int offset = _speechData.utteredWordOffset();
            final Node node = _textContent.firstChild();
            assert null != node;
            range.setStart(node, offset);
            range.setEnd(node, offset + length);
            return range.getBoundingClientRect();
        } else {
            return null;
        }
    }

    private CssProps markerCssProps() {
        final DOMRect markerRect = deriveUtteranceBounds();
        final Element body = WindowGlobal.document().body;
        assert null != body;
        final int length = _speechData.utteredWordLength();

        // This margin comes from "margin: .5rem;" in "#textarea > *" css rule
        final int marginInPixels = 8;
        return new CssProps()
                .position("fixed")
                .border("1px solid orange")
                .display(0 == length ? "none" : "block")
                .transition(0 == length ? "all 0s ease 0s" : "all 50ms ease")
                .top(
                        null == markerRect
                                ? "0"
                                : ((markerRect.top() - 1 + body.clientHeight() - body.scrollHeight() - marginInPixels)
                                        + "px"))
                .left(
                        null == markerRect
                                ? "0"
                                : ((markerRect.left() - 1 + body.clientWidth() - body.scrollWidth() - marginInPixels)
                                        + "px"))
                .width(null == markerRect ? "0" : (markerRect.width + "px"))
                .height(null == markerRect ? "0" : (markerRect.height + "px"));
    }
}
