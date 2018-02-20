package com.example.component;

import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent( name = "-abc" )
abstract class BadNameComponent
  extends Component
{
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
