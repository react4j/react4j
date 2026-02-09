package com.example.nested;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

public class NestedNestedView
{
  public static class DeepNesting
  {
    @View
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
}
