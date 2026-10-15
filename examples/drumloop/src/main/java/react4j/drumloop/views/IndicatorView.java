package react4j.drumloop.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.proptypes.html.CssProps;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.drumloop.model.DrumMachine;

@View(type = View.Type.MAYBE_TRACKING)
abstract class IndicatorView {
    private final DrumMachine _drumMachine;

    IndicatorView(final DrumMachine drumMachine) {
        _drumMachine = drumMachine;
    }

    @Render
    ReactNode render() {
        final boolean on = _drumMachine.isRunning();
        final int step = _drumMachine.getCurrentStep();

        return div(
                new HtmlProps().className("indicatorContainer"),
                on ? div(new HtmlProps().className("indicator").style(new CssProps().left(step * 37.5 + "px"))) : null);
    }
}
