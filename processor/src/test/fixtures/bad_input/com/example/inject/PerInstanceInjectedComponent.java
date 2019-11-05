package com.example.inject;

import arez.annotations.PerInstance;
import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PerInstanceInjectedComponent
  extends Component
{
  PerInstanceInjectedComponent( @PerInstance final String someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
