package com.example.pre_render;

import com.example.pre_render.other.BaseProtectedAccessPreRenderModel;
import react4j.ReactNode;
import react4j.annotations.PreRender;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ProtectedAccessFromBasePreRenderModel
  extends BaseProtectedAccessPreRenderModel
{
  @Render
  ReactNode render()
  {
    return null;
  }
}
