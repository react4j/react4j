package com.example.render;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
class MultipleRenderComponent
  extends Component<BaseProps, BaseState, BaseContext>
{
  @Nullable
  @Override
  protected ReactNode render()
  {
    return super.render();
  }

  @Nullable
  @Override
  protected ReactNode[] renderAsArray()
  {
    return super.renderAsArray();
  }
}
