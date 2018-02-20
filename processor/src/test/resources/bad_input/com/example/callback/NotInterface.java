package com.example.callback;

import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class NotInterface
  extends Component
{
  static abstract class Foo
  {
    abstract void foo();
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
