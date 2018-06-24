package com.example.component;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class MultipleConstructorsComponent
  extends Component
{
  public MultipleConstructorsComponent( final String ignored )
  {
  }

  public MultipleConstructorsComponent()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
