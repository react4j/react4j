package com.example.component;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class MultipleConstructorsComponent
  extends Component
{
  MultipleConstructorsComponent( final String ignored )
  {
  }

  MultipleConstructorsComponent()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
