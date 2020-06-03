package com.example.component;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

final class NonStaticInnerClassComponent
{
  @View
  abstract class MyReactClassComponent
  {
    @Render
    ReactNode render()
    {
      return null;
    }
  }
}
