package com.example.callback;

import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
public abstract class PublicComponentWithCallback
  extends Component<BaseState>
{
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Callback
  public void handleFoo()
  {
  }
}
