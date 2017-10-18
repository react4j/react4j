package com.example.component;

import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.RenderResult;

@ReactComponent
class PrivateConstructorComponent
  extends Component<BaseProps, BaseState>
{
  private PrivateConstructorComponent()
  {
  }

  @Override
  protected RenderResult render()
  {
    return null;
  }
}
