package com.example.component;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ConstructorWithParamComponent
  extends Component
{
  public ConstructorWithParamComponent( final String ignored )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
