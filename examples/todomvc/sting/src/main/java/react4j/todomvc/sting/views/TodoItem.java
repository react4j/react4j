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

@View
abstract class TodoItem {
    private static final ItemStyle ITEM = new Styled_TodoItem_0024_ItemStyle();
    private static final ViewStyle VIEW = new Styled_TodoItem_0024_ViewStyle();
    private static final ToggleStyle TOGGLE = new Styled_TodoItem_0024_ToggleStyle();
    private static final LabelStyle LABEL = new Styled_TodoItem_0024_LabelStyle();
    private static final DestroyStyle DESTROY = new Styled_TodoItem_0024_DestroyStyle();
    private static final EditStyle EDIT = new Styled_TodoItem_0024_EditStyle();

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
        final boolean completed = _todo.isCompleted();
        final boolean editing = isTodoBeingEdited();
        return li(
                new HtmlProps().className(editing ? ITEM.editing() : ITEM.style()),
                div(
                        new HtmlProps().className(editing ? VIEW.editing() : VIEW.style()),
                        input(new InputProps()
                                .className(TOGGLE.style())
                                .type(InputType.checkbox)
                                .checked(completed)
                                .onChange(e -> _todo.toggle())),
                        label(
                                new LabelProps()
                                        .className(completed ? LABEL.completed() : LABEL.style())
                                        .onDoubleClick(e -> onEdit(_todo)),
                                _todo.getTitle()),
                        button(new BtnProps().className(DESTROY.style()).onClick(e -> _todoRepository.destroy(_todo)))),
                input(new InputProps()
                        .ref(e -> _editField = Js.uncheckedCast(e))
                        .className(editing ? EDIT.editing() : EDIT.style())
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
        abstract String style();

        abstract String editing();
    }

    @Css("""
        &.editing {
          display: none;
        }
        """)
    abstract static class ViewStyle {
        abstract String style();

        abstract String editing();
    }

    @Css("""
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
        abstract String style();

        abstract String completed();
    }

    @Css("""
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
        abstract String style();

        abstract String editing();
    }
}
