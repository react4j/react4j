package com.example.component;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

final class NonStaticInnerClassComponent
{
  @ReactComponent
  abstract class MyReactClassComponent
  {
    @Render
    ReactNode render()
    {
      return null;
    }
  }
}
