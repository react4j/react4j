package com.example.post_render;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostRender;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PrivateModel
  extends Component
{
  @PostRender
  private void postRender()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
