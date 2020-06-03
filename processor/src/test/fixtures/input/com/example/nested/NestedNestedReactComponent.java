package com.example.nested;

import react4j.ReactNode;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

public class NestedNestedReactComponent
{
  public static class DeepNesting
  {
    @ReactComponent
    static abstract class BasicReactComponent
    {
      @Render
      ReactNode render()
      {
        return null;
      }
    }
  }
}
