package com.example.post_render;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostRender;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class PublicModel
  extends Component
{
  @PostRender
  public void postRender()
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
