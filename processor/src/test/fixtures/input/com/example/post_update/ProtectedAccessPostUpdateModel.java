package com.example.post_update;

import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class ProtectedAccessPostUpdateModel
{
  @PostUpdate
  protected void postUpdate()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
