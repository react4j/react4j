package com.example.render;

import react4j.annotations.PreUpdate;
import react4j.annotations.View;

@View( type = View.Type.NO_RENDER )
abstract class NoRenderWithPreUpdateView
{
  @PreUpdate
  void preUpdate()
  {
  }
}
