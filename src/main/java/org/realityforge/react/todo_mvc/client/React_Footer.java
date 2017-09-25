package org.realityforge.react.todo_mvc.client;

import gwt.react.client.components.BaseState;
import gwt.react.client.components.Component;
import gwt.react.client.elements.ReactElement;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;

@JsType
@Generated( "" )
final class React_Footer
  extends Component<Footer.Props, BaseState>
{
  private final Footer _component;

  @JsConstructor
  private React_Footer( @Nonnull final Footer.Props props )
  {
    super( props );
    _component = new Footer( this );
  }

  public ReactElement<?, ?> render()
  {
    return _component.render();
  }
}
