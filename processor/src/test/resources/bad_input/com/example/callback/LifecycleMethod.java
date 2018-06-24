package com.example.callback;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.Callback;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class LifecycleMethod
  extends Component
{
  @Override
  protected ReactNode render()
  {
    return null;
  }

  @Callback
  @Override
  protected void componentDidMount()
  {
    super.componentDidMount();
  }
}
