package react4j.todomvc.sting.views;

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
import react4j.css.Css;
import react4j.css.CssImport;
import react4j.dom.events.FormEvent;
import react4j.dom.events.KeyboardEvent;
import react4j.dom.proptypes.html.BtnProps;
import react4j.dom.proptypes.html.HtmlProps;
import react4j.dom.proptypes.html.InputProps;
import react4j.dom.proptypes.html.LabelProps;
import react4j.dom.proptypes.html.attributeTypes.InputType;
import react4j.todomvc.browser.HTMLInputElement;
import react4j.todomvc.sting.model.Todo;
import react4j.todomvc.sting.model.TodoRepository;
import react4j.todomvc.sting.model.TodoService;
import react4j.todomvc.sting.model.ViewService;
import react4j.todomvc.views.ItemAssets;

@View
abstract class TodoItem {
    private final TodoRepository _todoRepository;
    private final TodoService _todoService;
    private final ViewService _viewService;

    @ComponentDependency
    final Todo _todo;

    @Nullable
    private HTMLInputElement _editField;

    private boolean _isEditing;

    TodoItem(
            final TodoRepository todoRepository,
            final TodoService todoService,
            final ViewService viewService,
            @Input final Todo todo) {
        _todoRepository = todoRepository;
        _todoService = todoService;
        _viewService = viewService;
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
        return _viewService.getTodoBeingEdited() == _todo;
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
            _todoService.save(todo, val);
            _viewService.setTodoBeingEdited(null);
            setEditText(val);
        } else {
            _todoRepository.destroy(todo);
        }
    }

    @Action
    void onEdit(final Todo todo) {
        _viewService.setTodoBeingEdited(todo);
        resetEditText(todo);
    }

    @Action
    void onCancel(final Todo todo) {
        resetEditText(todo);
        _viewService.setTodoBeingEdited(null);
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
                                .onChange(e -> _todo.toggle())),
                        label(
                                new LabelProps()
                                        .className(completed ? labelStyle.completed() : labelStyle.style())
                                        .onDoubleClick(e -> onEdit(_todo)),
                                _todo.getTitle()),
                        button(new BtnProps()
                                .className(DestroyStyle.get().style())
                                .onClick(e -> _todoRepository.destroy(_todo)))),
                input(new InputProps()
                        .ref(e -> _editField = Js.uncheckedCast(e))
                        .className(editing ? editStyle.editing() : editStyle.style())
                        .value(getEditText())
                        .onBlur(e -> onSubmitTodo(_todo))
                        .onChange(this::handleChange)
                        .onKeyDown(e -> handleKeyDown(e, _todo))));
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

    @CssImport(value = ItemAssets.class, name = "media")
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
          content: url("asset:media.toggle");
        }
        &:checked:after {
          content: url("asset:media.checked");
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

    @CssImport(value = ItemStyle.class, property = "todo-destroy-display")
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
