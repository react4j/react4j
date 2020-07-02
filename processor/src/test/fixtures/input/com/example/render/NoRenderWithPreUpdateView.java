package com.example.render;

import react4j.annotations.PreUpdate;
import react4j.annotations.View;

@View( requireRender = false )
abstract class NoRenderWithPreUpdateView
{
  @PreUpdate
  void preUpdate()
  {
  }
}
