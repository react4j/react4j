package gwt.react.todo_mvc.client;

import com.google.gwt.dom.client.InputElement;
import gwt.interop.utils.client.plainobjects.JsPlainObj;
import gwt.interop.utils.shared.functional.JsBiConsumer;
import gwt.react.client.components.Component;
import gwt.react.client.elements.ReactElement;
import gwt.react.client.events.FocusEvent;
import gwt.react.client.events.FormEvent;
import gwt.react.client.events.KeyboardEvent;
import gwt.react.client.events.MouseEvent;
import gwt.react.client.proptypes.BaseProps;
import gwt.react.client.proptypes.html.BtnProps;
import gwt.react.client.proptypes.html.HtmlProps;
import gwt.react.client.proptypes.html.InputProps;
import gwt.react.client.proptypes.html.LabelProps;
import gwt.react.client.proptypes.html.attributeTypes.InputType;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import static gwt.interop.utils.client.plainobjects.JsPlainObj.$;
import static gwt.react.client.api.React.DOM.*;

@JsType
class TodoItem extends Component<TodoItem.TodoItemProps, TodoItem.TodoState> {

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    static class TodoItemProps extends BaseProps {
        TodoModel.Todo todo;
        boolean isEditing;
        JsBiConsumer<TodoModel.Todo, String> doSave;
        JsBiConsumer<TodoList.Action, TodoModel.Todo> doAction;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    static class TodoState extends JsPlainObj {
        String editText;
    }

    private TodoState newTodoItemState(String editText) {
        return $(new TodoState(), "editText", editText);
    }

    public TodoItem(TodoItem.TodoItemProps props) {
        super(props);

        state = newTodoItemState(props.todo.title);
    }

    private void submitTodo(FocusEvent event) {
        String val = state.editText;
        if (val != null && !val.isEmpty()) {
            props.doSave.accept(props.todo, val);

            setState(newTodoItemState(val));
        } else {
            props.doAction.accept(TodoList.Action.DESTROY, props.todo);
        }
    }

    private void handleEdit(MouseEvent event) {
        props.doAction.accept(TodoList.Action.EDIT, props.todo);
        setState(newTodoItemState(props.todo.title));
    }

    private void handleKeyDown(KeyboardEvent event) {
        if (event.which == App.ESCAPE_KEY) {
            setState(newTodoItemState(props.todo.title));
            props.doAction.accept(TodoList.Action.CANCEL, props.todo);
        } else if (event.which == App.ENTER_KEY) {
            submitTodo(null);
        }
    }

    private void handleChange(FormEvent event) {
        if (props.isEditing) {
            setState(newTodoItemState(InputElement.as(event.target).getValue()));
        }
    }


    /**
     * This is a completely optional performance enhancement that you can
     * implement on any React component. If you were to delete this method
     * the app would still work correctly (and still be very performant!), we
     * just use it as an example of how little code it takes to getCtorFn an order
     * of magnitude performance improvement.
     */
    public boolean shouldComponentUpdate(TodoItemProps nextProps, TodoState nextState) {
        return (nextProps.todo != props.todo ||
                nextProps.isEditing != props.isEditing ||
                !nextState.editText.equals(state.editText));
    }

    /**
     * Safely manipulate the DOM after updating the state when invoking
     * `props.onEdit()` in the `handleEdit` method above.
     * For more info refer to notes at https://facebook.github.io/react/docs/component-api.html#setstate
     * and https://facebook.github.io/react/docs/component-specs.html#updating-componentdidupdate
     */
    public void componentDidUpdate(TodoItemProps prevProps, TodoItemProps prevState) {

        if (!prevProps.isEditing && props.isEditing) {
            InputElement inputEl = InputElement.as((InputElement)this.refs.get("editField"));
            inputEl.focus();
            inputEl.select();
        }
    }

    public ReactElement<?, ?> render() {
        return
            li(new HtmlProps()
                    .className(Classnames.get("completed", props.todo.completed, "editing", props.isEditing)),
                div(new HtmlProps().className("view"),
                    input(new InputProps()
                            .className("toggle")
                            .type(InputType.checkbox).checked(props.todo.completed)
                            .onChange((event) -> props.doAction.accept(TodoList.Action.TOGGLE, props.todo))),
                    label(new LabelProps()
                            .OnDoubleClick(this::handleEdit), props.todo.title),
                    button(new BtnProps()
                            .className("destroy")
                            .onClick((event) -> props.doAction.accept(TodoList.Action.DESTROY, props.todo)))
                ),
                input(new InputProps()
                        .ref("editField")
                        .className("edit")
                        .defaultValue(state.editText)
                        .onBlur(this::submitTodo)
                        .onChange(this::handleChange)
                        .onKeyDown(this::handleKeyDown))
            );
    }
}
