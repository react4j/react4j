package com.example.event_handler;

import jsinterop.annotations.JsFunction;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.RenderResult;

@ReactComponent
class HandlerNotFunctionalInterface2Component
  extends Component<BaseProps, BaseState>
{
  @JsFunction
  interface Foo
  {
    default void foo()
    {
    }

    default void foo2()
    {
    }
  }

  @Override
  protected RenderResult render()
  {
    return null;
  }

  @EventHandler( Foo.class )
  void handleFoo()
  {
  }
}
