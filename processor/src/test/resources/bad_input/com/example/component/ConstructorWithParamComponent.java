package com.example.component;

import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

@ReactComponent
class ConstructorWithParamComponent
  extends Component<BaseProps, BaseState>
{
  public ConstructorWithParamComponent( final String ignored )
  {
  }

  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }
}
