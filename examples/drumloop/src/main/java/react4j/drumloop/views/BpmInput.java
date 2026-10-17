package react4j.drumloop.views;

import static react4j.dom.DOM.*;

import jsinterop.base.Js;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.css.Css;
import react4j.dom.events.FormEvent;
import react4j.dom.proptypes.html.InputProps;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.drumloop.browser.HTMLInputElement;
import react4j.drumloop.model.DrumMachine;

@View(type = View.Type.TRACKING)
abstract class BpmInput {
    private static final BpmStyle BPM = new Styled_BpmInput_0024_BpmStyle();

    private final DrumMachine _drumMachine;

    BpmInput(final DrumMachine drumMachine) {
        _drumMachine = drumMachine;
    }

    private void onBpmChange(final FormEvent e) {
        _drumMachine.setBpm(Integer.parseInt(
                Js.<HTMLInputElement>uncheckedCast(e.getTarget()).value()));
    }

    @Render
    ReactNode render() {
        return input(new InputProps()
                .className(BPM.style())
                .type(InputType.number)
                .value(String.valueOf(_drumMachine.getBpm()))
                .min("60")
                .max("180")
                .onChange(this::onBpmChange));
    }

    @Css("""
        color: #25ccf7;
        border: 2px solid #25ccf7;
        font-size: 18px;
        background: none;
        padding: 10px;
        font-family: 'Righteous', cursive;
        border-radius: 2px;
        margin: 2px 20px 2px 4px;
        align-self: center;
        """)
    abstract static class BpmStyle {
        abstract String style();
    }
}
