package com.example.post_render;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.PostRender;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PostRenderAndPostMount
  extends Component
{
  @PostRender
  void postRender()
  {
  }

  @PostMount
  void postMount()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
