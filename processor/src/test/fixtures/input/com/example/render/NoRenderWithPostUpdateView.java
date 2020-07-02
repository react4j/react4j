package com.example.render;

import react4j.annotations.PostUpdate;
import react4j.annotations.View;

@View( requireRender = false )
abstract class NoRenderWithPostUpdateView
{
  @PostUpdate
  void postUpdate()
  {
  }
}
