package react4j.drumloop.views;

import static react4j.dom.DOM.*;

import arez.annotations.Action;
import arez.annotations.ComponentDependency;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
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
        var stepStyle = StepStyle.get();
        final boolean oddBar = 1 == (_cell.beat() / 4) % 2;
        return button(new BtnProps()
                .data("step", Integer.toString(_cell.beat()))
                .className(
                        _cell.on() ? stepStyle.on() : stepStyle.style(),
                        oddBar ? stepStyle.odd() : null,
                        _cell.doubled() ? stepStyle.doubled() : null)
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

    @Css("""
        flex: 1;
        background: #FD7272;
        opacity: 0.35;
        border-radius: 2px;
        margin: 2px;

        &.on {
          opacity: 1;
        }

        &.odd {
          background: #25CCF7;
        }

        @keyframes doubled {
          0%, 100% { opacity: 0.5; }
          50% { opacity: 1; }
        }

        &.doubled {
          animation: doubled 0.5s linear infinite;
        }
        """)
    abstract static class StepStyle {
        static StepStyle get() {
            return StepButton_Styled_StepStyle.get();
        }

        abstract String style();

        abstract String on();

        abstract String odd();

        abstract String doubled();
    }
}
