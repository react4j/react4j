package com.example.basic;

import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class BasicReactComponent
  extends Component<BaseState>
{
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
