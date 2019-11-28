package com.example.basic;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

@Deprecated
@ReactComponent
abstract class DeprecatedReactComponent
  extends Component
{
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
