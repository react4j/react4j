package com.example.pre_render;

import react4j.ReactNode;
import react4j.annotations.PreRender;
import react4j.annotations.Render;
import react4j.annotations.View;
import org.jspecify.annotations.Nullable;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class MultiplePreRenderModel
{
  @PreRender
  void preRender1()
  {
  }

  @PreRender
  void preRender2()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
