package com.example.callback;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BadName2
  extends Component
{
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Callback( name = "long" )
  void handleFoo()
  {
  }
}
