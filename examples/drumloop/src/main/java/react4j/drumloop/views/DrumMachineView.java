package react4j.drumloop.views;

import static react4j.dom.DOM.*;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
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
                new HtmlProps().className(Styles.CONTAINER.style()),
                div(
                        new HtmlProps().className(Styles.HEADER.style()),
                        h1(new HtmlProps().className(Styles.LOGO.style()), "Trap Lord 9000"),
                        BpmInputBuilder.build(),
                        PlayButtonBuilder.build()),
                suspense(
                        p("Loading..."),
                        4000,
                        fragment(
                                div(
                                        new HtmlProps().className(Styles.SEQUENCER.style()),
                                        IndicatorBuilder.build(),
                                        fragment(_drumMachine.getTracks().stream()
                                                .map(TrackBuilder::track))),
                                div(
                                        new HtmlProps().className(Styles.BUTTONS.style()),
                                        fragment(_drumMachine.getEffects().stream()
                                                .map(FxButtonBuilder::sound))))));
    }
}
