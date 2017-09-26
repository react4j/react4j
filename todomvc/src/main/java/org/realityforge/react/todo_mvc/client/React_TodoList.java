package org.realityforge.react.todo_mvc.client;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react.core.BaseProps;
import react.core.NativeAdapterComponent;
import static org.realityforge.react.todo_mvc.client.TodoList.State;

@JsType
@Generated( "" )
final class React_TodoList
  extends NativeAdapterComponent<BaseProps, State, TodoList>
{
  @JsConstructor
  private React_TodoList( @Nonnull final BaseProps props )
  {
    super( props );
  }

  @Override
  protected TodoList createComponent()
  {
    return new Arez_TodoList();
  }
}
