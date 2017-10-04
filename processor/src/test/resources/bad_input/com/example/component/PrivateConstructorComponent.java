package com.example.component;

import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactElement;

@ReactComponent
class PrivateConstructorComponent
  extends Component<BaseProps, BaseState>
{
  private PrivateConstructorComponent()
  {
  }

  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }
}
