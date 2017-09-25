package gwt.react.todo_mvc.client;

import gwt.react.client.components.BaseState;
import gwt.react.client.components.Component;
import gwt.react.client.elements.ReactElement;
import gwt.react.todo_mvc.client.PersonComponent.Props;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;

/**
 * TODO: Also should be generated.
 */
@JsType
@Generated( "" )
final class React_PersonComponent
  extends Component<Props, BaseState>
{
  private final PersonComponent component;

  @JsConstructor
  private React_PersonComponent( @Nonnull final Props props )
  {
    super( props );
    component = new Arez_PersonComponent( this );
  }

  public ReactElement<?, ?> render()
  {
    return component.render();
  }

  public boolean shouldComponentUpdate( @Nonnull final Props nextProps, @Nonnull final BaseState nextState )
  {
    return component.shouldComponentUpdate( nextProps, nextState );
  }
}
