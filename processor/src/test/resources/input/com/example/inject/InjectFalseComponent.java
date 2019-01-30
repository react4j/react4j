package com.example.inject;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Feature;
import react4j.annotations.ReactComponent;

@ReactComponent( inject = Feature.DISABLE )
abstract class InjectFalseComponent
  extends Component
{
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
