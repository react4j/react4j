package com.example.post_update;

import react4j.ReactNode;
import react4j.annotations.PostUpdate;
import react4j.annotations.ReactComponent;
import react4j.annotations.Render;
import react4j.annotations.SuppressReact4jWarnings;

@ReactComponent
abstract class Suppressed2ProtectedAccessPostUpdateModel
{
  // This uses the CLASS retention suppression
  @SuppressReact4jWarnings( "React4j:ProtectedMethod" )
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
