package com.example.event_handler;

import jsinterop.annotations.JsFunction;
import react.annotations.EventHandler;
import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

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
  protected ReactElement<?, ?> render()
  {
    return null;
  }

  @EventHandler( Foo.class )
  void handleFoo()
  {
  }
}
