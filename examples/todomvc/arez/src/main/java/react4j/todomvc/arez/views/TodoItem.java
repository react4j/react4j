package react4j.todomvc.arez.views;

import static react4j.dom.DOM.*;

import arez.annotations.Action;
import arez.annotations.ComponentDependency;
import arez.annotations.Memoize;
import arez.annotations.Observable;
import arez.annotations.PostConstruct;
import jsinterop.base.Js;
import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Input;
import react4j.annotations.PostUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;
import react4j.dom.events.FormEvent;
import react4j.dom.events.KeyboardEvent;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.dom.proptypes.html.InputProps;
import react4j.dom.proptypes.html.LabelProps;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.todomvc.arez.model.AppData;
import react4j.todomvc.arez.model.Todo;
import react4j.todomvc.browser.HTMLInputElement;
import react4j.todomvc.styles.Styles;

@View
abstract class TodoItem {
    @ComponentDependency
    final Todo _todo;

    @Nullable
    private HTMLInputElement _editField;

    private boolean _isEditing;

    TodoItem(@Input final Todo todo) {
        _todo = todo;
    }

    private String _editText = "";

    @Observable
    String getEditText() {
        return _editText;
    }

    void setEditText(final String editText) {
        _editText = editText;
    }

    @Memoize
    boolean isTodoBeingEdited() {
        return AppData.viewService.getTodoBeingEdited() == _todo;
    }

    @PostConstruct
    void postConstruct() {
        resetEditText(_todo);
    }

    @Action
    void resetEditText(final Todo todo) {
        setEditText(todo.getTitle());
    }

    private void handleKeyDown(final KeyboardEvent event, final Todo todo) {
        if (KeyCodes.ESCAPE_KEY == event.getWhich()) {
            onCancel(todo);
        } else if (KeyCodes.ENTER_KEY == event.getWhich()) {
            onSubmitTodo(todo);
        }
    }

    @Action
    void onSubmitTodo(final Todo todo) {
        final String val = getEditText();
        if (!val.isEmpty()) {
            AppData.service.save(todo, val);
            AppData.viewService.setTodoBeingEdited(null);
            setEditText(val);
        } else {
            AppData.model.destroy(todo);
        }
    }

    @Action
    void onEdit(final Todo todo) {
        AppData.viewService.setTodoBeingEdited(todo);
        resetEditText(todo);
    }

    @Action
    void onCancel(final Todo todo) {
        resetEditText(todo);
        AppData.viewService.setTodoBeingEdited(null);
    }

    @Action(reportParameters = false)
    void handleChange(final FormEvent event) {
        if (isTodoBeingEdited()) {
            final HTMLInputElement input = Js.uncheckedCast(event.getTarget());
            setEditText(input.value);
        }
    }

    @Action
    @PostUpdate
    void postUpdate() {
        final boolean todoBeingEdited = isTodoBeingEdited();
        if (!_isEditing && todoBeingEdited) {
            _isEditing = true;
            resetEditText(_todo);
            assert null != _editField;
            _editField.focus();
            _editField.select();
        } else if (_isEditing && !todoBeingEdited) {
            _isEditing = false;
        }
    }

    @Render
    ReactNode render() {
        final boolean completed = _todo.isCompleted();
        final boolean editing = isTodoBeingEdited();
        return li(
                new HtmlProps().className(editing ? Styles.ITEM.editing() : Styles.ITEM.style()),
                div(
                        new HtmlProps().className(editing ? Styles.VIEW.editing() : Styles.VIEW.style()),
                        input(new InputProps()
                                .className(Styles.TOGGLE.style())
                                .type(InputType.checkbox)
                                .checked(completed)
                                .onChange(e -> _todo.toggle())),
                        label(
                                new LabelProps()
                                        .className(completed ? Styles.LABEL.completed() : Styles.LABEL.style())
                                        .onDoubleClick(e -> onEdit(_todo)),
                                _todo.getTitle()),
                        button(new BtnProps()
                                .className(Styles.DESTROY.style())
                                .onClick(e -> AppData.model.destroy(_todo)))),
                input(new InputProps()
                        .ref(e -> _editField = Js.uncheckedCast(e))
                        .className(editing ? Styles.EDIT.editing() : Styles.EDIT.style())
                        .value(getEditText())
                        .onBlur(e -> onSubmitTodo(_todo))
                        .onChange(this::handleChange)
                        .onKeyDown(e -> handleKeyDown(e, _todo))));
    }
}
