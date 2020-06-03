package com.example.post_mount_or_update;

import java.io.IOException;
import react4j.ReactNode;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ThrowsModel
{
  @PostMountOrUpdate
  void postMountOrUpdate()
    throws IOException
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
