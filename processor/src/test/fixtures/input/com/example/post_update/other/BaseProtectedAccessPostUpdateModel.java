package com.example.post_update.other;

import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.Render;
import org.jspecify.annotations.Nullable;

@org.jspecify.annotations.NullMarked
public abstract class BaseProtectedAccessPostUpdateModel
{
  @PostUpdate
  protected void postUpdate()
  {
  }

  @Nullable
  @Render
  protected ReactNode render()
  {
    return null;
  }
}
