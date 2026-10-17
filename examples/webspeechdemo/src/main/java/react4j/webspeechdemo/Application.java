package react4j.webspeechdemo;

import static react4j.dom.DOM.*;

import arez.annotations.CascadeDispose;
import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
import react4j.dom.events.FormEvent;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.CssProps;
import react4j.dom.proptypes.html.FormProps;
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
        var editorStyle = Application_Styled_EditorStyle.get();
        var textStyle = Application_Styled_TextStyle.get();
        var markerStyle = Application_Styled_MarkerStyle.get();
        var argumentStyle = Application_Styled_ArgumentStyle.get();
        var labelStyle = Application_Styled_LabelStyle.get();
        var rangeStyle = Application_Styled_RangeStyle.get();
        var resetStyle = Application_Styled_ResetStyle.get();
        var actionStyle = Application_Styled_ActionStyle.get();
        final SpeechSynthesisVoice voice = _speechData.getVoice();
        return div(
                h1(new HtmlProps().className(Application_Styled_TitleStyle.get().style()), "Web Speech Synthesis Demo"),
                form(
                        new FormProps()
                                .className(Application_Styled_FormStyle.get().style()),
                        div(
                                new HtmlProps()
                                        .id("textarea")
                                        .className(
                                                _speechData.isSpeaking()
                                                        ? editorStyle.speaking()
                                                        : editorStyle.style()),
                                _speechData.isSpeaking()
                                        ? fragment(
                                                div(
                                                        new HtmlProps()
                                                                .ref(e -> _textContent = Js.uncheckedCast(e))
                                                                .id("textbeingspoken")
                                                                .className(textStyle.spoken()),
                                                        _speechData.getText()),
                                                div(new HtmlProps()
                                                        .className(
                                                                0 == _speechData.utteredWordLength()
                                                                        ? markerStyle.style()
                                                                        : markerStyle.visible())
                                                        .style(markerCssProps())))
                                        : textarea(new TextAreaProps()
                                                .className(textStyle.style())
                                                .value(_speechData.getText())
                                                .disabled(_speechData.isSpeaking())
                                                .onChange(e -> _speechData.setText(
                                                        Js.<HTMLTextAreaElement>uncheckedCast(e.getTarget())
                                                                .value())))),
                        div(
                                new HtmlProps().className(argumentStyle.style()),
                                label(
                                        new LabelProps()
                                                .className(labelStyle.style())
                                                .htmlFor("pitch"),
                                        "Pitch"),
                                input(new InputProps()
                                        .className(rangeStyle.style())
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
                                                .className(resetStyle.style())
                                                .type(ButtonType.button)
                                                .prop("aria-label", Js.asAny("Reset pitch"))
                                                .title("Reset pitch")
                                                .disabled(_speechData.isSpeaking())
                                                .onClick(e -> _speechData.setPitch(DEFAULT_PITCH)),
                                        "\u21b6")),
                        div(
                                new HtmlProps().className(argumentStyle.style()),
                                label(
                                        new LabelProps()
                                                .className(labelStyle.style())
                                                .htmlFor("rate"),
                                        "Rate"),
                                input(new InputProps()
                                        .className(rangeStyle.style())
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
                                                .className(resetStyle.style())
                                                .type(ButtonType.button)
                                                .prop("aria-label", Js.asAny("Reset rate"))
                                                .title("Reset rate")
                                                .disabled(_speechData.isSpeaking())
                                                .onClick(e -> _speechData.setRate(DEFAULT_RATE)),
                                        "\u21b6")),
                        div(
                                new HtmlProps().className(argumentStyle.style()),
                                label(
                                        new LabelProps()
                                                .className(labelStyle.style())
                                                .htmlFor("volume"),
                                        "Volume"),
                                input(new InputProps()
                                        .className(rangeStyle.style())
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
                                                .className(resetStyle.style())
                                                .type(ButtonType.button)
                                                .prop("aria-label", Js.asAny("Reset volume"))
                                                .title("Reset volume")
                                                .disabled(_speechData.isSpeaking())
                                                .onClick(e -> _speechData.setVolume(DEFAULT_VOLUME)),
                                        "\u21b6")),
                        div(
                                new HtmlProps().className(argumentStyle.style()),
                                label(
                                        new LabelProps()
                                                .className(labelStyle.style())
                                                .htmlFor("voice"),
                                        "Voice"),
                                select(
                                        new SelectProps()
                                                .className(Application_Styled_SelectStyle.get()
                                                        .style())
                                                .onChange(this::onVoiceChange)
                                                .disabled(_speechData.isSpeaking())
                                                .value(null == voice ? "" : voice.voiceURI()),
                                        _speechData.getVoices().stream().map(this::renderVoiceOption)),
                                button(
                                        new BtnProps()
                                                .className(resetStyle.style())
                                                .type(ButtonType.button)
                                                .prop("aria-label", Js.asAny("Reset voice"))
                                                .title("Reset voice")
                                                .disabled(_speechData.isSpeaking())
                                                .onClick(e -> _speechData.resetVoice()),
                                        "\u21b6")),
                        div(
                                new HtmlProps()
                                        .className(Application_Styled_ActionsStyle.get()
                                                .style()),
                                button(
                                        new BtnProps()
                                                .className(actionStyle.style())
                                                .type(ButtonType.button)
                                                .prop("aria-label", Js.asAny("Speak"))
                                                .title("Speak")
                                                .disabled(_speechData.isSpeaking())
                                                .onClick(e -> _speechData.startSpeaking()),
                                        "Speak"),
                                button(
                                        new BtnProps()
                                                .className(actionStyle.style())
                                                .type(ButtonType.button)
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
                                                .className(actionStyle.style())
                                                .type(ButtonType.button)
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
        // Match the marker's .5rem margin when positioning its border around the word.
        final int marginInPixels = 8;
        return new CssProps()
                .customProperty(
                        "--marker-top",
                        null == markerRect
                                ? "0"
                                : ((markerRect.top() - 1 + body.clientHeight() - body.scrollHeight() - marginInPixels)
                                        + "px"))
                .customProperty(
                        "--marker-left",
                        null == markerRect
                                ? "0"
                                : ((markerRect.left() - 1 + body.clientWidth() - body.scrollWidth() - marginInPixels)
                                        + "px"))
                .customProperty("--marker-width", null == markerRect ? "0" : (markerRect.width + "px"))
                .customProperty("--marker-height", null == markerRect ? "0" : (markerRect.height + "px"));
    }

    @Css("""
        max-width: 480px;
        margin: 2rem auto;
        font-size: 150%;
        """)
    abstract static class TitleStyle {
        abstract String style();
    }

    @Css("""
        max-width: 480px;
        margin: 0 auto;
        """)
    abstract static class FormStyle {
        abstract String style();
    }

    @Css("""
        height: 2rem;
        display: flex;
        justify-content: space-between;
        align-items: center;
        """)
    abstract static class ArgumentStyle {
        abstract String style();
    }

    @Css("""
        display: inline-block;
        width: 20%;
        """)
    abstract static class LabelStyle {
        abstract String style();
    }

    @Css("""
        display: inline-block;
        width: calc(80% - 3rem);
        margin: 0;
        -moz-appearance: none;
        &::-moz-range-thumb {
          background-color: #fff;
          height: 1rem;
          width: .5rem;
          border-color: #fff;
          box-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
        }
        &::-moz-range-track {
          background-color: #fff;
        }
        """)
    abstract static class RangeStyle {
        abstract String style();
    }

    @Css("""
        display: inline-block;
        width: calc(80% - 3rem);
        margin: 0;
        appearance: none;
        """)
    abstract static class SelectStyle {
        abstract String style();
    }

    @Css("""
        width: 1.5rem;
        height: 1.5rem;
        margin: 0 0 0 1.5rem;
        display: inline-block;
        padding: 0;
        """)
    abstract static class ResetStyle {
        abstract String style();
    }

    @Css("""
        margin: 1rem 0;
        display: flex;
        justify-content: space-between;
        """)
    abstract static class ActionsStyle {
        abstract String style();
    }

    @Css("""
        height: 3.25rem;
        width: 3.25rem;
        padding: 0;
        """)
    abstract static class ActionStyle {
        abstract String style();
    }

    @Css("""
        width: 100%;
        height: 8rem;
        display: block;
        margin-bottom: 1rem;
        border-radius: 5px;
        position: relative;
        background-color: #fff;
        transition: background-color 100ms ease;
        &.speaking {
          background-color: #333;
        }
        """)
    abstract static class EditorStyle {
        abstract String style();

        abstract String speaking();
    }

    @Css("""
        position: absolute;
        display: block;
        border: none;
        resize: none;
        top: 0;
        left: 0;
        width: calc(100% - 1rem);
        height: calc(100% - 1rem);
        margin: .5rem;
        padding: 0;
        white-space: pre-wrap;
        line-height: 1.1rem;
        background-color: transparent;
        font-size: 12px;
        font-family: monospace;
        &.spoken {
          color: #aaa;
          overflow: auto;
        }
        """)
    abstract static class TextStyle {
        abstract String style();

        abstract String spoken();
    }

    @Css("""
        position: fixed;
        border: 1px solid orange;
        display: none;
        margin: .5rem;
        padding: 0;
        background-color: transparent;
        transition: all 0s ease 0s;
        top: var(--marker-top);
        left: var(--marker-left);
        width: var(--marker-width);
        height: var(--marker-height);
        &.visible {
          display: block;
          transition: all 50ms ease;
        }
        """)
    abstract static class MarkerStyle {
        abstract String style();

        abstract String visible();
    }
}
