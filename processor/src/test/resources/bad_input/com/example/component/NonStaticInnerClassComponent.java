package com.example.component;

import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

final class NonStaticInnerClassComponent
{
  @ReactComponent
  class MyReactClassComponent
    extends Component<BaseProps, BaseState>
  {
    @Override
    protected ReactElement<?, ?> render()
    {
      return null;
    }
  }
}
