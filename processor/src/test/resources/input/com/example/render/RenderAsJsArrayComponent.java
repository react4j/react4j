package com.example.render;

import elemental2.core.Array;
import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
class RenderAsJsArrayComponent
  extends Component<BaseProps, BaseState>
{
  @Nullable
  @Override
  protected Array<ReactNode> renderAsJsArray()
  {
    return super.renderAsJsArray();
  }
}
