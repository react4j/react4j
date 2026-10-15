package com.example.render;

import react4j.annotations.PreUpdate;
import react4j.annotations.View;

@View( type = View.Type.NO_RENDER )
@org.jspecify.annotations.NullMarked
abstract class NoRenderWithPreUpdateView
{
  @PreUpdate
  void preUpdate()
  {
  }
}
