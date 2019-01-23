package com.example.component;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
class ConcreteComponent
  extends Component
{
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Override
  protected int getComponentId()
  {
    return 0;
  }

  @Override
  protected String getComponentName()
  {
    return null;
  }
}
