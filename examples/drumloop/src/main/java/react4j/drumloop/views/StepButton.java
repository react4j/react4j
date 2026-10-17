package react4j.drumloop.views;

import static react4j.dom.DOM.*;

import arez.annotations.Action;
import arez.annotations.ComponentDependency;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.proptypes.html.BtnProps;
import react4j.drumloop.model.StepCell;

@View(type = View.Type.TRACKING)
abstract class StepButton {
    @ComponentDependency
    final StepCell _cell;

    StepButton(@Input final StepCell cell) {
        _cell = cell;
    }

    @Render
    ReactNode render() {
        final boolean oddBar = 1 == (_cell.beat() / 4) % 2;
        return button(new BtnProps()
                .data("step", Integer.toString(_cell.beat()))
                .className(
                        _cell.on() ? Styles.STEP.on() : Styles.STEP.style(),
                        oddBar ? Styles.STEP.odd() : null,
                        _cell.doubled() ? Styles.STEP.doubled() : null)
                .onClick(e -> toggleCell(e.isShiftKey())));
    }

    @Action
    void toggleCell(final boolean shiftKey) {
        final boolean on = _cell.on();
        if (on) {
            if (shiftKey && !_cell.doubled()) {
                _cell.setDoubled();
            } else {
                _cell.setOff();
            }
        } else {
            if (shiftKey) {
                _cell.setDoubled();
            } else {
                _cell.setOn();
            }
        }
    }
}
