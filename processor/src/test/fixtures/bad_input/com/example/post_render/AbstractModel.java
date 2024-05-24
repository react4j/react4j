package com.example.post_render;

import react4j.ReactNode;
import react4j.annotations.PostRender;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class AbstractModel
{
  @PostRender
  abstract void postRender();

  @Render
  ReactNode render()
  {
    return null;
  }
}
