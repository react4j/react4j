package com.example.pre_render;

import com.example.pre_render.other.BaseProtectedAccessPreRenderModel;
import react4j.ReactNode;
import react4j.annotations.PreRender;
import react4j.annotations.Render;
import react4j.annotations.View;
import org.jspecify.annotations.Nullable;

@View( type = View.Type.STATEFUL )
@org.jspecify.annotations.NullMarked
abstract class ProtectedAccessFromBasePreRenderModel
  extends BaseProtectedAccessPreRenderModel
{
  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
