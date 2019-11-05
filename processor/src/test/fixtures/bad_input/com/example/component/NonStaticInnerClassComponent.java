package com.example.component;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.ReactComponent;

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
