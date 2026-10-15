package com.example.package_access.other;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;

@org.jspecify.annotations.NullMarked
public abstract class BaseRenderModel
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
