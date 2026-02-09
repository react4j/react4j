package com.example.post_update;

import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;
import javax.annotation.Nullable;

@View
abstract class Suppressed1PublicAccessPostUpdateModel
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:PublicMethod" )
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
