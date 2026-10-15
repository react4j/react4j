package com.example.post_render;

import react4j.ReactNode;
import react4j.annotations.PostRender;
import react4j.annotations.Render;
import react4j.annotations.View;
import org.jspecify.annotations.Nullable;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class MultiplePostRenderModel
{
  @PostRender
  void postRender1()
  {
  }

  @PostRender
  void postRender2()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
