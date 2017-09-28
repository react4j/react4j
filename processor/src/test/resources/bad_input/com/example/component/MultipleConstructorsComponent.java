package com.example.component;

import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

@ReactComponent
class MultipleConstructorsComponent
  extends Component<BaseProps, BaseState>
{
  public MultipleConstructorsComponent( final String ignored )
  {
  }

  public MultipleConstructorsComponent()
  {
  }

  @Override
  protected ReactElement<?, ?> render()
  {
    return null;
  }
}
