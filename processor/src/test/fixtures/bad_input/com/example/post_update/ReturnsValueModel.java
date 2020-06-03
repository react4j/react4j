package com.example.post_update;

import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class ReturnsValueModel
{
  @PostUpdate
  int postUpdate()
  {
    return 0;
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
