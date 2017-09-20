package gwt.react.todo_mvc.client;

import gwt.react.client.components.Component;
import gwt.react.client.components.ComponentConstructorFn;
import gwt.react.client.components.ComponentUtils;
import gwt.react.client.elements.ReactElement;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;

/**
 * TODO: Also should be generated.
 */
@JsType
class TodoListWrapper
  extends Component<TodoList.Props, TodoList.State>
{
  private final TodoList _component;

  static ComponentConstructorFn<TodoList.Props> ctor()
  {
    return ComponentUtils.getCtorFn( TodoListWrapper.class, "TodoList" );
  }

  @JsConstructor
  TodoListWrapper( @Nonnull final TodoList.Props props )
  {
    super( props );
    _component = new TodoList( this );
  }

  public ReactElement<?, ?> render()
  {
    return _component.render();
  }
}
