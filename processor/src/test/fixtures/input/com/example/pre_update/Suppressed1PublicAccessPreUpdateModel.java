package com.example.pre_update;

import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class Suppressed1PublicAccessPreUpdateModel
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:PublicMethod" )
  @PreUpdate
  public void preUpdate()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
