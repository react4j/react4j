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
import react4j.css.Css;
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
        var itemStyle = ItemStyle.get();
        var viewStyle = ViewStyle.get();
        var labelStyle = LabelStyle.get();
        var editStyle = EditStyle.get();
        final boolean completed = _todo.isCompleted();
        final boolean editing = isTodoBeingEdited();
        return li(
                new HtmlProps().className(editing ? itemStyle.editing() : itemStyle.style()),
                div(
                        new HtmlProps().className(editing ? viewStyle.editing() : viewStyle.style()),
                        input(new InputProps()
                                .className(ToggleStyle.get().style())
                                .type(InputType.checkbox)
                                .checked(completed)
                                .onChange(e -> onToggle())),
                        label(
                                new LabelProps()
                                        .className(completed ? labelStyle.completed() : labelStyle.style())
                                        .onDoubleClick(e -> onEdit()),
                                _todo.getTitle()),
                        button(new BtnProps()
                                .className(DestroyStyle.get().style())
                                .onClick(e -> onDestroy()))),
                input(new InputProps()
                        .ref(e -> _editField = Js.uncheckedCast(e))
                        .className(editing ? editStyle.editing() : editStyle.style())
                        .value(_editText)
                        .onBlur(e -> onSubmitTodo())
                        .onChange(this::handleChange)
                        .onKeyDown(this::handleKeyDown)));
    }

    @Css("""
        position: relative;
        font-size: 24px;
        border-bottom: 1px solid #ededed;
        --todo-destroy-display: none;
        &:hover {
          --todo-destroy-display: block;
        }
        &:last-child {
          border-bottom: none;
        }
        &.editing {
          border-bottom: none;
          padding: 0;
        }
        &.editing:last-child {
          margin-bottom: -1px;
        }
        """)
    abstract static class ItemStyle {
        static ItemStyle get() {
            return TodoItem_Styled_ItemStyle.get();
        }

        abstract String style();

        abstract String editing();
    }

    @Css("""
        &.editing {
          display: none;
        }
        """)
    abstract static class ViewStyle {
        static ViewStyle get() {
            return TodoItem_Styled_ViewStyle.get();
        }

        abstract String style();

        abstract String editing();
    }

    @Css("""
        outline: none;
        text-align: center;
        width: 40px;
        height: auto;
        position: absolute;
        top: 0;
        bottom: 0;
        margin: auto 0;
        border: none;
        -webkit-appearance: none;
        appearance: none;
        &:after {
          content: url('toggle.svg');
        }
        &:checked:after {
          content: url('toggle_checked.svg');
        }
        @media screen and (-webkit-min-device-pixel-ratio: 0) {
          & {
            background: none;
            height: 40px;
          }
        }
        """)
    abstract static class ToggleStyle {
        static ToggleStyle get() {
            return TodoItem_Styled_ToggleStyle.get();
        }

        abstract String style();
    }

    @Css("""
        white-space: pre-line;
        word-break: break-all;
        padding: 15px 60px 15px 15px;
        margin-left: 45px;
        display: block;
        line-height: 1.2;
        transition: color 0.4s;
        &.completed {
          color: #d9d9d9;
          text-decoration: line-through;
        }
        """)
    abstract static class LabelStyle {
        static LabelStyle get() {
            return TodoItem_Styled_LabelStyle.get();
        }

        abstract String style();

        abstract String completed();
    }

    @Css("""
        padding: 0;
        border: 0;
        background: none;
        vertical-align: baseline;
        font-family: inherit;
        font-weight: inherit;
        -webkit-appearance: none;
        appearance: none;
        -webkit-font-smoothing: antialiased;
        -moz-font-smoothing: antialiased;
        font-smoothing: antialiased;
        outline: none;
        display: var(--todo-destroy-display);
        position: absolute;
        top: 0;
        right: 10px;
        bottom: 0;
        width: 40px;
        height: 40px;
        margin: auto 0;
        font-size: 30px;
        color: #cc9a9a;
        margin-bottom: 11px;
        transition: color 0.2s ease-out;
        &:hover {
          color: #af5b5e;
        }
        &:after {
          content: '\\00D7';
        }
        """)
    abstract static class DestroyStyle {
        static DestroyStyle get() {
            return TodoItem_Styled_DestroyStyle.get();
        }

        abstract String style();
    }

    @Css("""
        position: relative;
        margin: 0;
        width: 100%;
        font-size: 24px;
        font-family: inherit;
        font-weight: inherit;
        line-height: 1.4em;
        outline: none;
        color: inherit;
        padding: 6px;
        border: 1px solid #999;
        box-shadow: inset 0 -1px 5px 0 rgba(0, 0, 0, 0.2);
        box-sizing: border-box;
        -webkit-font-smoothing: antialiased;
        -moz-font-smoothing: antialiased;
        font-smoothing: antialiased;
        display: none;
        &.editing {
          display: block;
          width: 506px;
          padding: 13px 17px 12px 17px;
          margin: 0 0 0 43px;
        }

        &::-webkit-input-placeholder {
          font-style: italic;
          font-weight: 300;
          color: #e6e6e6;
        }
        &::-moz-placeholder {
          font-style: italic;
          font-weight: 300;
          color: #e6e6e6;
        }
        &::input-placeholder {
          font-style: italic;
          font-weight: 300;
          color: #e6e6e6;
        }
        """)
    abstract static class EditStyle {
        static EditStyle get() {
            return TodoItem_Styled_EditStyle.get();
        }

        abstract String style();

        abstract String editing();
    }
}
