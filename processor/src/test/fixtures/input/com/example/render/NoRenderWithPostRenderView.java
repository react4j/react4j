package com.example.render;

import react4j.annotations.PostRender;
import react4j.annotations.View;

@View( type = View.Type.NO_RENDER )
abstract class NoRenderWithPostRenderView
{
  @PostRender
  void postRender()
  {
  }
}
