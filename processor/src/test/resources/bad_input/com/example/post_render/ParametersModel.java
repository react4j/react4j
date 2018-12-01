package com.example.post_render;

import react4j.Component;
import react4j.ReactNode;
import react4j.annotations.PostRender;
import react4j.annotations.ReactComponent;

@ReactComponent
abstract class ParametersModel
  extends Component
{
  @PostRender
  void postRender( int x )
  {
  }

  @Override
  protected ReactNode render()
  {
    return null;
  }
}
