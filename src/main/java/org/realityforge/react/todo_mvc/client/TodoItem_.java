package org.realityforge.react.todo_mvc.client;

import gwt.react.client.GwtReactConfig;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;

/**
 * TODO: This should be generated from annotation processor, processing TodoItem.
 * Any lifecycle methods that are overridden (i.e. not defined in SideComponent)
 * should be copied here.
 */
@Generated( "" )
public final class TodoItem_
{
  static final JsConstructorFn<React_TodoItem> TYPE = ctor();

  @Nonnull
  private static JsConstructorFn<React_TodoItem> ctor()
  {
    final JsConstructorFn<React_TodoItem> constructorFn = JsConstructorFn.of( React_TodoItem.class );
    Guards.invariant( () -> null != constructorFn,
                      () -> "Unable to locate constructor function for TodoItem defined by class org.realityforge.react.todo_mvc.client.TodoItem" );
    assert null != constructorFn;
    if ( GwtReactConfig.enableComponentNames() )
    {
      JsPropertyMap.of( constructorFn ).set( "displayName", "TodoItem" );
    }
    return constructorFn;
  }
}
