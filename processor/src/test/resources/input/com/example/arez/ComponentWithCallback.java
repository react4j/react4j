package com.example.arez;

import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.ReactNode;

@ReactComponent
abstract class ComponentWithCallback
  extends ReactArezComponent
{
  @JsFunction
  public interface CustomHandler
  {
    int onMouseEvent( int i );
  }

  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Callback
  void handleFoo()
  {
  }

  // This is a custom handler that takes a parameter
  @Callback( CustomHandler.class )
  int handleFoo2( int i )
  {
    return 0;
  }
}
