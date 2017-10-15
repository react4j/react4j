package com.example.arez;

import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactElement;

@ReactComponent
class ComponentWithEventHandler
  extends ReactArezComponent<BaseProps, BaseState>
{
  @JsFunction
  public interface CustomHandler
  {
    int onMouseEvent( int i );
  }

  @Nullable
  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }

  @EventHandler
  void handleFoo()
  {
  }

  // This is a custom handler that takes a parameter
  @EventHandler( CustomHandler.class )
  int handleFoo2( int i )
  {
    return 0;
  }
}
