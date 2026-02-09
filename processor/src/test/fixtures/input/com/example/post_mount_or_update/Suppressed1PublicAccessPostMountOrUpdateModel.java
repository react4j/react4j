package com.example.post_mount_or_update;

import react4j.ReactNode;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class Suppressed1PublicAccessPostMountOrUpdateModel
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:PublicMethod" )
  @PostMountOrUpdate
  public void postMountOrUpdate()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
