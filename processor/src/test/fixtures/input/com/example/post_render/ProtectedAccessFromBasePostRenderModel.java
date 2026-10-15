package com.example.post_render;

import com.example.post_render.other.BaseProtectedAccessPostRenderModel;
import react4j.ReactNode;
import react4j.annotations.Render;
import react4j.annotations.View;
import org.jspecify.annotations.Nullable;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ProtectedAccessFromBasePostRenderModel
  extends BaseProtectedAccessPostRenderModel
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
