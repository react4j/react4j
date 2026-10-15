package react4j.drumloop.views;

import static react4j.dom.DOM.*;

import jsinterop.base.Js;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.events.FormEvent;
import react4j.dom.proptypes.html.InputProps;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.drumloop.browser.HTMLInputElement;
import react4j.drumloop.model.DrumMachine;

@View(type = View.Type.TRACKING)
abstract class BpmInput {
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
                .className("bpmInput")
                .type(InputType.number)
                .value(String.valueOf(_drumMachine.getBpm()))
                .min("60")
                .max("180")
                .onChange(this::onBpmChange));
    }
}
