package com.example.callback;

import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

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
