package com.example.post_render;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostRender;
import react4j.annotations.PreUpdate;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class BasicModel
  extends Component
{
  @PostRender
  void postRender()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
