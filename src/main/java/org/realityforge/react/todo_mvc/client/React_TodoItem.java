package org.realityforge.react.todo_mvc.client;

import react.core.Component;
import react.core.ReactElement;
import org.realityforge.react.todo_mvc.client.TodoItem.Props;
import org.realityforge.react.todo_mvc.client.TodoItem.State;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;

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
    _component = new Arez_TodoItem( this );
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
