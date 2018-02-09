package com.example.component;

import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class AbstractMethodComponent
  extends Component<BaseState>
{
  @Override
  protected ReactNode render()
  {
    return null;
  }

  protected abstract void foo();
}
