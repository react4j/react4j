package com.example.inject;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ConstructorInjectComponent
  extends Component
{
  ConstructorInjectComponent( String someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
