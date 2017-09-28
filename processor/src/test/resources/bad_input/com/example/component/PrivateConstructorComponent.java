package com.example.component;

import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

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
