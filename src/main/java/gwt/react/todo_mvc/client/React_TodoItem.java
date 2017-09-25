package gwt.react.todo_mvc.client;

import gwt.react.client.GwtReactConfig;
import gwt.react.client.components.Component;
import gwt.react.client.elements.ReactElement;
import gwt.react.todo_mvc.client.TodoItem.Props;
import gwt.react.todo_mvc.client.TodoItem.State;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;

/**
 * TODO: This should be generated from annotation processor, processing TodoItem.
 * Any lifecycle methods that are overridden (i.e. not defined in SideComponent)
 * should be copied here.
 */
@JsType
final class React_TodoItem
  extends Component<Props, State>
{
  private final TodoItem _component;

  @JsConstructor
  private React_TodoItem( @Nonnull final Props props )
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
