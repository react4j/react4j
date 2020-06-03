package com.example.package_access.other;

import javax.annotation.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;

public abstract class BaseRenderModel
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
