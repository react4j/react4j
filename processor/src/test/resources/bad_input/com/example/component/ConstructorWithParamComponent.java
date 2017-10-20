package com.example.component;

import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.RenderElement;

@ReactComponent
class ConstructorWithParamComponent
  extends Component<BaseProps, BaseState>
{
  public ConstructorWithParamComponent( final String ignored )
  {
  }

  @Override
  protected RenderElement render()
  {
    return null;
  }
}
