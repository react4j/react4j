package com.example.pre_render;

import react4j.ReactNode;
import react4j.annotations.PreRender;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class PackageAccessPreRenderModel
{
  @PreRender
  void preRender()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
