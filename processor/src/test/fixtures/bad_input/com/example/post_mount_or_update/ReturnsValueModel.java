package com.example.post_mount_or_update;

import react4j.ReactNode;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class ReturnsValueModel
{
  @PostMountOrUpdate
  int postMountOrUpdate()
  {
    return 0;
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
