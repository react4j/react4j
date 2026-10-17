package react4j.drumloop.views;

import static react4j.dom.DOM.*;

import arez.annotations.Action;
import arez.annotations.Observable;
import arez.annotations.PreDispose;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
import react4j.dom.proptypes.html.BtnProps;
import react4j.drumloop.browser.audio.AudioBufferSourceNode;
import react4j.drumloop.browser.audio.AudioContext;
import react4j.drumloop.browser.audio.GainNode;
import react4j.drumloop.model.SoundEffect;

@View(type = View.Type.TRACKING)
abstract class FxButton {
    private static final FxButtonStyle FX_BUTTON = new Styled_FxButton_0024_FxButtonStyle();

    private final SoundEffect _sound;

    @Nullable
    private AudioBufferSourceNode _node;

    FxButton(@Input final SoundEffect sound) {
        _sound = sound;
    }

    @Observable()
    abstract boolean held();

    abstract void setHeld(boolean held);

    @PreDispose
    void preDispose() {
        stop();
    }

    @Render
    ReactNode render() {
        _sound.suspendUntilAudioLoaded();
        return button(
                new BtnProps()
                        .data("sound", _sound.getName())
                        .className(held() ? FX_BUTTON.held() : FX_BUTTON.style())
                        .onMouseDown(e -> playSound(e.isShiftKey()))
                        .onTouchStart(e -> playSound(e.isShiftKey()))
                        .onTouchEnd(e -> stopSound())
                        .onMouseUp(e -> stopSound()),
                _sound.getName());
    }

    @Action
    void playSound(final boolean isShiftKeyDown) {
        if (held()) {
            setHeld(false);
            stop();
        } else {
            if (isShiftKeyDown) {
                loop();
                setHeld(true);
            } else {
                play();
            }
        }
    }

    @Action
    void stopSound() {
        if (!held()) {
            stop();
        }
    }

    private AudioBufferSourceNode newAudioNode() {
        final AudioContext audioContext = _sound.getDrumMachine().getActiveAudioContext();
        final AudioBufferSourceNode node = audioContext.createBufferSource();
        node.buffer = _sound.getAudioBuffer();
        final GainNode gain = audioContext.createGain();
        node.connect(gain);
        gain.gain().value = 0.2F;
        gain.connect(audioContext.destination());
        return node;
    }

    private void play() {
        if (null != _node) {
            stop();
        }
        start(false);
    }

    private void start(final boolean loop) {
        final AudioBufferSourceNode node = newAudioNode();
        node.loop = loop;
        node.onended = e -> stopUnlessLooping(node);
        node.start(0);
        _node = node;
    }

    private void stopUnlessLooping(final AudioBufferSourceNode node) {
        if (!node.loop) {
            stop();
        }
    }

    private void loop() {
        if (null != _node) {
            _node.loop = true;
        } else {
            start(true);
        }
    }

    private void stop() {
        if (null != _node) {
            _node.stop();
            _node.disconnect();
            _node = null;
        }
    }

    @Css("""
        background: none;
        flex: 1;
        border: 2px solid #fd7272;
        color: #fd7272;
        display: inline-block;
        font-size: 24px;
        border-radius: 2px;
        padding: 20px;
        font-family: 'Righteous', cursive;
        margin: 2px;

        &.held {
          background: #fd7272;
          color: black;
        }

        &:active {
          background: #fd7272;
          color: black;
        }
        """)
    abstract static class FxButtonStyle {
        abstract String style();

        abstract String held();
    }
}
