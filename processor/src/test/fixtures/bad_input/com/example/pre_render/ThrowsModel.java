package com.example.pre_render;

import react4j.ReactNode;
import react4j.annotations.PreRender;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ThrowsModel
{
  @PreRender
  void preRender()
    throws Exception
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
