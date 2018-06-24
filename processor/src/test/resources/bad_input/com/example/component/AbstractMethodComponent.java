package com.example.component;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class AbstractMethodComponent
  extends Component
{
  @Override
  protected ReactNode render()
  {
    return null;
  }

  protected abstract void foo();
}
