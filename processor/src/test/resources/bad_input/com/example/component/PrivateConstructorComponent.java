package com.example.component;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PrivateConstructorComponent
  extends Component
{
  private PrivateConstructorComponent()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
