package com.example.nested;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

public class NestedReactComponent
{
  @ReactComponent
  static abstract class BasicReactComponent
    extends Component
  {
    @Override
    protected ReactNode render()
    {
      return null;
    }
  }
}
