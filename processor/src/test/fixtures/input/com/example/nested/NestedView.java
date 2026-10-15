package com.example.nested;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import org.jspecify.annotations.Nullable;

@org.jspecify.annotations.NullMarked
public class NestedView
{
  @View( type = View.Type.STATEFUL )
  static abstract class BasicView
  {
    @Nullable
    @Render
    ReactNode render()
    {
      return null;
    }
  }
}
