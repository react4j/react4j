package com.example.post_update;

import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;

@ReactComponent
abstract class ProtectedAccessPostUpdateModel
{
  @PostUpdate
  protected void postUpdate()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
