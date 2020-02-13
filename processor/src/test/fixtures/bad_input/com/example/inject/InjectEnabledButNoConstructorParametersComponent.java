package com.example.inject;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;

@ReactComponent( inject = Feature.ENABLE )
abstract class InjectEnabledButNoConstructorParametersComponent
  extends Component
{
  InjectEnabledButNoConstructorParametersComponent()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
