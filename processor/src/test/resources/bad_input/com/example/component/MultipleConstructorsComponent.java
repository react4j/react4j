package com.example.component;

import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

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
