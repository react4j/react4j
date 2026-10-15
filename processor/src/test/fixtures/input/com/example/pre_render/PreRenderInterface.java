package com.example.pre_render;

import react4j.annotations.PreRender;

@org.jspecify.annotations.NullMarked
interface PreRenderInterface
{
  @PreRender
  default void preRender()
  {
  }
}
