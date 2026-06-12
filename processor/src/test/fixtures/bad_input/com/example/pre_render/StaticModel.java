package com.example.pre_render;

import react4j.ReactNode;
import react4j.annotations.PreRender;
import react4j.annotations.Render;
import react4j.annotations.View;

@View( type = View.Type.STATEFUL )
abstract class StaticModel
{
  @PreRender
  static void preRender( )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
