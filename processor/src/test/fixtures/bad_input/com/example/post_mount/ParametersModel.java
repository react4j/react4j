package com.example.post_mount;

import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ParametersModel
{
  @PostMount
  void postMount( int x )
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
