package gwt.react.todo_mvc.client;

import gwt.react.client.GwtReactConfig;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;

/**
 * TODO: Also should be generated.
 */
@Generated( "" )
public final class Footer_
{
  public static final JsConstructorFn<React_Footer> TYPE = ctor();

  @Nonnull
  private static JsConstructorFn<React_Footer> ctor()
  {
    final JsConstructorFn<React_Footer> constructorFn = JsConstructorFn.of( React_Footer.class );
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for Footer defined by class gwt.react.todo_mvc.client.Footer" );
    assert null != constructorFn;
    if ( GwtReactConfig.enableComponentNames() )
    {
      JsPropertyMap.of( constructorFn ).set( "displayName", "Footer" );
    }
    return constructorFn;
  }
}
