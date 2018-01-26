package com.example.component;

import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
class ConcreteComponent
  extends Component<BaseProps, BaseState, BaseContext>
{
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
