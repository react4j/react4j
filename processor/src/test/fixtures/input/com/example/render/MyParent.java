package com.example.render;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;

@org.jspecify.annotations.NullMarked
abstract class MyParent
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
