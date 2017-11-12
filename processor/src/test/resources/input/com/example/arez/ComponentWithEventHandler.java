package com.example.arez;

import javax.annotation.Nullable;
import jsinterop.annotations.JsFunction;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactNode;

@ReactComponent
class ComponentWithEventHandler
  extends ReactArezComponent<BaseProps, BaseState, BaseContext>
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
