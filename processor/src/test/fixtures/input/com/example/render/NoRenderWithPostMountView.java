package com.example.render;

import react4j.annotations.PostMount;
import react4j.annotations.View;

@View( requireRender = false )
abstract class NoRenderWithPostMountView
{
  @PostMount
  void postMount()
  {
  }
}
