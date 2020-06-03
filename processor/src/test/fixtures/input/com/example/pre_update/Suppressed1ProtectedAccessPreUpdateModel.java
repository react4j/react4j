package com.example.pre_update;

import react4j.ReactNode;
import react4j.annotations.PreUpdate;
import react4j.annotations.Render;
import react4j.annotations.View;

@View
abstract class Suppressed1ProtectedAccessPreUpdateModel
{
  // This uses the SOURCE retention suppression
  @SuppressWarnings( "React4j:ProtectedMethod" )
  @PreUpdate
  protected void preUpdate()
  {
  }

  @Render
  ReactNode render()
  {
    return null;
  }
}
