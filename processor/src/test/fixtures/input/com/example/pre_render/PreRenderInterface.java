package com.example.pre_render;

import react4j.annotations.PreRender;

interface PreRenderInterface
{
  @PreRender
  default void preRender()
  {
  }
}
