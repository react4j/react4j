package com.example.post_render;

import react4j.annotations.PostRender;

interface PostRenderInterface
{
  @PostRender
  default void postRender()
  {
  }
}
