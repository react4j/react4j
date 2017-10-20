package com.example.render;

import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.RenderElement;

@ReactComponent
class MultipleRenderComponent
  extends Component<BaseProps, BaseState>
{
  @Nullable
  @Override
  protected RenderElement render()
  {
    return super.render();
  }

  @Nullable
  @Override
  protected String renderAsString()
  {
    return super.renderAsString();
  }
}
