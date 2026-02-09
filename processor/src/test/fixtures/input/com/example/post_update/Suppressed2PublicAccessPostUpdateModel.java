package com.example.post_update;

import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class Suppressed2PublicAccessPostUpdateModel
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:PublicMethod" )
  @PostUpdate
  public void postUpdate()
  {
  }

  @Nullable
  @Render
  ReactNode render()
  {
    return null;
  }
}
