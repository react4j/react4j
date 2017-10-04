package com.example.nested;

import react4j.annotations.ReactComponent;
import react4j.core.BaseProps;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactElement;

public class NestedNestedReactComponent
{
  public static class DeepNesting
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
}
