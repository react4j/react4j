package com.example.post_update;

import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class Suppressed1ProtectedAccessPostUpdateModel
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:ProtectedMethod" )
  @PostUpdate
  protected void postUpdate()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
