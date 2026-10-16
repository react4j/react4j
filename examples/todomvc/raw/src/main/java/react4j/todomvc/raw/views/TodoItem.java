package react4j.todomvc.raw.views;

import static react4j.dom.DOM.*;

import arez.SafeProcedure;
import arez.annotations.PreDispose;
import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.PostMount;
import react4j.annotations.PostUpdate;
import react4j.annotations.Render;
import react4j.annotations.ScheduleRender;
import react4j.annotations.View;
import react4j.dom.events.FormEvent;
import react4j.dom.events.KeyboardEvent;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.dom.proptypes.html.InputProps;
import react4j.dom.proptypes.html.LabelProps;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.todomvc.browser.HTMLInputElement;
import react4j.todomvc.raw.model.AppData;
import react4j.todomvc.raw.model.Todo;

@View(type = View.Type.STATEFUL)
abstract class TodoItem {
    private final SafeProcedure _onChange = this::scheduleRender;

    private final Todo _todo;

    @Nullable
    private HTMLInputElement _editField;

    private boolean _isEditing;
    private String _editText;

    TodoItem(@Input final Todo todo) {
        _todo = todo;
        _editText = todo.getTitle();
    }

    @ScheduleRender
    abstract void scheduleRender();

    private void setEditText(final String editText) {
        _editText = editText;
        scheduleRender();
    }

    private void resetEditTextAndReRender() {
        setEditText(_todo.getTitle());
    }

    private boolean isTodoBeingEdited() {
        return AppData.viewService.getTodoBeingEdited() == _todo;
    }

    @PostMount
    void postMount() {
        _todo.subscribe(_onChange);
    }

    private void handleKeyDown(final KeyboardEvent event) {
        if (KeyCodes.ESCAPE_KEY == event.getWhich()) {
            onCancel();
        } else if (KeyCodes.ENTER_KEY == event.getWhich()) {
            onSubmitTodo();
        }
    }

    private void onSubmitTodo() {
        if (!_editText.isEmpty()) {
            AppData.service.save(_todo, _editText);
            AppData.viewService.setTodoBeingEdited(null);
            setEditText(_editText);
        } else {
            AppData.service.destroy(_todo);
        }
    }

    private void onToggle() {
        AppData.service.toggle(_todo);
    }

    private void onEdit() {
        AppData.viewService.setTodoBeingEdited(_todo);
        resetEditTextAndReRender();
    }

    private void onDestroy() {
        AppData.service.destroy(_todo);
    }

    private void onCancel() {
        AppData.viewService.setTodoBeingEdited(null);
        resetEditTextAndReRender();
    }

    private void handleChange(final FormEvent event) {
        if (isTodoBeingEdited()) {
            final HTMLInputElement input = Js.uncheckedCast(event.getTarget());
            setEditText(input.value);
        }
    }

    @PostUpdate
    void postUpdate() {
        final boolean todoBeingEdited = isTodoBeingEdited();
        if (!_isEditing && todoBeingEdited) {
            _isEditing = true;
            assert null != _editField;
            _editField.focus();
            _editField.select();
            resetEditTextAndReRender();
        } else if (_isEditing && !todoBeingEdited) {
            _isEditing = false;
        }
    }

    @PreDispose
    void preDispose() {
        _todo.unsubscribe(_onChange);
    }

    @Render
    ReactNode render() {
        final boolean completed = _todo.isCompleted();
        return li(
                new HtmlProps().className(completed ? "checked" : null, isTodoBeingEdited() ? "editing" : null),
                div(
                        new HtmlProps().className("view"),
                        input(new InputProps()
                                .className("toggle")
                                .type(InputType.checkbox)
                                .checked(completed)
                                .onChange(e -> onToggle())),
                        label(new LabelProps().onDoubleClick(e -> onEdit()), _todo.getTitle()),
                        button(new BtnProps().className("destroy").onClick(e -> onDestroy()))),
                input(new InputProps()
                        .ref(e -> _editField = Js.uncheckedCast(e))
                        .className("edit")
                        .value(_editText)
                        .onBlur(e -> onSubmitTodo())
                        .onChange(this::handleChange)
                        .onKeyDown(this::handleKeyDown)));
    }
}
