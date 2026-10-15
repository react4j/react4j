package react4j.drumloop.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
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
        final boolean on = _drumMachine.isRunning();
        return button(
                new BtnProps()
                        .className("startButton", on ? null : "startButton_off")
                        .onClick(e -> _drumMachine.toggleRunning()),
                on ? "Stop" : "Play");
    }
}
