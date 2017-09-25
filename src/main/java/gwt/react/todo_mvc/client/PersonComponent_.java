package gwt.react.todo_mvc.client;

import gwt.react.client.GwtReactConfig;
import javax.annotation.Generated;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;

/**
 * TODO: Also should be generated.
 */
@Generated( "" )
final class PersonComponent_
{
  static final JsConstructorFn<React_PersonComponent> TYPE = ctor();

  private static JsConstructorFn<React_PersonComponent> ctor()
  {
    final JsConstructorFn<React_PersonComponent> constructorFn = JsConstructorFn.of( React_PersonComponent.class );
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for PersonComponent defined by class gwt.react.todo_mvc.client.PersonComponent" );
    assert null != constructorFn;
    if ( GwtReactConfig.enableComponentNames() )
    {
      JsPropertyMap.of( constructorFn ).set( "displayName", "PersonComponent" );
    }
    return constructorFn;
  }
}
