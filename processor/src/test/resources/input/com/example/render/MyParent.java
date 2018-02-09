package com.example.render;

import javax.annotation.Nullable;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

abstract class MyParent
  extends Component<BaseState>
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return null;
  }
}
