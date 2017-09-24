package gwt.react.todo_mvc.client;

import gwt.react.client.GwtReactConfig;
import gwt.react.client.components.BaseState;
import gwt.react.client.components.Component;
import gwt.react.client.elements.ReactElement;
import gwt.react.todo_mvc.client.PersonComponent.Props;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;

/**
 * TODO: Also should be generated.
 */
@JsType
class PersonComponentWrapper
  extends Component<Props, BaseState>
{
  private final PersonComponent _component;

  static JsConstructorFn<PersonComponentWrapper> ctor()
  {
    final JsConstructorFn<PersonComponentWrapper> constructorFn = JsConstructorFn.of( PersonComponentWrapper.class );
    if ( null == constructorFn )
    {
      //TODO Replace this with invariant check soon
      throw new IllegalStateException( "Unable to get constructor function for PersonComponentWrapper" );
    }
    if ( GwtReactConfig.enableComponentNames() )
    {
      JsPropertyMap.of( constructorFn ).set( "displayName", "PersonComponent" );
    }
    return constructorFn;
  }

  @JsConstructor
  PersonComponentWrapper( @Nonnull final Props props )
  {
    super( props );
    _component = new Arez_PersonComponent( this );
  }

  public ReactElement<?, ?> render()
  {
    return _component.render();
  }


  public boolean shouldComponentUpdate( @Nonnull final Props nextProps, @Nonnull final BaseState nextState )
  {
    return _component.shouldComponentUpdate( nextProps, nextState );
  }
}
