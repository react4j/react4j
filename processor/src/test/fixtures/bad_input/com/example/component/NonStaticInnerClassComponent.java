package com.example.component;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@org.jspecify.annotations.NullMarked
final class NonStaticInnerClassComponent
{
  @View( type = View.Type.STATEFUL )
  abstract class MyReactClassComponent
  {
    @Render
    ReactNode render()
    {
      return null;
    }
  }
}
