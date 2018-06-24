package com.example.callback;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;

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
