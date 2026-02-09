package com.example.pre_render;

import com.example.pre_render.other.BaseProtectedAccessPreRenderModel;
import react4j.ReactNode;
import react4j.annotations.PreRender;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
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
