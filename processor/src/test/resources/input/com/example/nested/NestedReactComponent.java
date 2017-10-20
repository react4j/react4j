package com.example.nested;

import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

public class NestedReactComponent
{
  @ReactComponent
  static class BasicReactComponent
    extends Component<BaseProps, BaseState>
  {
    @Override
    protected ReactNode render()
    {
      return null;
    }
  }
}
