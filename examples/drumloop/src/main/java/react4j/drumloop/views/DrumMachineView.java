package react4j.drumloop.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.drumloop.model.DrumMachine;

@View(exportBuilder = true)
abstract class DrumMachineView {
    private final DrumMachine _drumMachine;

    DrumMachineView(final DrumMachine drumMachine) {
        _drumMachine = drumMachine;
    }

    @Render
    ReactNode render() {
        return div(
                new HtmlProps()
                        .className(DrumMachineView_Styled_ContainerStyle.get().style()),
                div(
                        new HtmlProps()
                                .className(
                                        DrumMachineView_Styled_HeaderStyle.get().style()),
                        h1(
                                new HtmlProps()
                                        .className(DrumMachineView_Styled_LogoStyle.get()
                                                .style()),
                                "Trap Lord 9000"),
                        BpmInputBuilder.build(),
                        PlayButtonBuilder.build()),
                suspense(
                        p("Loading..."),
                        4000,
                        fragment(
                                div(
                                        new HtmlProps()
                                                .className(DrumMachineView_Styled_SequencerStyle.get()
                                                        .style()),
                                        IndicatorBuilder.build(),
                                        fragment(_drumMachine.getTracks().stream()
                                                .map(TrackBuilder::track))),
                                div(
                                        new HtmlProps()
                                                .className(DrumMachineView_Styled_ButtonsStyle.get()
                                                        .style()),
                                        fragment(_drumMachine.getEffects().stream()
                                                .map(FxButtonBuilder::sound))))));
    }

    @Css("""
        max-width: 800px;
        margin: auto;
        background: linear-gradient(to bottom right, #222, #0a0a0a);
        border: 2px solid black;
        border-radius: 4px;
        margin-top: 20px;
        display: flex;
        flex-direction: column;
        """)
    abstract static class ContainerStyle {
        abstract String style();
    }

    @Css("""
        flex: 0 1 auto;
        display: flex;
        justify-content: space-between;
        flex-direction: row;
        align-items: center;
        """)
    abstract static class HeaderStyle {
        abstract String style();
    }

    @Css("""
        font-size: 28px;
        color: #25ccf7;
        font-family: 'Righteous', cursive;
        padding: 20px;
        margin: 0;
        text-transform: uppercase;
        display: inline-block;
        """)
    abstract static class LogoStyle {
        abstract String style();
    }

    @Css("""
        flex: 1;
        flex-direction: row;
        align-items: stretch;
        width: 100%;
        padding: 0 20px 10px;
        display: flex;
        """)
    abstract static class ButtonsStyle {
        abstract String style();
    }

    @Css("""
        display: flex;
        flex: 1;
        flex-direction: column;
        border: 1px solid #555;
        margin: 0 20px 20px;
        position: relative;
        """)
    abstract static class SequencerStyle {
        abstract String style();
    }
}
