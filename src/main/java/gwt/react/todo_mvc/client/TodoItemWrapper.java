package gwt.react.todo_mvc.client;

import gwt.interop.utils.shared.JsHelper;
import gwt.react.client.GwtReactConfig;
import gwt.react.client.components.Component;
import gwt.react.client.elements.ReactElement;
import gwt.react.todo_mvc.client.TodoItem.Props;
import gwt.react.todo_mvc.client.TodoItem.State;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import jsinterop.base.JsConstructorFn;

/**
 * TODO: This should be generated from annotation processor, processing TodoItem.
 * Any lifecycle methods that are overridden (i.e. not defined in SideComponent)
 * should be copied here.
 */
@JsType
class TodoItemWrapper
  extends Component<Props, State>
{
  private final TodoItem _component;

  static JsConstructorFn<TodoItemWrapper> ctor()
  {
    final JsConstructorFn<TodoItemWrapper> constructorFn = JsConstructorFn.of( TodoItemWrapper.class );
    if ( null == constructorFn )
    {
      //TODO Replace this with invariant check soon
      throw new IllegalStateException( "Unable to get constructor function for TodoItemWrapper" );
    }
    if ( GwtReactConfig.enableComponentNames() )
    {
      JsHelper.setObjectProperty( constructorFn, "displayName", "TodoItem" );
    }
    return constructorFn;
  }

  @JsConstructor
  TodoItemWrapper( @Nonnull final Props props )
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
