package com.example.nested;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

public class NestedView
{
  @View
  static abstract class BasicView
  {
    @Render
    ReactNode render()
    {
      return null;
    }
  }
}
