package com.example.post_mount_or_update.other;

import react4j.ReactNode;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.Render;

public abstract class BaseProtectedAccessPostMountOrUpdateModel
{
  @PostMountOrUpdate
  protected void postMountOrUpdate()
  {
  }

  @Render
  protected ReactNode render()
  {
    return null;
  }
}
