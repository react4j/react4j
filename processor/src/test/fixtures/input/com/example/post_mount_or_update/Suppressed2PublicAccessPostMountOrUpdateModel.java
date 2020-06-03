package com.example.post_mount_or_update;

import react4j.ReactNode;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;

@View
abstract class Suppressed2PublicAccessPostMountOrUpdateModel
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:PublicMethod" )
  @PostMountOrUpdate
  public void postMountOrUpdate()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
