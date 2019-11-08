package com.example.inject;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class GenericTypeInjectedComponent<T>
  extends Component
{
  GenericTypeInjectedComponent( String someParam )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
