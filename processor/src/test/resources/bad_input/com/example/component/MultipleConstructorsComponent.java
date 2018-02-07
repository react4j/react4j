package com.example.component;

import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
abstract class MultipleConstructorsComponent
  extends Component<BaseState, BaseContext>
{
  public MultipleConstructorsComponent( final String ignored )
  {
  }

  public MultipleConstructorsComponent()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
