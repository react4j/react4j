package com.example.nested;

import react4j.annotations.ReactComponent;
import react4j.core.BaseState;
import react4j.core.Component;
import react4j.core.ReactNode;

public class NestedNestedReactComponent
{
  public static class DeepNesting
  {
    @ReactComponent
    static abstract class BasicReactComponent
      extends Component<BaseState>
    {
      @Override
      protected ReactNode render()
      {
        return null;
      }
    }
  }
}
