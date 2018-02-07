package com.example.nested;

import react4j.annotations.ReactComponent;
import react4j.core.BaseContext;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

public class NestedReactComponent
{
  @ReactComponent
  static abstract class BasicReactComponent
    extends Component<BaseState, BaseContext>
  {
    @Override
    protected ReactNode render()
    {
      return null;
    }
  }
}
