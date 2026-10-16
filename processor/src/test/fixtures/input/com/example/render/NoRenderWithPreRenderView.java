package com.example.render;

import react4j.annotations.PreRender;
import react4j.annotations.PreUpdate;
import react4j.annotations.View;
import react4j.annotations.Publish;

@View( type = View.Type.NO_RENDER )
@org.jspecify.annotations.NullMarked
abstract class NoRenderWithPreRenderView
{
  @PreRender
  void preRender()
  {
  }
  @Publish
  String contextValue()
  {
    return "context";
  }
}
