package com.example.post_mount;

import react4j.ReactNode;
import react4j.annotations.PostMount;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class Suppressed2PublicAccessPostMountModel
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:PublicMethod" )
  @PostMount
  public void postMount()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
