package com.example.render;

import elemental2.core.JsArray;
import javax.annotation.Nullable;
import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

@ReactComponent
class RenderAsJsArrayComponent
  extends Component<BaseProps, BaseState, BaseContext>
{
  @Nullable
  @Override
  protected JsArray<ReactNode> renderAsJsArray()
  {
    return super.renderAsJsArray();
  }
}
