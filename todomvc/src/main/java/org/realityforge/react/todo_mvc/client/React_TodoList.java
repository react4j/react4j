package org.realityforge.react.todo_mvc.client;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react.core.BaseProps;
import react.core.Component;
import react.core.ReactElement;

@JsType
@Generated( "" )
final class React_TodoList
  extends Component<BaseProps, TodoList.State>
{
  private final TodoList _component;

  @JsConstructor
  private React_TodoList( @Nonnull final BaseProps props )
  {
    super( props );
    _component = new Arez_TodoList( this );
  }

  public ReactElement<?, ?> render()
  {
    return _component.render();
  }
}
