package com.example.event_handler;

import jsinterop.annotations.JsFunction;
import react4j.annotations.EventHandler;
import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
class HandlerNotFunctionalInterfaceComponent
  extends Component<BaseProps, BaseState>
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
