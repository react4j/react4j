package com.example.post_update.other;

import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.Render;

public abstract class BaseProtectedAccessPostUpdateModel
{
  @PostUpdate
  protected void postUpdate()
  {
  }

  @Render
  protected ReactNode render()
  {
    return null;
  }
}
