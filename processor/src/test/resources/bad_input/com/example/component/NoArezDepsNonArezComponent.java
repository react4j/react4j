package com.example.component;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@ReactComponent( allowNoArezDeps = true )
abstract class NoArezDepsNonArezComponent
  extends Component
{
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
