package com.example.post_render;

import react4j.annotations.PostRender;

@org.jspecify.annotations.NullMarked
interface PostRenderInterface
{
  @PostRender
  default void postRender()
  {
  }
}
