package com.example.post_render;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostRender;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ReturnsValueModel
  extends Component
{
  @PostRender
  int postRender()
  {
    return 0;
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
