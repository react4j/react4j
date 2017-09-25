package org.realityforge.react.todo_mvc.client;

import gwt.react.client.components.BaseProps;
import gwt.react.client.components.Component;
import gwt.react.client.elements.ReactElement;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;

/**
 * TODO: Also should be generated.
 */
@JsType
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
