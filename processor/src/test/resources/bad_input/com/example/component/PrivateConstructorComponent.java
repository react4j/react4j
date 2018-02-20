package com.example.component;

import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

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
