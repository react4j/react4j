package org.realityforge.react.todo_mvc.client;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.base.JsConstructorFn;
import jsinterop.base.JsPropertyMap;
import org.realityforge.braincheck.Guards;
import react.core.ReactConfig;

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
    if ( ReactConfig.enableComponentNames() )
    {
      JsPropertyMap.of( constructorFn ).set( "displayName", "TodoItem" );
    }
    return constructorFn;
  }
}
