package com.example.basic;

import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
public abstract class PublicReactComponent
  extends Component<BaseState, BaseContext>
{
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
