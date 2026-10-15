package com.example.basic;

import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import org.jspecify.annotations.Nullable;

@View( exportBuilder = true, type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
public abstract class PublicView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
