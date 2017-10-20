package com.example.arez;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.arez.ReactArezComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.ReactNode;

@ReactComponent
class BasicArezReactComponent
  extends ReactArezComponent<BaseProps, BaseState>
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
