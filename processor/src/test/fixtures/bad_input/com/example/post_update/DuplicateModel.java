package com.example.post_update;

import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class DuplicateModel
{
  @PostUpdate
  void postUpdate()
  {
  }

  @PostUpdate
  void postUpdate2()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
