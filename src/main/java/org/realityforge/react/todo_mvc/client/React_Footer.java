package org.realityforge.react.todo_mvc.client;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

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
