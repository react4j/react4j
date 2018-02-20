package com.example.callback;

import jsinterop.annotations.JsFunction;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class NotFunctionalInterface
  extends Component
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

  @Callback( Foo.class )
  void handleFoo()
  {
  }
}
