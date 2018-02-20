package com.example.component;

import react4j.annotations.ReactComponent;
import react4j.core.Component;
import react4j.core.ReactNode;

final class NonStaticInnerClassComponent
{
  @ReactComponent
  abstract class MyReactClassComponent
    extends Component
  {
    @Override
    protected ReactNode render()
    {
      return null;
    }
  }
}
