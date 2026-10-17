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
    private static final ContainerStyle CONTAINER = new Styled_DrumMachineView_0024_ContainerStyle();
    private static final HeaderStyle HEADER = new Styled_DrumMachineView_0024_HeaderStyle();
    private static final LogoStyle LOGO = new Styled_DrumMachineView_0024_LogoStyle();
    private static final ButtonsStyle BUTTONS = new Styled_DrumMachineView_0024_ButtonsStyle();
    private static final SequencerStyle SEQUENCER = new Styled_DrumMachineView_0024_SequencerStyle();

    private final DrumMachine _drumMachine;

    DrumMachineView(final DrumMachine drumMachine) {
        _drumMachine = drumMachine;
    }

    @Render
    ReactNode render() {
        return div(
                new HtmlProps().className(CONTAINER.style()),
                div(
                        new HtmlProps().className(HEADER.style()),
                        h1(new HtmlProps().className(LOGO.style()), "Trap Lord 9000"),
                        BpmInputBuilder.build(),
                        PlayButtonBuilder.build()),
                suspense(
                        p("Loading..."),
                        4000,
                        fragment(
                                div(
                                        new HtmlProps().className(SEQUENCER.style()),
                                        IndicatorBuilder.build(),
                                        fragment(_drumMachine.getTracks().stream()
                                                .map(TrackBuilder::track))),
                                div(
                                        new HtmlProps().className(BUTTONS.style()),
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
