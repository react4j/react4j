package com.example.post_render;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostRender;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class DuplicateModel
  extends Component
{
  @PostRender
  void postRender()
  {
  }

  @PostRender
  void postRender2()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
