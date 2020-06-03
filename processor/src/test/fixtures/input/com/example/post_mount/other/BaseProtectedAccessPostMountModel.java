package com.example.post_mount.other;

import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;

public abstract class BaseProtectedAccessPostMountModel
{
  @PostMount
  protected void postMount()
  {
  }

  @Render
  protected ReactNode render()
  {
    return null;
  }
}
