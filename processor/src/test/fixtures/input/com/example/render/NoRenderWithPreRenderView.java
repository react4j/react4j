package com.example.render;

import react4j.annotations.PreRender;
import react4j.annotations.PreUpdate;
import react4j.annotations.View;

@View( type = View.Type.NO_RENDER )
abstract class NoRenderWithPreRenderView
{
  @PreRender
  void preRender()
  {
  }
}
