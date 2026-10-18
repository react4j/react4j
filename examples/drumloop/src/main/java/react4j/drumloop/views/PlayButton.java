package react4j.drumloop.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
import react4j.dom.proptypes.html.BtnProps;
import react4j.drumloop.model.DrumMachine;

@View(type = View.Type.TRACKING)
abstract class PlayButton {
    private final DrumMachine _drumMachine;

    PlayButton(final DrumMachine drumMachine) {
        _drumMachine = drumMachine;
    }

    @Render
    ReactNode render() {
        var playStyle = PlayStyle.get();
        final boolean on = _drumMachine.isRunning();
        return button(
                new BtnProps()
                        .className(on ? playStyle.style() : playStyle.off())
                        .onClick(e -> _drumMachine.toggleRunning()),
                on ? "Stop" : "Play");
    }

    @Css("""
        color: #FD7272;
        border: 2px solid #FD7272;
        background: none;
        font-family: 'Righteous', cursive;
        padding: 10px;
        font-size: 18px;
        border-radius: 2px;
        margin: 2px 20px 2px 4px;
        align-self: center;
        min-width: 100px;

        &.off {
          color: #25CCF7;
          border: 2px solid #25CCF7;
        }
        """)
    abstract static class PlayStyle {
        static PlayStyle get() {
            return PlayButton_Styled_PlayStyle.get();
        }

        abstract String style();

        abstract String off();
    }
}
