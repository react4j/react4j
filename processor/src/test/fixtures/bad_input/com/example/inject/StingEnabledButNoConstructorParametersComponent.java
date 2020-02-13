package com.example.inject;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;

@ReactComponent( sting = Feature.ENABLE )
abstract class StingEnabledButNoConstructorParametersComponent
  extends Component
{
  StingEnabledButNoConstructorParametersComponent()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
