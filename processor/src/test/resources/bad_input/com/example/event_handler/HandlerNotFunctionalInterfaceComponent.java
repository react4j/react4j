package com.example.event_handler;

import jsinterop.annotations.JsFunction;
import react.annotations.EventHandler;
import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

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
  protected ReactElement<?, ?> render()
  {
    return null;
  }

  @EventHandler( Foo.class )
  void handleFoo()
  {
  }
}
