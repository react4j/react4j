package gwt.react.todo_mvc.client;

import gwt.react.client.GwtReactConfig;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;

/**
 * TODO: Also should be generated.
 */
public final class TodoList_
{
  public static final JsConstructorFn<React_TodoList> TYPE = ctor();

  private static JsConstructorFn<React_TodoList> ctor()
  {
    final JsConstructorFn<React_TodoList> constructorFn = JsConstructorFn.of( React_TodoList.class );
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for TodoList defined by class gwt.react.todo_mvc.client.TodoList" );
    assert null != constructorFn;
    if ( GwtReactConfig.enableComponentNames() )
    {
      JsPropertyMap.of( constructorFn ).set( "displayName", "TodoList" );

    }
    return constructorFn;
  }
}
