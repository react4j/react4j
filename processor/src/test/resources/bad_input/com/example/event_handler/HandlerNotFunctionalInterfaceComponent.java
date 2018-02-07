package com.example.event_handler;

import jsinterop.annotations.JsFunction;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class HandlerNotFunctionalInterfaceComponent
  extends Component<BaseState, BaseContext>
{
  @JsFunction
  interface Foo
  {
    void foo();

    void foo2();
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }

  @EventHandler( Foo.class )
  void handleFoo()
  {
  }
}
