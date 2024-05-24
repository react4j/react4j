package com.example.pre_render.other;

import react4j.ReactNode;
import react4j.annotations.PreRender;
import react4j.annotations.Render;
import react4j.annotations.View;

public abstract class BaseProtectedAccessPreRenderModel
{
  @PreRender
  protected void preRender()
  {
  }
}
