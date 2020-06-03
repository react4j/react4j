package com.example.post_mount_or_update;

import react4j.ReactNode;
import react4j.annotations.PostMountOrUpdate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;

@ReactComponent
abstract class Suppressed2ProtectedAccessPostMountOrUpdateModel
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:ProtectedMethod" )
  @PostMountOrUpdate
  protected void postMountOrUpdate()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
