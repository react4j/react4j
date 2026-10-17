package react4j.todomvc.arez.views;

import static react4j.dom.DOM.*;

import arez.annotations.Action;
import arez.annotations.Observable;
import jsinterop.base.Js;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.events.FormEvent;
import react4j.dom.events.KeyboardEvent;
import react4j.dom.proptypes.html.InputProps;
import react4j.todomvc.arez.model.AppData;
import react4j.todomvc.browser.HTMLInputElement;
import react4j.todomvc.styles.Styles;

@View
abstract class TodoEntry {
    private String _todoText = "";

    @Observable
    String getTodoText() {
        return _todoText;
    }

    void setTodoText(final String todoText) {
        _todoText = todoText;
    }

    private void handleNewTodoKeyDown(final KeyboardEvent event) {
        if (KeyCodes.ENTER_KEY == event.getKeyCode()) {
            event.preventDefault();
            addTodo();
        }
    }

    @Action
    void addTodo() {
        final String val = getTodoText().trim();
        if (val.length() > 0) {
            AppData.service.addTodo(val);
            setTodoText("");
        }
    }

    @Action(reportParameters = false)
    void handleChange(final FormEvent event) {
        final HTMLInputElement input = Js.uncheckedCast(event.getTarget());
        setTodoText(input.value);
    }

    @Render
    ReactNode render() {
        return input(new InputProps()
                .className(Styles.ENTRY.style())
                .placeHolder("What needs to be done?")
                .value(getTodoText())
                .onKeyDown(this::handleNewTodoKeyDown)
                .onChange(this::handleChange)
                .autoFocus());
    }
}
