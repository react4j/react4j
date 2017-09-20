package gwt.react.todo_mvc.client;

import gwt.react.client.components.Component;
import gwt.react.client.components.ComponentConstructorFn;
import gwt.react.client.components.ComponentUtils;
import gwt.react.client.elements.ReactElement;
import gwt.react.todo_mvc.client.TodoItem.Props;
import gwt.react.todo_mvc.client.TodoItem.State;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsType;

/**
 * TODO: This should be generated from annotation processor, processing TodoItem.
 */
@JsType
class TodoItemWrapper
  extends Component<Props, State>
{
  private final TodoItem _component;

  static ComponentConstructorFn<Props> ctor()
  {
    return ComponentUtils.getCtorFn( TodoItemWrapper.class, "TodoItemWrapper" );
  }

  public TodoItemWrapper( @Nonnull final Props props )
  {
    super( props );
    _component = new TodoItem( this );
  }

  public boolean shouldComponentUpdate( @Nonnull final Props nextProps, @Nonnull final State nextState )
  {
    return _component.shouldComponentUpdate( nextProps, nextState );
  }

  public void componentDidUpdate( @Nonnull final Props prevProps, @Nonnull final Props prevState )
  {
    _component.componentDidUpdate( prevProps, prevState );
  }

  public ReactElement<?, ?> render()
  {
    return _component.render();
  }
}
