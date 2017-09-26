package org.realityforge.react.todo_mvc.client;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import org.realityforge.react.todo_mvc.client.TodoItem.Props;
import org.realityforge.react.todo_mvc.client.TodoItem.State;
import react.core.NativeAdapterComponent;

/**
 * TODO: This should be generated from annotation processor, processing TodoItem.
 * Any lifecycle methods that are overridden (i.e. not defined in Component)
 * should be copied here.
 */
@JsType
@Generated( "" )
final class React_TodoItem
  extends NativeAdapterComponent<Props, State, TodoItem>
{
  @JsConstructor
  private React_TodoItem( @Nonnull final Props props )
  {
    super( props );
  }

  @Override
  protected TodoItem createComponent()
  {
    return new Arez_TodoItem();
  }

  public boolean shouldComponentUpdate( @Nonnull final Props nextProps, @Nonnull final State nextState )
  {
    return performShouldComponentUpdate( nextProps, nextState );
  }

  public void componentDidUpdate( @Nonnull final Props prevProps, @Nonnull final Props prevState )
  {
    performComponentDidUpdate( prevProps, prevState );
  }
}
