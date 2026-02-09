package com.example.post_mount.other;

import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import javax.annotation.Nullable;

public abstract class BaseProtectedAccessPostMountModel
{
  @PostMount
  protected void postMount()
  {
  }

  @Nullable
  @Render
  protected ReactNode render()
  {
    return null;
  }
}
