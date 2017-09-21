package gwt.react.todo_mvc.client;

import gwt.react.client.GwtReactConfig;
import gwt.react.client.components.BaseState;
import gwt.react.client.components.Component;
import gwt.react.client.elements.ReactElement;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;

/**
 * TODO: Also should be generated.
 */
@JsType
class FooterWrapper
  extends Component<Footer.Props, BaseState>
{
  private final Footer _component;

  static JsConstructorFn<FooterWrapper> ctor()
  {
    final JsConstructorFn<FooterWrapper> constructorFn = JsConstructorFn.of( FooterWrapper.class );
    if ( null == constructorFn )
    {
      //TODO Replace this with invariant check soon
      throw new IllegalStateException( "Unable to get constructor function for FooterWrapper" );
    }
    if ( GwtReactConfig.enableComponentNames() )
    {
      JsPropertyMap.of( constructorFn ).set( "displayName", "Footer" );
    }
    return constructorFn;
  }

  @JsConstructor
  FooterWrapper( @Nonnull final Footer.Props props )
  {
    super( props );
    _component = new Footer( this );
  }

  public ReactElement<?, ?> render()
  {
    return _component.render();
  }
}
