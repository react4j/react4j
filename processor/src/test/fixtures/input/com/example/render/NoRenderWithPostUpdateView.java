package com.example.render;

import react4j.annotations.PostUpdate;
import react4j.annotations.View;

@View( type = View.Type.NO_RENDER )
abstract class NoRenderWithPostUpdateView
{
  @PostUpdate
  void postUpdate()
  {
  }
}
