package com.example.nested;

import react.annotations.ReactComponent;
import react.core.BaseProps;
import react.core.BaseState;
import react.core.Component;
import react.core.ReactElement;

public class NestedReactComponent
{
  @ReactComponent
  static class BasicReactComponent
    extends Component<BaseProps, BaseState>
  {
    @Override
    protected ReactElement<?, ?> render()
    {
      return null;
    }
  }
}
