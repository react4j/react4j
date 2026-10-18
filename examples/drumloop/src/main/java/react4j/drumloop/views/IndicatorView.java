package react4j.drumloop.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
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
                new HtmlProps()
                        .className(IndicatorView_Styled_IndicatorsStyle.get().style()),
                on
                        ? div(new HtmlProps()
                                .className(IndicatorView_Styled_IndicatorStyle.get()
                                        .style())
                                .style(new CssProps().customProperty("--indicator-left", step * 37.5 + "px")))
                        : null);
    }

    @Css("""
        box-sizing: border-box;
        position: absolute;
        width: 600px;
        top: 0;
        right: 0;
        height: 100%;
        padding: 4px 2px;
        pointer-events: none;
        """)
    abstract static class IndicatorsStyle {
        abstract String style();
    }

    @Css("""
        position: absolute;
        top: 0;
        width: 38px;
        height: 100%;
        background: #00ff0020;
        left: var(--indicator-left);
        """)
    abstract static class IndicatorStyle {
        abstract String style();
    }
}
