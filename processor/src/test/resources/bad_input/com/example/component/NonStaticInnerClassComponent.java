package com.example.component;

import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.RenderElement;

final class NonStaticInnerClassComponent
{
  @ReactComponent
  class MyReactClassComponent
    extends Component<BaseProps, BaseState>
  {
    @Override
    protected RenderElement render()
    {
      return null;
    }
  }
}
