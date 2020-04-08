package com.example.basic;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
public abstract class FinalMethodInReactComponent
  extends Component
{
  final void someMethod()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
