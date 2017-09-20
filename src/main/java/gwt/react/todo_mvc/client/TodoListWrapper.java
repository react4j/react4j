package gwt.react.todo_mvc.client;

import gwt.interop.utils.shared.JsHelper;
import gwt.react.client.GwtReactConfig;
import gwt.react.client.components.Component;
import gwt.react.client.elements.ReactElement;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import jsinterop.base.JsConstructorFn;

/**
 * TODO: Also should be generated.
 */
@JsType
class TodoListWrapper
  extends Component<TodoList.Props, TodoList.State>
{
  private final TodoList _component;

  static JsConstructorFn<TodoListWrapper> ctor()
  {
    final JsConstructorFn<TodoListWrapper> constructorFn = JsConstructorFn.of( TodoListWrapper.class );
    if ( null == constructorFn )
    {
      //TODO Replace this with invariant check soon
      throw new IllegalStateException( "Unable to get constructor function for TodoListWrapper" );
    }
    if ( GwtReactConfig.enableComponentNames() )
    {
      JsHelper.setObjectProperty( constructorFn, "displayName", "TodoList" );
    }
    return constructorFn;
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
