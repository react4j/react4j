package com.example.post_render;

import com.example.post_render.other.BaseProtectedAccessPostRenderModel;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ProtectedAccessFromBasePostRenderModel
  extends BaseProtectedAccessPostRenderModel
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
