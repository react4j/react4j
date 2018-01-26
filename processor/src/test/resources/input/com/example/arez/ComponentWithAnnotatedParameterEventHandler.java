package com.example.arez;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.ReactNode;

@ReactComponent
abstract class ComponentWithAnnotatedParameterEventHandler
  extends ReactArezComponent<BaseProps, BaseContext>
{
  @JsFunction
  public interface CustomHandler
  {
    void onMyEvent( @Nonnull String i );
  }

  @JsFunction
  public interface CustomHandler2
  {
    @Nonnull
    String onMyEvent();
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @EventHandler( CustomHandler2.class )
  @Nonnull
  String handleFoo()
  {
    return "";
  }

  @EventHandler( CustomHandler.class )
  void handleFoo2( @Nonnull String i )
  {
  }
}
