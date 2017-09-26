package org.realityforge.react.todo_mvc.client;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsType;
import react.core.BaseState;
import react.core.NativeAdapterComponent;
import static org.realityforge.react.todo_mvc.client.Footer.Props;

@JsType
@Generated( "" )
final class React_Footer
  extends NativeAdapterComponent<Props, BaseState, Footer>
{
  @JsConstructor
  private React_Footer( @Nonnull final Props props )
  {
    super( props );
  }

  @Override
  protected Footer createComponent()
  {
    return new Footer();
  }
}
