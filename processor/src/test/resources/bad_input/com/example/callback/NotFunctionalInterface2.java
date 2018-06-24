package com.example.callback;

import jsinterop.annotations.JsFunction;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class NotFunctionalInterface2
  extends Component
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
  protected ReactNode render()
  {
    return null;
  }

  @Callback( Foo.class )
  void handleFoo()
  {
  }
}
