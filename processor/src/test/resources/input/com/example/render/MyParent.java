package com.example.render;

import javax.annotation.Nullable;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

class MyParent
  extends Component<BaseProps, BaseState, BaseContext>
{
  @Nullable
  @Override
  protected ReactNode[] renderAsArray()
  {
    return super.renderAsArray();
  }
}
