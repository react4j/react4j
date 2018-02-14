package com.example.arez;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.ReactNode;

@ReactComponent
abstract class ComponentWithAnnotatedParameterEventHandler
  extends ReactArezComponent
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

  @Callback( CustomHandler2.class )
  @Nonnull
  String handleFoo()
  {
    return "";
  }

  @Callback( CustomHandler.class )
  void handleFoo2( @Nonnull String i )
  {
  }
}
