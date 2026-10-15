package com.example.basic;

import org.jspecify.annotations.Nullable;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( exportBuilder = true, type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ExportedBuilderView
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
