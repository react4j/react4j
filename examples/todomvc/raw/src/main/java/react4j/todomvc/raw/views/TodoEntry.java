package react4j.todomvc.raw.views;

import static react4j.dom.DOM.*;

import jsinterop.base.Js;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;
import react4j.annotations.View;
import react4j.dom.events.FormEvent;
import react4j.dom.events.KeyboardEvent;
import react4j.dom.proptypes.html.InputProps;
import react4j.todomvc.browser.HTMLInputElement;
import react4j.todomvc.raw.model.AppData;

@View(type = View.Type.STATEFUL)
abstract class TodoEntry {
    private String _todoText = "";

    @ScheduleRender
    abstract void scheduleRender();

    private void setTodoText(final String todoText) {
        _todoText = todoText;
        scheduleRender();
    }

    private void handleNewTodoKeyDown(final KeyboardEvent event) {
        if (KeyCodes.ENTER_KEY == event.getKeyCode()) {
            event.preventDefault();
            final String val = _todoText.trim();
            if (val.length() > 0) {
                AppData.service.addTodo(val);
                setTodoText("");
            }
        }
    }

    private void handleChange(final FormEvent event) {
        final HTMLInputElement input = Js.uncheckedCast(event.getTarget());
        setTodoText(input.value);
    }

    @Render
    ReactNode render() {
        return input(new InputProps()
                .className("new-todo")
                .placeHolder("What needs to be done?")
                .value(_todoText)
                .onKeyDown(this::handleNewTodoKeyDown)
                .onChange(this::handleChange)
                .autoFocus());
    }
}
